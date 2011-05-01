package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventListBuilder;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public final class AnswerActionTest {

    private Session session;
    private AnswerAction action;
    private ActionSupport actionSupport;

    @Before
    public void setUp() {
        actionSupport = createMock(ActionSupport.class);
        session = createMock(Session.class);
        action = new AnswerAction();
        action.setActionSupport(actionSupport);
    }

    /**
     * Test of handleAction method, of class AnswerAction.
     */
    @Test
    public void testHandleActionAnswer() throws HangupException {
        
        EventList complete = EventListBuilder.single(Event.CHANNEL_EXECUTE_COMPLETE);
        expect(session.answer()).andReturn(complete);
        expect(actionSupport.proceed(complete)).andReturn(Boolean.TRUE);
        actionSupport.fireEvent(CallXmlEvent.ANSWER);
        actionSupport.setContextVar("isconnected", Boolean.TRUE);

        replay(actionSupport, session);
        action.handleAction(session);
        verify(actionSupport, session);
    }

    /**
     * Test so that answer triggers a hangup event when such IvrEvent returns.
     */
    @Test
    public void testHandleActionHangup() throws HangupException {
        EventList hangup = EventListBuilder.single(Event.CHANNEL_HANGUP);
        expect(session.answer()).andReturn(hangup);
        expect(actionSupport.proceed(hangup)).andReturn(Boolean.FALSE);
        actionSupport.setContextVar("isconnected", Boolean.FALSE);

        replay(session, actionSupport);
        action.handleAction(session);
        verify(session, actionSupport);
    }
}
