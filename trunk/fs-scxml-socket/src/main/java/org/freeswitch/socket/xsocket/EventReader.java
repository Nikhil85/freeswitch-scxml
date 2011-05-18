package org.freeswitch.socket.xsocket;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public String readEvent(final INonBlockingConnection connection) throws IOException {

        if (noDataAvailable(connection)) {
            return null;
        }

        String header = connection.readStringByDelimiter(LINE_BREAKS);

        if (isCommandReply(header) || isDisconnectNotice(header)) {
            return null;
        } else if (isError(header)) {
            return "CHANNEL_ERROR";
        }

        return readEventByHeader(header, connection);

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
            // !! Do not catch BufferUnderflowException !!
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

    private boolean isError(String header) {
        return header.contains("-ERR");
    }
}
