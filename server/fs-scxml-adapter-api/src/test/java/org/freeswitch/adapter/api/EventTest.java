package org.freeswitch.adapter.api;


import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventName;
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
public final class EventTest {

    private static final int QUEUE_SIZE = 50;
    private BlockingQueue<Event> queue;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        queue = new ArrayBlockingQueue<Event>(QUEUE_SIZE);
    }

    /**
     *
     * @throws InterruptedException If we fail to put events to queue.
     */
    @Test
    public void testBuildComplete() throws InterruptedException {

        queue.put(Event.getInstance(DTMF.ONE));
        queue.put(Event.getInstance(DTMF.ONE));
        queue.put(Event.getInstance(DTMF.FIVE));
        queue.put(Event.getInstance(DTMF.SIX));

        queue.put(Event.getInstance(EventName.CHANNEL_EXECUTE_COMPLETE));

        final Event.EventCatcher builder = new Event.EventCatcher(queue);

        Event event = builder.startPolling().newEvent();

        assertTrue(event.contains(DTMF.ONE));
        assertTrue(event.contains(DTMF.FIVE));
        assertTrue(event.contains(DTMF.SIX));
        assertTrue(event.sizeOfDtmfs() == 4);

    }

    @Test
    public void testBuildMaxDigits() throws InterruptedException {

        queue.put(Event.getInstance(DTMF.ONE));
        queue.put(Event.getInstance(DTMF.ONE));
        queue.put(Event.getInstance(DTMF.FIVE));
        queue.put(Event.getInstance(DTMF.SEVEN));
        queue.put(Event.getInstance(DTMF.FIVE));
        queue.put(Event.getInstance(DTMF.SIX));
        queue.put(Event.getInstance(DTMF.SIX));
        queue.put(Event.getInstance(DTMF.SIX));
        queue.put(Event.getInstance(DTMF.SIX));

        queue.put(Event.getInstance(EventName.CHANNEL_EXECUTE_COMPLETE));

        final Event evt = new Event.EventCatcher(queue)
                .maxDigits(5).startPolling().newEvent();

        assertTrue("Size of dtmf should be 5 is " + evt.sizeOfDtmfs(),
                evt.sizeOfDtmfs() == 5);
    }

    @Test
    public void testBuildTermDigits() throws InterruptedException {

        queue.put(Event.getInstance(DTMF.ONE));
        queue.put(Event.getInstance(DTMF.ONE));
        queue.put(Event.getInstance(DTMF.FIVE));
        queue.put(Event.getInstance(DTMF.SEVEN));
        queue.put(Event.getInstance(DTMF.FIVE));
        queue.put(Event.getInstance(DTMF.SIX));
        queue.put(Event.getInstance(DTMF.SIX));
        queue.put(Event.getInstance(DTMF.POUND));

        queue.put(Event.getInstance(DTMF.FIVE));
        queue.put(Event.getInstance(DTMF.SIX));

        Set<DTMF> terms = EnumSet.of(DTMF.POUND, DTMF.STAR);

        final Event evt = new Event.EventCatcher(queue)
                .maxDigits(10).termDigits(terms).startPolling().newEvent();


        assertTrue(evt.sizeOfDtmfs() == 8);
    }

    @Test
    public void testGetInstance(){

       Event event = Event.getInstance(DTMF.POUND);

       assertTrue(event.contains(DTMF.POUND));

       System.out.println(event);
    }

    @Test
    public void testPut()  throws InterruptedException{

        Event event = Event.getInstance(
                EventName.CHANNEL_EXECUTE_COMPLETE);

        queue.put(event);

        assertTrue("Queue should contain CHANNEL_EXECUTE_COMPLETE",
                queue.contains(Event.getInstance(
                EventName.CHANNEL_EXECUTE_COMPLETE)));
    }


}
