package org.freeswitch.adapter;

import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventListBuilder;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class DigitsAdapter implements Extension {

    private static final Logger LOG = LoggerFactory.getLogger(DigitsAdapter.class);
    private Session session;

    public DigitsAdapter(Session session) {
        this.session = session;
    }

    public EventList getDigits(int maxdigits, Set<DTMF> terms, long timeout) throws HangupException {
        LOG.debug("Session#{}: getDigits ...", session.getUuid());
        ScheduledFuture<Boolean> future = session.scheduleTimeout(timeout);
        EventList evt = new EventListBuilder(session.getEventQueue())
                .maxDigits(maxdigits).termDigits(terms).consume().build();
        cancelFuture(future);
        return evt;
    }

    private void cancelFuture(ScheduledFuture<Boolean> future) {
        if (!future.isDone()) {
            future.cancel(true);
        }
    }

    public EventList read(int maxDigits, String prompt, long timeout, Set<DTMF> terms) throws HangupException {
        LOG.info("Session#{}: read ...  timeout -->{}", session.getUuid(), timeout);
        EventQueue eventQueue = session.execute(Command.playback(prompt, false));
        EventListBuilder builder = new EventListBuilder(eventQueue).maxDigits(maxDigits).termDigits(terms).consume();
        
        if (builder.containsAnyEvent(Event.CHANNEL_EXECUTE_COMPLETE)) {
            LOG.trace("Session#{} the prompt playing was stopped, start timer", session.getUuid());
            ScheduledFuture<Boolean> future = session.scheduleTimeout(timeout);
            builder.reset().consume();
            cancelFuture(future);
        } else if (!builder.containsAnyEvent(Event.CHANNEL_HANGUP)) {
            LOG.trace("Session#{} the prompt is still playing, cancel it", session.getUuid());
            session.breakAction();
            builder.consume();
        }

        return builder.build();
    }
}
