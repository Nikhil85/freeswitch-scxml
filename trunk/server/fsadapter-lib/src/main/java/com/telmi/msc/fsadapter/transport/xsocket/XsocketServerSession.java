package com.telmi.msc.fsadapter.transport.xsocket;

import com.telmi.msc.freeswitch.FSEventName;
import com.telmi.msc.freeswitch.DTMFMessage;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.events.AbstractEvent;
import com.telmi.msc.freeswitch.events.FSEventFactory;
import com.telmi.msc.fsadapter.transport.ServerSessionListener;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
@ThreadSafe
public final class XsocketServerSession implements ServerSessionListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(XsocketServerSession.class);
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("(Event-Name:)(\\s)(\\w*)", Pattern.MULTILINE);
    private static final Pattern APP_PATTERN =
            Pattern.compile("^(Application:)(\\s)(\\w*)$", Pattern.MULTILINE);
    private static final String DTMF_LINE = "DTMF-Digit: ";
    private static final int DTMF_LINE_LENGTH = DTMF_LINE.length();

    private final BlockingQueue<FSEvent> queue;


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
            BlockingQueue<FSEvent> eventQueue,
            EventMatcher eventMatcher) {

        this.queue = eventQueue;
        this.eventMatcher = eventMatcher;
    }

    private FSEventName findEventName(final String data) {
        FSEventName result = null;
        Matcher matcher = EVENT_PATTERN.matcher(data);
        if (matcher.find()) {
            String event = matcher.group(3);
            try {
                result = FSEventName.valueOf(event);
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


        try {
            // Finns här för att testa lite funktion inför nästa rewrite.
            AbstractEvent event = FSEventFactory.getFSEvent(data);
            LOG.trace("AbstractEvent Test: received event {}", event);
        } catch (Exception e) {
            LOG.error("AbstractEvent Test: Oops!", e);
        }

        //
        // Back to original code.
        //

        
        FSEventName fse = findEventName(data);
        if (fse == null) {
            LOG.warn("Data does not have an event '{}' ", data);
            return;
        }

        FSEvent receivedEvent = null;

        if (fse == FSEventName.DTMF) {
            // DTMF is only one character long. get it.
            char dtmfChar =
                    data.charAt(data.indexOf(DTMF_LINE) + DTMF_LINE_LENGTH);

            //But '#' is encoded as '%23'
            if (dtmfChar == '%') {
                dtmfChar = '#';
            }

            receivedEvent = FSEvent.getInstance(DTMFMessage.valueOfChar(dtmfChar));

        } else {
            // XXX vi hittar väl bara application om det är CHANNEL_EXECUTE_COMPLETE
            String application = findApplication(data);
            if (eventMatcher.matches(application)) {
                receivedEvent = FSEvent.getInstance(fse);
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
    public BlockingQueue<FSEvent> getQueue() {
        return queue;
    }

    @Override
    public void onClose() {

        try {
            queue.put(FSEvent.getInstance(FSEventName.CHANNEL_HANGUP));

        } catch (InterruptedException ex) {
            LOG.info(ex.getMessage());
        }
    }
}
