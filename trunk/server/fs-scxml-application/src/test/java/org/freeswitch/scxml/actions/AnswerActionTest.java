package org.freeswitch.scxml.actions;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.TriggerEvent;
import org.easymock.EasyMock;
import org.freeswitch.adapter.Event;
import org.freeswitch.adapter.EventName;
import org.freeswitch.adapter.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;

/**
 *
 * @author jocke
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public final class AnswerActionTest {

    @Mock private Session session;
//    @Mock private SCInstance scInstance;
    @Mock private Context context;
    @Mock private Evaluator evaluator;
    private AnswerAction action;

    /**
     * Setup the test.
     */
    @Before
    public void setUp() {
        action = new AnswerAction();
        action.evaluator = evaluator;
        action.ctx = context;

    }

    /**
     * Test of handleAction method, of class AnswerAction.
     */
    @Test
    public void testHandleActionAnswer() {

        Event event = Event.getInstance(EventName.CHANNEL_EXECUTE_COMPLETE);

        action.derivedEvents = new ArrayList<TriggerEvent>();
        EasyMock.expect(session.answer()).andReturn(event);
        context.set("isconnected", Boolean.FALSE);

        EasyMockUnitils.replay();
        action.handleAction(session);

        TriggerEvent triggerEvent = action.derivedEvents.iterator().next();

        assertTrue("Not expected event name " + triggerEvent.getName(),
                triggerEvent.getName().equals(CallXmlEvent.ANSWER.toString()));
    }

    /**
     * Test so that answer triggers a hangup event when such IvrEvent returns.
     */
    @Test
    public void testHandleActionHangup() {

        Event hangup = Event.getInstance(EventName.CHANNEL_HANGUP);

        TriggerEvent event = handleAction(hangup);

        assertTrue("Not expected event name " + event.getName(),
                event.getName().equals(CallXmlEvent.HANGUP.toString()));

    }

    /**
     * Handles the action and returns the event.
     *
     * @param event The expected event to be fired.
     * @return      The trigger event found in action.derivedEvents.
     *
     */
    private TriggerEvent handleAction(Event event) {

        action.derivedEvents = new ArrayList<TriggerEvent>();

        expect(session.answer()).andReturn(event);

        context.set("isconnected", Boolean.FALSE);

        EasyMockUnitils.replay();

        action.handleAction(session);

        TriggerEvent triggerEvent = action.derivedEvents.iterator().next();

        return triggerEvent;

    }
}
