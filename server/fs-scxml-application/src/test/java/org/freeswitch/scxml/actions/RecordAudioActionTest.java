package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.DTMF;
import org.freeswitch.adapter.Event;
import org.freeswitch.adapter.EventName;
import org.freeswitch.adapter.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
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
public final class RecordAudioActionTest {

    private  static final int TIME_LIMIT = 60000;
    private static final String RECORD = "/tmp/ndskdac454dcc15ac44.wav";

    @Mock private Session session;
    @Mock private Context ctx;
    @Mock private Evaluator evaluator;

    private final Set<DTMF> terms = EnumSet.of(DTMF.POUND);
    private RecordAudioAction action;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        action = new RecordAudioAction();
        action.ctx = ctx;
        action.evaluator = evaluator;
        action.derivedEvents = new ArrayList<TriggerEvent>();
        action.setBeep(true);
        action.setVar("recording");
        action.setMaxtime("60s");
        action.setFormat("wav");
        action.setTermdigits("#");
    }


    /**
     * Test of handleAction method, of class RecordAudioAction.
     */
    @Test
    public void testHandleActionTermdigit() {

       Event event = Event.getInstance(DTMF.POUND);

       expect(session.clearDigits()).andReturn(Boolean.TRUE);
       expect(session.recordFile(TIME_LIMIT, true, terms, "wav"))
               .andReturn(event);

       Map<String, Object> vars = new HashMap<String, Object>();
       vars.put("last_rec", RECORD);
       vars.put("duration", 2000L);

       expect(session.getVars()).andReturn(vars);

       ctx.set(action.getVar(), RECORD);
       ctx.set(action.getTimevar(), 2000L);

       EasyMockUnitils.replay();

       action.handleAction(session);

       TriggerEvent triggerEvent = action.derivedEvents.iterator().next();

       assertEquals("The event should be termdigit ",
               triggerEvent.getName(), CallXmlEvent.TERMDIGIT.toString());

    }
    /**
     * Test of handleAction method, of class RecordAudioAction.
     */
    @Test
    public void testHandleActionMaxtime() {

       Event event =  Event.getInstance(EventName.TIMEOUT);

       expect(session.clearDigits()).andReturn(Boolean.TRUE);

       expect(session.recordFile(TIME_LIMIT, true, terms, "wav"))
               .andReturn(event);

       Map<String, Object> vars = new HashMap<String, Object>();
       vars.put("last_rec", RECORD);
       vars.put("duration", 2000L);

       expect(session.getVars()).andReturn(vars);

       ctx.set(action.getVar(), RECORD);
       ctx.set(action.getTimevar(), 2000L);

       EasyMockUnitils.replay();

       action.handleAction(session);

       TriggerEvent triggerEvent = action.derivedEvents.iterator().next();

       assertEquals("Event should be maxtime ",
               triggerEvent.getName(), CallXmlEvent.MAXTIME.toString());

    }
}
