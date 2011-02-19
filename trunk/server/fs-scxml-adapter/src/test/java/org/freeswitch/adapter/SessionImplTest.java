package org.freeswitch.adapter;

import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.EventName;
import java.io.IOException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author jocke
 */
public final class SessionImplTest {

    private static final int TEST_TIME_OUT = 2000;
    private CommandExecutor connection;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<EventName> future;
    private SessionImpl session;

    /**
     * Set up the test.
     *
     * @throws FileNotFoundException If file with events is not found.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws FileNotFoundException {
        
        connection = EasyMock.createMock(CommandExecutor.class);
        scheduler = EasyMock.createMock(ScheduledExecutorService.class);
        future = EasyMock.createMock(ScheduledFuture.class);
        session = new SessionImpl(new HashMap<String, Object>(), connection, scheduler, "/tmp", new ArrayBlockingQueue<Event>(50));

    }


    /**
     * Test the answer action.
     *
     */
    @Test
    public void testAnswer() throws IOException {

        session.getEventQueue().add(
            Event.getInstance(EventName.CHANNEL_EXECUTE_COMPLETE));

        EasyMock.expect(connection.isReady()).andReturn(Boolean.TRUE);
        connection.execute(Command.answer());

        EasyMock.replay(connection);

        Event event = session.answer();

        assertTrue("No DTMF event was added!! should be empty ",
            event.sizeOfDtmfs() == 0);

        assertTrue("Event was added should contain it ",
            event.contains(EventName.CHANNEL_EXECUTE_COMPLETE));

        EasyMock.verify(connection);

    }

    /**
     * Test to see that the collection of digits stops when there is a
     * timeout.
     *
     * @throws Exception any exception.
     */
    @Test(timeout = TEST_TIME_OUT)
    public void testGetDigitsTimeout() throws Exception {

        EasyMock.expect(
            scheduler.schedule(
                session, 10000, TimeUnit.MILLISECONDS)).andReturn(future);

        //Simulate a timeout
        session.call();

        EasyMock.expect(future.isDone()).andReturn(Boolean.TRUE);

        EasyMock.replay(future);
        EasyMock.replay(scheduler);
        Set<DTMF> terms = EnumSet.of(DTMF.POUND);

        long timeout = 10000L;
        int maxdigits = 5;

        Event event = session.getDigits(maxdigits, terms, timeout);

        assertTrue("Should contain timeout event ",
            event.contains(EventName.TIMEOUT));

        EasyMock.verify(future);
        EasyMock.verify(scheduler);

    }

    /**
     * Test get maximum number of digits.
     *
     */
    @Test
    public void testGetDigitsMax() {

        assertTrue("Test not started should be empty ",
            session.getEventQueue().isEmpty());

        //Adds 8 dtmf events to the session queue.
        fillQueue();

        EasyMock.expect(
            scheduler.schedule(
                session, 10000, TimeUnit.MILLISECONDS)).andReturn(future);

        EasyMock.expect(future.isDone()).andReturn(Boolean.FALSE);
        EasyMock.expect(future.cancel(true)).andReturn(Boolean.TRUE);

        EasyMock.replay(future);
        EasyMock.replay(scheduler);

        Set<DTMF> terms = EnumSet.of(DTMF.POUND);

        Event event = session.getDigits(5, terms, 10000L);

        assertFalse("No dtmf terminator should be in event ",
            event.containsAny(terms));

        assertTrue("Should have a DTMF event ",
            event.contains(EventName.DTMF));

        assertTrue("Wrong size of digits should be 5 is " + event.sizeOfDtmfs()
            , event.sizeOfDtmfs() == 5);

        EasyMock.verify(future);
        EasyMock.verify(scheduler);
    }

    /**
     * Fill the event queue with some digits.
     */
    private void fillQueue() {
        session.getEventQueue().add(Event.getInstance(DTMF.FOUR));
        session.getEventQueue().add(Event.getInstance(DTMF.THREE));
        session.getEventQueue().add(Event.getInstance(DTMF.FOUR));
        session.getEventQueue().add(Event.getInstance(DTMF.FIVE));
        session.getEventQueue().add(Event.getInstance(DTMF.SIX));
        session.getEventQueue().add(Event.getInstance(DTMF.SEVEN));
        session.getEventQueue().add(Event.getInstance(DTMF.EIGHT));
    }
}
