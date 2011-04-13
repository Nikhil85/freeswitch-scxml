package org.freeswitch.socket.xsocket;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public final class XsocketServerSession implements org.freeswitch.socket.ServerSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(XsocketServerSession.class);
    private static final Pattern EVENT_PATTERN = Pattern.compile("(Event-Name:)(\\s)(\\w*)", Pattern.MULTILINE);
    private static final Pattern APP_PATTERN = Pattern.compile("^(Application:)(\\s)(\\w*)$", Pattern.MULTILINE);
    private final EventQueue queue;
    private final EventMatcher eventMatcher;

    /**
     * Create a new ServerSession that will parse events and add it to the
     * queue.
     *
     * @param eventQueue
     *        To store events.
     * @param eventMatcher
     *        The matcher to ask if the event should be published to the queue.
     */
    public XsocketServerSession(EventQueue eventQueue, EventMatcher eventMatcher) {
        this.queue = eventQueue;
        this.eventMatcher = eventMatcher;
    }

    private String findEventName(final String data) {

        Matcher matcher = EVENT_PATTERN.matcher(data);

        if (matcher.find()) {
            return matcher.group(3);
        }

        return null;
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
    public void onDataEvent(String data) {

        String evtName = findEventName(data);

        if (evtName == null) {
            LOG.warn("Data does not have an event '{}' ", data);
            return;

        } else {
            LOG.info("Process event " + evtName);
        }

        if (evtName.equals(Event.CHANNEL_EXECUTE_COMPLETE) && isCurrentTransaktion(data)) {
            queue.add(new Event(evtName, data));

        } else if (!evtName.equals(Event.CHANNEL_EXECUTE_COMPLETE)) {
            queue.add(new Event(evtName, data));
        }
    }

    private boolean isCurrentTransaktion(String data) {
        return eventMatcher.matches(findApplication(data));
    }

    public EventQueue getQueue() {
        return queue;
    }

    @Override
    public void onClose() {
        queue.add(Event.named(Event.CHANNEL_HANGUP));
    }
}
