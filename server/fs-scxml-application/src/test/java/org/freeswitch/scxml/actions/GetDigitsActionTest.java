package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.Session;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
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


        TriggerEvent event = handleAction(action, EventList.single(Event.TIMEOUT), "");

        assertSame("IvrEvent was timeout, trigger event should be maxtime ",
                event.getName(), CallXmlEvent.MAXTIME.toString());

    }

    /**
     * Test so that GetDigits triggers an termdigit event.
     */
    @Test
    public void testHandleActionTermdigit() {

        EventList termDigitEvent = EventList.single(DTMF.POUND);
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
    private TriggerEvent handleAction(GetDigitsAction getDigits, EventList event, String result) {

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
