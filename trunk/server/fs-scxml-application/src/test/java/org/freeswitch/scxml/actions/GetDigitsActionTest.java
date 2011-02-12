package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.DTMF;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.freeswitch.adapter.EventName;
import org.freeswitch.adapter.Event;
import org.freeswitch.adapter.Session;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.TriggerEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 *
 * @author jocke
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public final class GetDigitsActionTest {

    private static final int MAX_DIGITS = 7;
    private static final int MAX_TIME = 40000;
    @Mock
    private Session session;
    @Mock
    private Context ctx;
    @Mock
    private Evaluator evaluator;
    private GetDigitsAction action;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        action = new GetDigitsAction();
        action.evaluator = evaluator;
        action.ctx = ctx;
        action.setVar("number");
        action.setTermdigits("#*");
        action.setMaxdigits(MAX_DIGITS);
        action.setMaxtime("40s");
        action.setIncludetermdigit(false);
    }

    /**
     * Test so that the GetDigits action triggers an timeout event if such
     * event is returned.
     */
    @Test
    public void testHandleActionMaxtime() {

        Event maxtime = Event.getInstance(EventName.TIMEOUT);
        TriggerEvent event = handleAction(action, maxtime, "");

        assertSame("IvrEvent was timeout, trigger event should be maxtime ",
                event.getName(), CallXmlEvent.MAXTIME.toString());

    }

    /**
     * Test so that GetDigits action triggers an maxdigit event.
     *
     * @throws InterruptedException If we fail to add events to queue.
     */
    @Test
    public void testHandleActionMaxdigits() throws InterruptedException {

        BlockingQueue<Event> queue = new ArrayBlockingQueue<Event>(20);
        queue.put(Event.getInstance(EventName.DTMF));
        queue.put(Event.getInstance(DTMF.ONE));
        queue.put(Event.getInstance(DTMF.TWO));
        queue.put(Event.getInstance(DTMF.THREE));
        queue.put(Event.getInstance(DTMF.FOUR));
        queue.put(Event.getInstance(DTMF.FIVE));
        queue.put(Event.getInstance(DTMF.SIX));
        queue.put(Event.getInstance(DTMF.SEVEN));

        Event maxDigits = new Event.EventCatcher(queue).maxDigits(MAX_DIGITS).startPolling().newFSEvent();

        // CallXmlEvent.MAXDIGITS
        TriggerEvent event = handleAction(action, maxDigits, "1234567");

        assertSame("IvrEvent has 7 digits, trigger event should be maxdigits ",
                event.getName(), CallXmlEvent.MAXDIGITS.toString());
    }

    /**
     * Test so that GetDigits triggers an termdigit event.
     */
    @Test
    public void testHandleActionTermdigit() {

        Event termDigitEvent = Event.getInstance(DTMF.POUND);
        TriggerEvent event = handleAction(action, termDigitEvent, "");

        assertSame("IvrEvent contains term digit trigger event should be "
                + "termdigit",
                event.getName(),
                CallXmlEvent.TERMDIGIT.toString());
    }

    /**
     *
     * @param getDigits    GetDigitsAction to test.
     * @param event     IvrEvent return from session.
     * @param result    Expected variable value.
     *
     * @return The event that triggered.
     */
    private TriggerEvent handleAction(GetDigitsAction getDigits, Event event, String result) {

        getDigits.derivedEvents = new ArrayList<TriggerEvent>();
        Set<DTMF> terms = EnumSet.of(DTMF.STAR, DTMF.POUND);

        expect(session.getDigits(MAX_DIGITS, terms, MAX_TIME)).andReturn(event);
        ctx.set(getDigits.getVar(), result);

        EasyMockUnitils.replay();
        getDigits.handleAction(session);

        TriggerEvent triggerEvent = getDigits.derivedEvents.iterator().next();
        return triggerEvent;

    }
}
