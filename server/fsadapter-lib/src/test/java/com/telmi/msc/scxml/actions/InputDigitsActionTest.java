package com.telmi.msc.scxml.actions;

import com.telmi.msc.freeswitch.FSEventName;
import com.telmi.msc.freeswitch.DTMFMessage;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.scxml.actions.InputDigitsAction;
import com.telmi.msc.scxml.engine.CallXmlEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.TriggerEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
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
public final class InputDigitsActionTest {

    private static final int MAX_TIME = 40000;
    private static final Logger LOG =
            LoggerFactory.getLogger(InputDigitsActionTest.class);
    private static final int MAX_DIGITS = 7;
    private static final int MIN_DIGITS = 3;
    private static final String VALUE = "path.wav";
    private static final String BASE = "file:/home/test/test.xml";
    @Mock private FSSession session;
    @Mock private Context ctx;
    @Mock private Evaluator evaluator;
    private InputDigitsAction action;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        action = new InputDigitsAction();
        action.evaluator = evaluator;
        action.ctx = ctx;
        action.setVar("number");
        action.setTermdigits("#*");
        action.setMaxdigits(MAX_DIGITS);
        action.setMindigits(MIN_DIGITS);
        action.setValue(VALUE);
        action.setMaxtime("40s");
        action.setIncludetermdigit(false);
    }

    /**
     * Test so that InputDigits triggers a max digit event.
     *
     * @throws InterruptedException If we fail to add events.
     */
    @Test
    public void testhandleActionMaxdigit() throws InterruptedException {

        BlockingQueue<FSEvent> queue = new ArrayBlockingQueue<FSEvent>(20);

        queue.put(FSEvent.getInstance(FSEventName.DTMF));
        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.TWO));
        queue.put(FSEvent.getInstance(DTMFMessage.THREE));
        queue.put(FSEvent.getInstance(DTMFMessage.FOUR));
        queue.put(FSEvent.getInstance(DTMFMessage.FIVE));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));
        queue.put(FSEvent.getInstance(DTMFMessage.SEVEN));

        FSEvent maxDigits = new FSEvent.EventCatcher(queue)
                .maxDigits(7).startPolling().newFSEvent();

        TriggerEvent event = handleAction(action, maxDigits, "1234567");

        assertSame("IvrEvent has 7 digits, trigger event should be maxdigits ",
                event.getName(), CallXmlEvent.MAXDIGITS.toString());
    }

    /**
     * Test so that InputDigits triggers an timeout event.
     */
    @Test
    public void testHandleActionTimeout() {

        FSEvent maxtime = FSEvent.getInstance(FSEventName.TIMEOUT);

        TriggerEvent event = handleAction(action, maxtime, "");

        assertSame("IvrEvent was timeout trigger event should be maxtime ",
                event.getName(), CallXmlEvent.MAXTIME.toString());

    }

    /**
     * Test so that InputDigits triggers an termdigit event.
     *
     * @throws InterruptedException If we fail to add events.
     */
    @Test
    public void testHandleActionTermdigit() throws InterruptedException {

        BlockingQueue<FSEvent> queue = new ArrayBlockingQueue<FSEvent>(20);
        queue.put(FSEvent.getInstance(DTMFMessage.ZERO));
        queue.put(FSEvent.getInstance(DTMFMessage.ZERO));
        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.STAR));
        queue.put(FSEvent.getInstance(DTMFMessage.TWO));

        FSEvent term = new FSEvent.EventCatcher(queue)
                .maxDigits(5).termDigits(EnumSet.of(DTMFMessage.STAR)).startPolling().newFSEvent();

        TriggerEvent event = handleAction(action, term, "0011");

        assertSame("IvrEvent has termdigit trigger event should be termdigit ",
                event.getName(), CallXmlEvent.TERMDIGIT.toString());


    }

    /**
     *
     * Test so that InputDigits triggers an mindigits event.
     *
     * @throws InterruptedException If we fail to add events.
     */
    @Test(timeout = 5000)
    public void testHandleActionMindigits() throws InterruptedException {

        BlockingQueue<FSEvent> queue = new ArrayBlockingQueue<FSEvent>(20);


        queue.put(FSEvent.getInstance(DTMFMessage.ZERO));
        queue.put(FSEvent.getInstance(DTMFMessage.ZERO));
        queue.put(FSEvent.getInstance(DTMFMessage.STAR));


        FSEvent mindigits = new FSEvent.EventCatcher(queue)
                .maxDigits(4).termDigits(EnumSet.of(DTMFMessage.STAR)).startPolling().newFSEvent();

        TriggerEvent event = handleAction(action, mindigits, "00");

        assertSame("IvrEvent has only 4 digits trigger "
                + "event should be mindigits ",
                event.getName(), CallXmlEvent.MINDIGITS.toString());
    }

    /**
     *
     * @param inDigitsAction    GetDigitsAction to test.
     * @param event     IvrEvent return from session.
     * @param result    Expected variable value.
     *
     * @throws ModelException
     * @throws SCXMLExpressionException
     *
     * @return The TriggerEvent that was found.
     */
    private TriggerEvent handleAction(
            InputDigitsAction inDigitsAction, FSEvent event, String result) {

        Collection<TriggerEvent> events = new ArrayList<TriggerEvent>();
        inDigitsAction.derivedEvents = events;
        Set<DTMFMessage> terms = EnumSet.of(DTMFMessage.STAR, DTMFMessage.POUND);

        expect(session.read(MAX_DIGITS, "/home/test/path.wav", MAX_TIME, terms))
                .andReturn(event);

        ctx.set(inDigitsAction.getVar(), result);

        try {
            expect(evaluator.eval(ctx, VALUE)).andReturn(VALUE);
            expect(ctx.get("base")).andReturn(new URL(BASE));

        } catch (MalformedURLException ex) {
            LOG.error(ex.getMessage());
        } catch (SCXMLExpressionException ex) {
            LOG.error(ex.getMessage());
        }

        EasyMockUnitils.replay();
        inDigitsAction.handleAction(session);

        return events.iterator().next();

    }
}
