package org.freeswitch.socket.xsocket;


import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freeswitch.adapter.CommandExecutor;
import org.freeswitch.socket.SocketWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.INonBlockingConnection;

/**
 *
 * @author jocke
 */
public final class XSocketSocketWriter implements CommandExecutor, EventMatcher {

    private static final String BREAK = "break";

    private static final Logger LOG =
            LoggerFactory.getLogger(XSocketSocketWriter.class);

    private final INonBlockingConnection connection;

    private String executedAppName;

    private static final Pattern APP_PATTERN =
            Pattern.compile("^(execute-app-name:)(\\s)(\\w*)$",
            Pattern.MULTILINE);

    /**
     * Create a new XSocketWriter the a channel.
     *
     * @param con The connection to use when writing to the socket.
     */
    XSocketSocketWriter(INonBlockingConnection con) {
        this.connection = con;
    }

    public void write(String data) throws IOException {


        String execute_app_name = findApplication(data);

        // Ignore breaks
        if (!BREAK.equals(execute_app_name)) {
            this.executedAppName = execute_app_name;
        }

        synchronized (connection) {
            connection.write(data.getBytes());
        }

    }

    public boolean isConnected() {
        synchronized (connection) {
            return connection.isOpen();
        }
    }

    @Override
    public boolean matches(String application) {

        if (application == null || application.isEmpty()) {
            LOG.error("Can not match null or empty string");
            return false;
        }

        if (executedAppName == null) {
            LOG.warn("No current transaction to match for {} ", application);
            return false;


        } else if (BREAK.equals(application)) {
            //pass "break" through
            return true;

        } else {
            LOG.debug("Matching {} with {} ", executedAppName, application);
            return application.equals(executedAppName);
        }
    }

    private String findApplication(final String data) {
        String result = null;
        Matcher matcher = APP_PATTERN.matcher(data);
        if (matcher.find()) {
            result = matcher.group(3);
        }
        return result;
    }

    @Override
    public void execute(String data) throws IOException {
        write(data);
    }

    @Override
    public boolean isReady() {
        return isConnected();
    }
}
