package org.freeswitch.adapter;

import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.CommandExecutor;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.test.utils.MockLookup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public final class SessionImplTest {

    private static final int TEST_TIME_OUT = 2000;
    private CommandExecutor connection;
    private EventQueue evtQueue;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<Boolean> future;
    private SessionImpl session;

    /**
     * Set up the test.
     *
     * @throws FileNotFoundException If file with events is not found.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws FileNotFoundException {
        connection = createMock(CommandExecutor.class);
        scheduler = createMock(ScheduledExecutorService.class);
        future = createMock(ScheduledFuture.class);
        evtQueue = new EventQueue();
        MockLookup.setInstances(scheduler);
        session = new SessionImpl(new HashMap<String, Object>(), evtQueue, connection);

    }

    /**
     * Test the answer action.
     *
     */
    @Test
    public void testAnswer() throws IOException {

        evtQueue.add(new Event(Event.CHANNEL_EXECUTE_COMPLETE));
        expect(connection.isReady()).andReturn(Boolean.TRUE);
        connection.execute(Command.answer());

        replay(connection);
        EventList event = session.answer();
        verify(connection);

        assertTrue("No DTMF event was added!! should be empty ", event.sizeOfDtmfs() == 0);
        assertTrue("Event was added should contain it ", event.contains(Event.CHANNEL_EXECUTE_COMPLETE));

    }

    /**
     * Test to see that the collection of digits stops when there is a
     * timeout.
     *
     * @throws Exception any exception.
     */
    @Test(timeout = TEST_TIME_OUT)
    public void testGetDigitsTimeout() throws Exception {

        expect(scheduler.schedule(session, 10000, TimeUnit.MILLISECONDS)).andReturn(future);
        session.call();
        expect(future.isDone()).andReturn(Boolean.TRUE);

        long timeout = 10000L;
        int maxdigits = 5;

        replay(future, scheduler);
        EventList event = session.getDigits(maxdigits, EnumSet.of(DTMF.POUND), timeout);
        verify(future, scheduler);

        assertTrue("Should contain timeout event ", event.contains(Event.TIMEOUT));


    }

    /**
     * Test get maximum number of digits.
     *
     */
    @Test
    public void testGetDigitsMax() {

        assertTrue("Test not started should be empty ", evtQueue.isEmpty());
        fillQueue();

        expect(scheduler.schedule(session, 10000, TimeUnit.MILLISECONDS)).andReturn(future);
        expect(future.isDone()).andReturn(Boolean.FALSE);
        expect(future.cancel(true)).andReturn(Boolean.TRUE);

        replay(scheduler, future);
        EventList event = session.getDigits(5, EnumSet.of(DTMF.POUND), 10000L);
        verify(future, scheduler);

        assertFalse("No dtmf terminator should be in event ", event.containsAny(EnumSet.of(DTMF.POUND)));
        assertTrue("Should have a DTMF event ", event.contains(Event.DTMF));
        assertTrue("Wrong size of digits should be 5 is " + event.sizeOfDtmfs(), event.sizeOfDtmfs() == 5);

    }

    @Test(timeout = 2000)
    public void testSpeak() throws IOException {

        evtQueue.add(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));
        final String toSpeak = "hello world";

        Capture<String> cap = new Capture<>();
        connection.execute(capture(cap));
        expect(connection.isReady()).andReturn(Boolean.TRUE);

        replay(connection);
        EventList speak = session.speak(toSpeak);
        verify(connection);

        assertTrue(speak.contains(Event.CHANNEL_EXECUTE_COMPLETE));
        
        assertThat(cap.getValue(),  CommandMatcher.appName("speak").args("hello world"));


    }

    @Test(timeout = 2000)
    public void testBeep() throws IOException {

        Capture<String> commandCapture = new Capture<>(CaptureType.ALL);

        evtQueue.add(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));
        evtQueue.add(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));
        expect(connection.isReady()).andReturn(Boolean.TRUE).anyTimes();
        connection.execute(capture(commandCapture));
        connection.execute(capture(commandCapture));
        
        replay(connection);
        EventList beep = session.beep();
        verify(connection);

        assertThat(commandCapture.getValues().get(0), CommandMatcher.appName("playback").args(SessionImpl.BEEP_TONE));
        assertThat(commandCapture.getValues().get(1), CommandMatcher.appName("playback").args(SessionImpl.SILENCE_STREAM));


    }

    /**
     * Fill the event queue with some digits.
     */
    private void fillQueue() {
        session.getEventQueue().add(getInstance(DTMF.FOUR));
        session.getEventQueue().add(getInstance(DTMF.THREE));
        session.getEventQueue().add(getInstance(DTMF.FOUR));
        session.getEventQueue().add(getInstance(DTMF.FIVE));
        session.getEventQueue().add(getInstance(DTMF.SIX));
        session.getEventQueue().add(getInstance(DTMF.SEVEN));
        session.getEventQueue().add(getInstance(DTMF.EIGHT));
    }

    private Event getInstance(DTMF dtmf) {
        Map<String, String> vars = new HashMap<>();
        vars.put("DTMF-Digit", dtmf.toString());
        return new Event(Event.DTMF, vars);
    }
}
