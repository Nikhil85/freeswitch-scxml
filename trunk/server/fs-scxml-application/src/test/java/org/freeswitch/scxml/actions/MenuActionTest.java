package org.freeswitch.scxml.actions;

import org.freeswitch.scxml.engine.CallXmlEvent;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventName;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.TriggerEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
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
public final class MenuActionTest {

    private  static final int MAX_TIME = 50000;

    private static final org.slf4j.Logger LOG =
            LoggerFactory.getLogger(MenuActionTest.class);
    private static final String VALUE = "path.wav";
    private static final String BASE = "file:/home/test/test.xml";
    @Mock private Session session;
    @Mock private Context ctx;
    @Mock private Evaluator evaluator;
    private MenuAction action;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        action = new MenuAction();
        action.ctx = ctx;
        action.evaluator = evaluator;

        action.setValue(VALUE);
        action.setMaxtime("50s");
        action.setChoices("1234");
        action.setTermdigits("#");
    }

    /**
     *
     * Test so that menu action triggers a termdigit event.
     */
    @Test
    public void testHandleActionTermdigit() {

        Event evt = Event.getInstance(DTMF.POUND);
        TriggerEvent event = handleAction(evt);

        assertSame("IvrEvent has termdigit trigger event should be termdigit ",
                event.getName(), CallXmlEvent.TERMDIGIT.toString());
    }

    /**
     * Test so the menu action triggers a maxtime event.
     */
    @Test
    public void testHandleActionMaxtime() {

        Event evt = Event.getInstance(EventName.TIMEOUT);
        TriggerEvent event = handleAction(evt);

        assertSame("IvrEvent was timeout, trigger event should be maxtime ",
                event.getName(), CallXmlEvent.MAXTIME.toString());


    }

    /**
     * Test so the the menu action triggers choice events.
     */
    @Test
    public void testHandleActionChoice() {

        Event evt = Event.getInstance(DTMF.ONE);

        handleAction(evt);

        Iterator<TriggerEvent> iterator = action.derivedEvents.iterator();

        TriggerEvent choiceOne = iterator.next();

        TriggerEvent choiceMatch = iterator.next();

        assertTrue("We sould have two events ",
                action.derivedEvents.size() == 2);

        String choice1 = CallXmlEvent.CHOICE.toString() + ":1";

        assertEquals("Event is not choice:1",
                choiceOne.getName(), choice1);

        assertSame("Event is not match ",
                choiceMatch.getName(), CallXmlEvent.MATCH.toString());

    }

    /**
     * Test so that menu action triggers a nomatch event.
     */
    @Test
    public void testHandleActionNomatch() {

        Event evt = Event.getInstance(DTMF.FIVE);

        TriggerEvent event = handleAction(evt);

        assertSame("Event is not nomatch ",
                event.getName(), CallXmlEvent.NOMATCH.toString());

    }

    /**
     * Test of handleAction method, of class MenuAction.
     *
     * @param evt  The event to return from IvrSession.
     *
     * @return The triggered event.
     */
    public TriggerEvent handleAction(Event evt) {

        Set<DTMF> terms = EnumSet.of(DTMF.POUND);

        action.derivedEvents = new ArrayList<TriggerEvent>();


        try {
            expect(ctx.get("base")).andReturn(new URL(BASE));
            expect(evaluator.eval(ctx, VALUE)).andReturn(VALUE);

        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }

        expect(session.clearDigits()).andReturn(Boolean.TRUE);
        expect(session.read(1, "/home/test/path.wav", MAX_TIME, terms))
                .andReturn(evt);

        EasyMockUnitils.replay();

        action.handleAction(session);

        return action.derivedEvents.iterator().next();

    }
}
