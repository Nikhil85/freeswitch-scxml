package org.freeswitch.socket.xsocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventName;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.adapter.api.SessionFactory;
import org.freeswitch.scxml.ApplicationLauncher;
import org.freeswitch.scxml.ThreadPoolManager;
import org.freeswitch.socket.ServerSessionListener;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public final class EventSocketHandler implements IDataHandler, IDisconnectHandler, IConnectHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(EventSocketHandler.class);
    private static final Pattern CONTENT_LENGTH_PATTERN = Pattern.compile("Content-Length:\\s(\\d*)", Pattern.MULTILINE);
    private static final Pattern EVENT_IN_CONTENT_PATTERN = Pattern.compile("Content-Type: text/event-plain", Pattern.MULTILINE);
    private static final Pattern EVENT_PATTERN = Pattern.compile("Event-Name:\\s(.*)", Pattern.MULTILINE);
    private static final String UTF8 = "UTF-8";

    /**
     *
     * Create a new XsocketSessionManager.
     *
     * @param poolManager   Handles Thread pools.
     * @param appLauncher   Launches SCXML applications.
     * @param recordingPath The path to save recordings.
     *
     */
    public EventSocketHandler() {
        
    }

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
     * @return true
     */
    @Override
    public boolean onData(final INonBlockingConnection connection) {
        
        try {
            
            if (connection.available() == -1) {
                LOG.debug("no data available, Channel has reached End Of Stream");
                return true;
            }
            
            String header = connection.readStringByDelimiter("\n\n");            
            if (commandReply(header) || disconnectNotice(header)) {
                return true;
            }
            
            String content = null;
            
            Matcher matcher = CONTENT_LENGTH_PATTERN.matcher(header);
            if (matcher.find()) {    
                int length = Integer.parseInt(matcher.group(1));
                // !! Do not catch BufferUnderflowException !!
                content = connection.readStringByLength(length, UTF8);
                LOG.trace("content ==========\n{}=========", content);
            }
            
            
            String eventdata = null;
            
            if (EVENT_PATTERN.matcher(header).find()) {
                eventdata = header;
            } else if (EVENT_IN_CONTENT_PATTERN.matcher(header).find()) {
                eventdata = content;
            } else {
                LOG.warn("Unknown socket data:\nheader=\n{}\ncontent=\n{}\n", header, content);
                return false;
            }
            
            ServerSessionListener session = (ServerSessionListener) connection.getAttachment();
            
            if (session != null) {
                // old connection
                session.onDataEvent(eventdata);
            } else {
                LOG.debug("New Connection so prepare the call to launch");
                
                INonBlockingConnection syncConnection = ConnectionUtils.synchronizedConnection(connection);
                
                XSocketSocketWriter socketWriter = new XSocketSocketWriter(syncConnection);
                BlockingQueue<Event> eventQueue = new ArrayBlockingQueue<Event>(50);
                final XsocketServerSession serverSession = new XsocketServerSession(eventQueue, socketWriter);
                
                Map<String, Object> channelVars = extractDataToMap(eventdata);
                channelVars.put(BlockingQueue.class.getName(), eventQueue);
                channelVars.put(CommandExecutor.class.getName(), socketWriter);
                
                SessionFactory factory = Lookup.getDefault().lookup(SessionFactory.class);
                
                if (factory == null) {
                    LOG.warn("No factory found in lookup ");
                    return false;
                }
                
                final Session fss = factory.create(channelVars);
                LOG.debug("Session Ready");
                // save reference to serverSessionListener
                syncConnection.setAttachment(serverSession);
                
                Runnable appRunner = new Runnable()   {
                    
                    @Override
                    public void run() {
                        try {
                            ApplicationLauncher applicationLauncher = Lookup.getDefault().lookup(ApplicationLauncher.class);
                            applicationLauncher.launch(fss);
                        } catch (Exception ex) {
                            LOG.error("Application Runnder Thread died \n", ex);
                        }
                        
                        fss.hangup();
                    }
                };
                
                LOG.info("launch applicationLauncher in new thread");
                ThreadPoolManager manager = Lookup.getDefault().lookup(ThreadPoolManager.class);
                manager.getWorkerPool().execute(appRunner);
            }
            
        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex.getMessage());
            
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
        
        return true;
    }

    private boolean disconnectNotice(String header) {
        return header.startsWith("Content-Type: text/disconnect-notice");
    }

    private boolean commandReply(String header) {
        return header.startsWith("Content-Type: command/reply");
    }
    
    @Override
    public boolean onConnect(INonBlockingConnection connection) {
        try {
            LOG.debug("onConnect:  connection[{}] {} bytes available", connection, connection.available());
            connection.write("connect\n\n");
            connection.write("myevents\n\n");
            connection.write("filter Event-Name " + EventName.CHANNEL_EXECUTE_COMPLETE + "\n\n");
            connection.write("filter Event-Name " + EventName.DTMF + "\n\n");
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
    
    private Map<String, Object> extractDataToMap(final String data) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        String trimedData = data.replaceAll(":", "");
        Scanner scanner = new Scanner(trimedData);
        while (scanner.hasNext()) {
            String key = null;
            String value = null;
            try {
                key = scanner.next();
                value = scanner.next();
                map.put(key, URLDecoder.decode(value, "UTF-8"));
            } catch (Exception ex) {
                LOG.warn("Error while adding {}={} to data map", key, value);
            }
        }
        return map;
    }
}
