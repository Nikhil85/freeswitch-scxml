package org.freeswitch.socket.xsocket;

import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.constant.VarName;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.INonBlockingConnection;

/**
 *
 * @author jocke
 */
public class EventManager implements CommandExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(EventManager.class);
    private static final Pattern APP_PATTERN = Pattern.compile("^(execute-app-name:)(\\s+)(\\w*)$", Pattern.MULTILINE);
    private final EventQueue queue;
    private INonBlockingConnection connection;
    private Stack<String> transactions = new Stack<>();

    /**
     *
     * @param con The connection to use when writing to the socket.
     **/
    public EventManager(INonBlockingConnection con, EventQueue queue) {
        this.connection = con;
        this.queue = queue;
    }

    public EventManager(EventQueue queue) {
        this.queue = queue;
    }

    public void setConnection(INonBlockingConnection connection) {
        this.connection = connection;
    }

    void setApps(Stack<String> apps) {
        this.transactions = apps;
    }

    Stack<String> getApps() {
        return transactions;
    }
   
    public void onEvent(Event evt) {

        if (evt == null) {
            throw new NullPointerException("Null event");
        
        } else {
            LOG.debug("Process event {}" , evt.getEventName());
        }

        if (Event.CHANNEL_EXECUTE_COMPLETE.equals(evt.getEventName()) && isCurrentTransaktion(evt.getVar(VarName.APPLICATION))) {
            queue.add(evt);

        } else if (!Event.CHANNEL_EXECUTE_COMPLETE.equals(evt.getEventName())) {
            queue.add(evt);
        }
    }

    public boolean isCurrentTransaktion(String application) {

        if (application == null || application.isEmpty()) {
            LOG.error("No match for null or empty string");
            return false;

        } else if (transactions.isEmpty()) {
            LOG.warn("No current transaction to match for {} ", application);
            return false;
        }

        String executedAppName = transactions.pop();
        LOG.trace("Matching {} with {} ", executedAppName, application);
        return application.equals(executedAppName);
    }

    @Override
    public void execute(String data) throws IOException {
        String executeAppName = findApplication(data);
        if (executeAppName != null) {
            transactions.push(executeAppName);
        }
        synchronized (this) {
            connection.write(data.getBytes());
        }
    }

    private String findApplication(final String data) {
        Matcher matcher = APP_PATTERN.matcher(data);
        if (matcher.find()) {
            return matcher.group(3);
        } else if (data.startsWith("api")) {
            return null;
        } else {
            throw new IllegalStateException("Could not find application in \n" + data);
        }
    }

    @Override
    public boolean isReady() {
        synchronized (this) {
            return connection.isOpen();
        }
    }

    public void onClose() {
        queue.add(Event.named(Event.CHANNEL_HANGUP));
    }
}
