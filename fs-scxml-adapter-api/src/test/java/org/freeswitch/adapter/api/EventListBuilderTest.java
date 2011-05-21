package org.freeswitch.adapter.api;


import java.util.Map;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public final class EventListBuilderTest {

    private EventQueue queue;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        queue = new DefaultEventQueue();
    }

    /**
     *
     * @throws InterruptedException If we fail to put events to queue.
     */
    @Test
    public void testBuildComplete() throws InterruptedException, HangupException {

        queue.add(getInstance(DTMF.ONE));
        queue.add(getInstance(DTMF.ONE));
        queue.add(getInstance(DTMF.FIVE));
        queue.add(getInstance(DTMF.SIX));

        queue.add(new Event(Event.CHANNEL_EXECUTE_COMPLETE));

        final EventListBuilder builder = new EventListBuilder(queue);

        EventList evtl = builder.consume().build();

        assertTrue(evtl.contains(DTMF.ONE));
        assertTrue(evtl.contains(DTMF.FIVE));
        assertTrue(evtl.contains(DTMF.SIX));
        assertTrue(evtl.sizeOfDtmfs() == 4);

    }

    @Test
    public void testBuildMaxDigits() throws InterruptedException, HangupException {

        queue.add(getInstance(DTMF.ONE));
        queue.add(getInstance(DTMF.ONE));
        queue.add(getInstance(DTMF.FIVE));
        queue.add(getInstance(DTMF.SEVEN));
        queue.add(getInstance(DTMF.FIVE));
        queue.add(getInstance(DTMF.SIX));
        queue.add(getInstance(DTMF.SIX));
        queue.add(getInstance(DTMF.SIX));
        queue.add(getInstance(DTMF.SIX));

        queue.add(new Event(Event.CHANNEL_EXECUTE_COMPLETE));
        final EventList evtl = new EventListBuilder(queue).maxDigits(5).consume().build();
        assertTrue("Size of dtmf should be 5 is " + evtl.sizeOfDtmfs(), evtl.sizeOfDtmfs() == 5);
    }

    @Test
    public void testBuildTermDigits() throws InterruptedException, HangupException {

        queue.add(getInstance(DTMF.ONE));
        queue.add(getInstance(DTMF.ONE));
        queue.add(getInstance(DTMF.FIVE));
        queue.add(getInstance(DTMF.SEVEN));
        queue.add(getInstance(DTMF.FIVE));
        queue.add(getInstance(DTMF.SIX));
        queue.add(getInstance(DTMF.SIX));
        queue.add(getInstance(DTMF.POUND));

        queue.add(getInstance(DTMF.FIVE));
        queue.add(getInstance(DTMF.SIX));
        Set<DTMF> terms = EnumSet.of(DTMF.POUND, DTMF.STAR);
        EventList evt = new EventListBuilder(queue).maxDigits(10).termDigits(terms).consume().build();
        assertSame(8, evt.sizeOfDtmfs());
    }


    private Event getInstance(DTMF dtmf) {
        Map<String, String> vars = new HashMap<>();
        vars.put("DTMF-Digit", dtmf.toString());
        return new Event(Event.DTMF, vars);
    }


}
