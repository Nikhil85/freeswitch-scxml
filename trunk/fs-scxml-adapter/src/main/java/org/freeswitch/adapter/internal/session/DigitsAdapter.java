package org.freeswitch.adapter.internal.session;

import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class DigitsAdapter implements Extension {

    private static final Logger LOG = LoggerFactory.getLogger(DigitsAdapter.class);
    private Session session;
    private Command cmd;

    public DigitsAdapter(Session session, Command cmd) {
        this.session = session;
        this.cmd = cmd;
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
        EventQueue eventQueue = session.execute(cmd.playback(prompt));
        EventListBuilder builder = new EventListBuilder(eventQueue).maxDigits(maxDigits).termDigits(terms).consume();
        
        if (builder.contains(Event.CHANNEL_EXECUTE_COMPLETE)) {
            LOG.trace("Session#{} the prompt playing was stopped, start timer", session.getUuid());
            ScheduledFuture<Boolean> future = session.scheduleTimeout(timeout);
            builder.reset().consume();
            cancelFuture(future);
        } else if (!builder.contains(Event.CHANNEL_HANGUP)) {
            LOG.trace("Session#{} the prompt is still playing, cancel it", session.getUuid());
            session.breakAction();
            builder.consume();
        }

        return builder.build();
    }
}
