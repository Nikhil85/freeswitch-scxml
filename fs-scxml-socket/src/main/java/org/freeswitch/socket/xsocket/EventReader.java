package org.freeswitch.socket.xsocket;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freeswitch.adapter.api.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.INonBlockingConnection;

/**
 *
 * @author jocke
 */
public class EventReader {

    private static final Logger LOG = LoggerFactory.getLogger(EventReader.class);
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
    public Event readEvent(final INonBlockingConnection connection) throws IOException {

        if (noDataAvailable(connection)) {
            return null;
        }

        String header = connection.readStringByDelimiter(LINE_BREAKS);

        if (isCommandReply(header) || isDisconnectNotice(header)) {
            return null;
        }

        String body = readEventByHeader(header, connection);

        if (body != null) {
            return Event.fromData(body);
        
        } else {
            return null;
        }

    }

    private String readEventByHeader(String header, INonBlockingConnection connection) throws IOException {

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

    private String readEventByLength(String header, final INonBlockingConnection connection)
            throws IOException, NumberFormatException, BufferUnderflowException {
        String content = null;
        Matcher matcher = CONTENT_LENGTH_PATTERN.matcher(header);
        if (matcher.find()) {
            int length = Integer.parseInt(matcher.group(1));
            content = connection.readStringByLength(length, UTF8);
            LOG.trace("content ==========\n{}=========", content);
        }

        return content;
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
}
