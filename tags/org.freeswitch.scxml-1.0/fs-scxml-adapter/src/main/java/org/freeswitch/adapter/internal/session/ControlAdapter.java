package org.freeswitch.adapter.internal.session;

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
class ControlAdapter implements Extension {

    private static final Logger LOG = LoggerFactory.getLogger(ControlAdapter.class);
    private Session session;
    private SessionState state;
    private Command cmd;

    ControlAdapter(Session session, SessionState state, Command cmd) {
        this.session = session;
        this.state = state;
        this.cmd = cmd;
    }

    public EventList answer() throws HangupException {
        LOG.trace("Session#{}: answer ...", session.getUuid());
        if (state.isNotAnswered()) {
            EventQueue eventQueue = session.execute(cmd.answer());
            state.setNotAnswered(false);
            return new EventListBuilder(eventQueue).consume().build();

        } else {
            return EventListBuilder.single(Event.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    public void sleep(long milliseconds) {
        LOG.debug("Session#{}: Channel Silence for '{}' millis.", session.getUuid(), milliseconds);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            LOG.warn("Session#{}: Thread interupted while sleeping, {}", session.getUuid(), ex.getMessage());
        }
    }

    public EventList hangup() throws HangupException {
        LOG.trace("Session#{}: hangup ...", session.getUuid());
        if (session.isAlive()) {
            state.setAlive(false);
            EventQueue eventQueue = session.execute(cmd.hangup(null));
            return new EventListBuilder(eventQueue).consume().build();

        } else {
            return EventListBuilder.single(Event.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    public EventList breakAction() throws HangupException {
        LOG.debug("Session#{}: breakAction ...", session.getUuid());
        EventQueue eventQueue = session.execute(cmd.breakcommand());
        sleep(1000L);
        return new EventListBuilder(eventQueue).consume().build();
    }
}
