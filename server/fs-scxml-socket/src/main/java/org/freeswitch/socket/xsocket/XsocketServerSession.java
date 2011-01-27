package org.freeswitch.socket.xsocket;


import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freeswitch.adapter.DTMF;
import org.freeswitch.adapter.Event;
import org.freeswitch.adapter.EventName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public final class XsocketServerSession implements org.freeswitch.socket.ServerSessionListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(XsocketServerSession.class);
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("(Event-Name:)(\\s)(\\w*)", Pattern.MULTILINE);
    private static final Pattern APP_PATTERN =
            Pattern.compile("^(Application:)(\\s)(\\w*)$", Pattern.MULTILINE);
    private static final String DTMF_LINE = "DTMF-Digit: ";
    private static final int DTMF_LINE_LENGTH = DTMF_LINE.length();

    private final BlockingQueue<Event> queue;


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
    public XsocketServerSession(
            BlockingQueue<Event> eventQueue,
            EventMatcher eventMatcher) {

        this.queue = eventQueue;
        this.eventMatcher = eventMatcher;
    }

    private EventName findEventName(final String data) {
        EventName result = null;
        Matcher matcher = EVENT_PATTERN.matcher(data);
        if (matcher.find()) {
            String event = matcher.group(3);
            try {
                result = EventName.valueOf(event);
            } catch (IllegalArgumentException e) {
                LOG.error("Received unknown FreeSwitch Event '{}'.", event);
            }
        }
        return result;
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
        
        EventName fse = findEventName(data);
        if (fse == null) {
            LOG.warn("Data does not have an event '{}' ", data);
            return;
        }

        Event receivedEvent = null;

        if (fse == EventName.DTMF) {
            // DTMF is only one character long. get it.
            char dtmfChar =
                    data.charAt(data.indexOf(DTMF_LINE) + DTMF_LINE_LENGTH);

            //But '#' is encoded as '%23'
            if (dtmfChar == '%') {
                dtmfChar = '#';
            }

            receivedEvent = Event.getInstance(DTMF.valueOfChar(dtmfChar));

        } else {
            // XXX vi hittar väl bara application om det är CHANNEL_EXECUTE_COMPLETE
            String application = findApplication(data);
            if (eventMatcher.matches(application)) {
                receivedEvent = Event.getInstance(fse);
            } else {
                LOG.warn("Got event from application '{}' not the current transaction", application);
            }
        }

        if (receivedEvent != null) {
            queue.add(receivedEvent);
        }
    }

    /**
     * Get the queue.
     *
     * @return The queue.
     */
    public BlockingQueue<Event> getQueue() {
        return queue;
    }

    @Override
    public void onClose() {

        try {
            queue.put(Event.getInstance(EventName.CHANNEL_HANGUP));

        } catch (InterruptedException ex) {
            LOG.info(ex.getMessage());
        }
    }
}
