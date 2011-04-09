package org.freeswitch.socket.xsocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.ConnectionUtils;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.adapter.api.SessionFactory;
import org.freeswitch.scxml.ThreadPoolManager;
import org.freeswitch.socket.ServerSessionListener;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public final class EventSocketHandler implements IDataHandler, IDisconnectHandler, IConnectHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(EventSocketHandler.class);
    static final String LINE_BREAKS = "\n\n";
    static final String COMMAND_REPLY = "Content-Type: command/reply";
    static final String DISCONNECT_NOTICE = "Content-Type: text/disconnect-notice";
    private static final Pattern CONTENT_LENGTH_PATTERN = Pattern.compile("Content-Length:\\s(\\d*)", Pattern.MULTILINE);
    private static final Pattern EVENT_IN_CONTENT_PATTERN = Pattern.compile("Content-Type: text/event-plain", Pattern.MULTILINE);
    private static final Pattern EVENT_PATTERN = Pattern.compile("Event-Name:\\s(.*)", Pattern.MULTILINE);
    public static final String UTF8 = "UTF-8";

    /**
     * This was taken from the FreeSwitch WIKI.
     *
     * <b>Pseudo code</b>
     * <ol>
     * <li> Look for \n\n in your receive buffer </li>
     * <li> Examine data for existence of Content-Length </li>
     * <li> If NOT present, process event and remove from receive buffer </li>
     * <li> IF present, Shift buffer to remove 'header'
     *      Evaluate content-length value </li>
     * <li> Loop until receive buffer size is >= Content-length
     *      Extract content-length bytes from buffer and process </li>
     * </ol>
     * </p>
     *
     * @param connection The connection to read and write to.
     *
     * @return true if all went well false otherwise.
     */
    @Override
    public boolean onData(final INonBlockingConnection connection) throws IOException {

        if (noDataAvailable(connection)) {
            return true;
        }

        String header = connection.readStringByDelimiter(LINE_BREAKS);

        if (isCommandReply(header) || isDisconnectNotice(header)) {
            return true;
        }

        String evt = readEvent(header, connection);

        if (evt == null) {
            return true;
        }

        ServerSessionListener session = (ServerSessionListener) connection.getAttachment();

        if (session != null) {
            session.onDataEvent(evt);

        } else {
            initSession(evt, ConnectionUtils.synchronizedConnection(connection));
        }

        return true;
    }

    private void initSession(String evt, final INonBlockingConnection connection) throws UnsupportedEncodingException {
        LOG.debug("New Connection so prepare the call to launch");

        XSocketSocketWriter socketWriter = new XSocketSocketWriter(connection);

        BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<Event>(50);
        final XsocketServerSession serverSession = new XsocketServerSession(eventQueue, socketWriter);
        connection.setAttachment(serverSession);

        SessionFactory factory = Lookup.getDefault().lookup(SessionFactory.class);

        if (factory == null) {
            LOG.warn("No factory found in lookup ");
            return;
        }

        final Session fss = factory.create(createVars(evt, eventQueue, socketWriter));
        runApplication(new ApplicationRunner(fss));
    }

    private Map<String, Object> createVars(String evt, BlockingQueue<Event> eventQueue, XSocketSocketWriter socketWriter) throws UnsupportedEncodingException {
        Map<String, Object> channelVars = extractDataToMap(evt);
        channelVars.put(BlockingQueue.class.getName(), eventQueue);
        channelVars.put(CommandExecutor.class.getName(), socketWriter);
        channelVars.put(ScheduledExecutorService.class.getName(), Lookup.getDefault().lookup(ThreadPoolManager.class).getScheduler());
        return channelVars;
    }

    private void runApplication(Runnable appRunner) {
        LOG.info("launch application in new thread");
        Lookup.getDefault().lookup(ThreadPoolManager.class).getWorkerPool().execute(appRunner);
    }

    private String readEventByLength(String header, final INonBlockingConnection connection)
            throws IOException, NumberFormatException, BufferUnderflowException {

        String content = null;

        Matcher matcher = CONTENT_LENGTH_PATTERN.matcher(header);

        if (matcher.find()) {
            int length = Integer.parseInt(matcher.group(1));
            // !! Do not catch BufferUnderflowException !!
            content = connection.readStringByLength(length, UTF8);
            LOG.trace("content ==========\n{}=========", content);
        }

        return content;
    }

    private String readEvent(String header, INonBlockingConnection connection) throws IOException {

        String evt = null;

        if (isContentLength(header)) {
            evt = readEventByLength(header, connection);

        } else if (isEvent(header)) {
            evt = header;

        } else if (isEventPlain(header)) {
            evt = header;

        } else {
            LOG.warn("Unknown socket data:\nheader=\n{}\ncontent=\n{}\n", header, evt);
        }

        return evt;
    }

    private Map<String, Object> extractDataToMap(final String data) throws UnsupportedEncodingException {

        Map<String, Object> map = new HashMap<String, Object>();
        Scanner scanner = new Scanner(data.replaceAll(":", ""));

        while (scanner.hasNext()) {            
            try {
                map.put(scanner.next(), URLDecoder.decode(scanner.next(), UTF8));
            } catch(NoSuchElementException nsee) {
                LOG.error(nsee.getMessage());
            }
        }

        return map;
    }

    private boolean noDataAvailable(final INonBlockingConnection connection) throws IOException {

        if (connection.available() == -1) {
            LOG.debug("no data available, Channel has reached End Of Stream");
            return true;
        }

        return false;
    }

    private boolean isContentLength(String header) {
        return CONTENT_LENGTH_PATTERN.matcher(header).find();
    }

    private boolean isEvent(String header) {
        return EVENT_PATTERN.matcher(header).find();
    }

    private boolean isEventPlain(String header) {
        return EVENT_IN_CONTENT_PATTERN.matcher(header).find();
    }

    private boolean isDisconnectNotice(String header) {
        return header.startsWith(DISCONNECT_NOTICE);
    }

    private boolean isCommandReply(String header) {
        return header.startsWith(COMMAND_REPLY);
    }

    @Override
    public boolean onConnect(INonBlockingConnection connection) {
        try {
            LOG.debug("onConnect:  connection[{}] {} bytes available", connection, connection.available());
            connection.write("connect\n\n");
            connection.write("myevents\n\n");
            connection.write("filter Event-Name " + Event.CHANNEL_EXECUTE_COMPLETE + "\n\n");
            connection.write("filter Event-Name " + Event.DTMF + "\n\n");
        } catch (Exception ex) {
            LOG.error("Oops! onConnect error.", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean onDisconnect(INonBlockingConnection connection) {
        try {
            LOG.debug("onDisconnect:  connection[{}] {} bytes available", connection, connection.available());

            ServerSessionListener fss = (ServerSessionListener) connection.getAttachment();

            if (fss == null) {
                LOG.warn("A connection was set up, but no session was created.");

            } else {
                fss.onClose();
            }

        } catch (Exception ex) {
            LOG.error("Oops! onDisconnect error.", ex);
            return false;
        }
        return true;
    }
}
