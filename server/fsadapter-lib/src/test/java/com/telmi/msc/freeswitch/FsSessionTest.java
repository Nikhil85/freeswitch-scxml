package com.telmi.msc.freeswitch;

import java.io.IOException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.telmi.msc.fsadapter.transport.SocketWriter;

/**
 *
 * @author jocke
 */
public final class FsSessionTest {

    private static final int TEST_TIME_OUT = 2000;
    private SocketWriter connection;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<FSEventName> future;
    private FSSessionImpl session;

    /**
     * Set up the test.
     *
     * @throws FileNotFoundException If file with events is not found.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws FileNotFoundException {
        connection = EasyMock.createMock(SocketWriter.class);
        scheduler = EasyMock.createMock(ScheduledExecutorService.class);
        future = EasyMock.createMock(ScheduledFuture.class);
        session = new FSSessionImpl(null, connection, scheduler, "/tmp",
            new ArrayBlockingQueue<FSEvent>(50));

    }


    /**
     * Test the answer action.
     *
     */
    @Test
    public void testAnswer() throws IOException {

        session.getEventQueue().add(
            FSEvent.getInstance(FSEventName.CHANNEL_EXECUTE_COMPLETE));

        EasyMock.expect(connection.isConnected()).andReturn(Boolean.TRUE);
        connection.write(FSCommand.answer());

        EasyMock.replay(connection);

        FSEvent event = session.answer();

        assertTrue("No DTMF event was added!! should be empty ",
            event.sizeOfDtmfs() == 0);

        assertTrue("Event was added should contain it ",
            event.contains(FSEventName.CHANNEL_EXECUTE_COMPLETE));

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
        Set<DTMFMessage> terms = EnumSet.of(DTMFMessage.POUND);

        long timeout = 10000L;
        int maxdigits = 5;

        FSEvent event = session.getDigits(maxdigits, terms, timeout);

        assertTrue("Should contain timeout event ",
            event.contains(FSEventName.TIMEOUT));

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

        Set<DTMFMessage> terms = EnumSet.of(DTMFMessage.POUND);

        FSEvent event = session.getDigits(5, terms, 10000L);

        assertFalse("No dtmf terminator should be in event ",
            event.containsAny(terms));

        assertTrue("Should have a DTMF event ",
            event.contains(FSEventName.DTMF));

        assertTrue("Wrong size of digits should be 5 is " + event.sizeOfDtmfs()
            , event.sizeOfDtmfs() == 5);

        EasyMock.verify(future);
        EasyMock.verify(scheduler);
    }

    /**
     * Fill the event queue with some digits.
     */
    private void fillQueue() {
        session.getEventQueue().add(FSEvent.getInstance(DTMFMessage.FOUR));
        session.getEventQueue().add(FSEvent.getInstance(DTMFMessage.THREE));
        session.getEventQueue().add(FSEvent.getInstance(DTMFMessage.FOUR));
        session.getEventQueue().add(FSEvent.getInstance(DTMFMessage.FIVE));
        session.getEventQueue().add(FSEvent.getInstance(DTMFMessage.SIX));
        session.getEventQueue().add(FSEvent.getInstance(DTMFMessage.SEVEN));
        session.getEventQueue().add(FSEvent.getInstance(DTMFMessage.EIGHT));
    }
}
