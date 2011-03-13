package org.freeswitch.adapter.api;


import java.util.Map;
import java.util.EnumSet;
import java.util.HashMap;
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
public final class EventListTest {

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

        queue.put(getInstance(DTMF.ONE));
        queue.put(getInstance(DTMF.ONE));
        queue.put(getInstance(DTMF.FIVE));
        queue.put(getInstance(DTMF.SIX));

        queue.put(new Event(Event.CHANNEL_EXECUTE_COMPLETE));

        final EventList.EventListBuilder builder = new EventList.EventListBuilder(queue);

        EventList evtl = builder.startPolling().build();

        assertTrue(evtl.contains(DTMF.ONE));
        assertTrue(evtl.contains(DTMF.FIVE));
        assertTrue(evtl.contains(DTMF.SIX));
        assertTrue(evtl.sizeOfDtmfs() == 4);

    }

    @Test
    public void testBuildMaxDigits() throws InterruptedException {

        queue.put(getInstance(DTMF.ONE));
        queue.put(getInstance(DTMF.ONE));
        queue.put(getInstance(DTMF.FIVE));
        queue.put(getInstance(DTMF.SEVEN));
        queue.put(getInstance(DTMF.FIVE));
        queue.put(getInstance(DTMF.SIX));
        queue.put(getInstance(DTMF.SIX));
        queue.put(getInstance(DTMF.SIX));
        queue.put(getInstance(DTMF.SIX));

        queue.put(new Event(Event.CHANNEL_EXECUTE_COMPLETE));
        final EventList evtl = new EventList.EventListBuilder(queue).maxDigits(5).startPolling().build();
        assertTrue("Size of dtmf should be 5 is " + evtl.sizeOfDtmfs(), evtl.sizeOfDtmfs() == 5);
    }

    @Test
    public void testBuildTermDigits() throws InterruptedException {

        queue.put(getInstance(DTMF.ONE));
        queue.put(getInstance(DTMF.ONE));
        queue.put(getInstance(DTMF.FIVE));
        queue.put(getInstance(DTMF.SEVEN));
        queue.put(getInstance(DTMF.FIVE));
        queue.put(getInstance(DTMF.SIX));
        queue.put(getInstance(DTMF.SIX));
        queue.put(getInstance(DTMF.POUND));

        queue.put(getInstance(DTMF.FIVE));
        queue.put(getInstance(DTMF.SIX));

        Set<DTMF> terms = EnumSet.of(DTMF.POUND, DTMF.STAR);

        EventList evt = new EventList.EventListBuilder(queue)
                .maxDigits(10).termDigits(terms).startPolling().build();


        assertTrue(evt.sizeOfDtmfs() == 8);
    }


    private Event getInstance(DTMF dtmf) {
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("DTMF-Digit", dtmf.toString());
        return new Event(Event.DTMF, vars);
    }


}
