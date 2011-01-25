package com.telmi.msc.freeswitch;

import com.telmi.msc.freeswitch.DTMFMessage;
import com.telmi.msc.freeswitch.FSEvent;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public final class FSEventTest {

    private static final int QUEUE_SIZE = 50;
    private BlockingQueue<FSEvent> queue;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        queue = new ArrayBlockingQueue<FSEvent>(QUEUE_SIZE);
    }

    /**
     *
     * @throws InterruptedException If we fail to put events to queue.
     */
    @Test
    public void testBuildComplete() throws InterruptedException {

        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.FIVE));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));

        queue.put(FSEvent.getInstance(FSEventName.CHANNEL_EXECUTE_COMPLETE));

        final FSEvent.EventCatcher builder = new FSEvent.EventCatcher(queue);

        FSEvent event = builder.startPolling().newFSEvent();

        assertTrue(event.contains(DTMFMessage.ONE));
        assertTrue(event.contains(DTMFMessage.FIVE));
        assertTrue(event.contains(DTMFMessage.SIX));
        assertTrue(event.sizeOfDtmfs() == 4);

    }

    @Test
    public void testBuildMaxDigits() throws InterruptedException {

        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.FIVE));
        queue.put(FSEvent.getInstance(DTMFMessage.SEVEN));
        queue.put(FSEvent.getInstance(DTMFMessage.FIVE));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));

        queue.put(FSEvent.getInstance(FSEventName.CHANNEL_EXECUTE_COMPLETE));

        final FSEvent evt = new FSEvent.EventCatcher(queue)
                .maxDigits(5).startPolling().newFSEvent();

        assertTrue("Size of dtmf should be 5 is " + evt.sizeOfDtmfs(),
                evt.sizeOfDtmfs() == 5);
    }

    @Test
    public void testBuildTermDigits() throws InterruptedException {

        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.ONE));
        queue.put(FSEvent.getInstance(DTMFMessage.FIVE));
        queue.put(FSEvent.getInstance(DTMFMessage.SEVEN));
        queue.put(FSEvent.getInstance(DTMFMessage.FIVE));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));
        queue.put(FSEvent.getInstance(DTMFMessage.POUND));

        queue.put(FSEvent.getInstance(DTMFMessage.FIVE));
        queue.put(FSEvent.getInstance(DTMFMessage.SIX));

        Set<DTMFMessage> terms = EnumSet.of(DTMFMessage.POUND, DTMFMessage.STAR);

        final FSEvent evt = new FSEvent.EventCatcher(queue)
                .maxDigits(10).termDigits(terms).startPolling().newFSEvent();


        assertTrue(evt.sizeOfDtmfs() == 8);
    }

    @Test
    public void testGetInstance(){

       FSEvent event = FSEvent.getInstance(DTMFMessage.POUND);

       assertTrue(event.contains(DTMFMessage.POUND));

       System.out.println(event);
    }

    @Test
    public void testPut()  throws InterruptedException{

        FSEvent event = FSEvent.getInstance(
                FSEventName.CHANNEL_EXECUTE_COMPLETE);

        queue.put(event);

        assertTrue("Queue should contain CHANNEL_EXECUTE_COMPLETE",
                queue.contains(FSEvent.getInstance(
                FSEventName.CHANNEL_EXECUTE_COMPLETE)));
    }


}
