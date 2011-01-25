package com.telmi.msc.scxml.actions;

import com.telmi.msc.freeswitch.FSEventName;
import com.telmi.msc.freeswitch.DTMFMessage;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.scxml.actions.PlayAudioAction;
import com.telmi.msc.scxml.engine.CallXmlEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
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
public final class PlayAudioActionTest {

    private static final String VALUE = "path.wav";
    private static final String BASE = "file:/home/test/test.xml";
    @Mock private FSSession session;
    @Mock private Context ctx;
    @Mock private Evaluator evaluator;
    private PlayAudioAction action;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        action = new PlayAudioAction();
        action.ctx = ctx;
        action.evaluator = evaluator;
        action.setValue(VALUE);
        action.setTermdigits("#");


    }

    /**
     * Test of handleAction method, of class PlayAudioAction.
     *
     * @throws MalformedURLException    If it happens.
     * @throws SCXMLExpressionException If it happens.
     */
    @Test
    public void testHandleActionTermdigit()
            throws SCXMLExpressionException, MalformedURLException {

        action.derivedEvents = new ArrayList<TriggerEvent>();
        action.setTermdigits("#*");

        expect(evaluator.eval(ctx, VALUE)).andReturn(VALUE);

        expect(ctx.get("base")).andReturn(new URL(BASE));

        FSEvent evt = FSEvent.getInstance(DTMFMessage.POUND);

        Set<DTMFMessage> terms = EnumSet.of(DTMFMessage.STAR, DTMFMessage.POUND);

        expect(session.streamFile("/home/test/path.wav", terms)).andReturn(evt);

        EasyMockUnitils.replay();

        action.handleAction(session);

        TriggerEvent event = action.derivedEvents.iterator().next();

        assertTrue("We should have triggered a termdigit event ",
                event.getName().equals(CallXmlEvent.TERMDIGIT.toString()));

    }

    /**
     * Test of handleAction method, of class PlayAudioAction.
     *
     * @throws MalformedURLException    If it happens.
     * @throws SCXMLExpressionException If it happens.
     */
    @Test
    public void testHandleActionNoTermdigit()
            throws SCXMLExpressionException, MalformedURLException {

        action.derivedEvents = new ArrayList<TriggerEvent>();
        action.setTermdigits("");

        expect(evaluator.eval(ctx, VALUE)).andReturn(VALUE);
        expect(ctx.get("base")).andReturn(new URL(BASE));

        FSEvent evt = FSEvent.getInstance(FSEventName.DTMF);

        expect(session.streamFile("/home/test/path.wav")).andReturn(evt);

        EasyMockUnitils.replay();

        action.handleAction(session);

        assertTrue("No event should be triggered ",
                action.derivedEvents.isEmpty());

    }
}
