package org.freeswitch.adapter.api.event;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class DefaultEventQueueTest {

    private DefaultEventQueue eventQueue;

    @Before
    public void setUp() {
        eventQueue = new DefaultEventQueue("1234");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of add method, of class DefaultEventQueue.
     */
    @Test
    public void testAdd() {
        eventQueue.add(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));

        assertFalse(eventQueue.isEmpty());
        assertTrue(eventQueue.poll().getEventName().equals(Event.CHANNEL_EXECUTE_COMPLETE));
        assertNull(eventQueue.poll());
    }

    /**
     * Test of clearDigits method, of class DefaultEventQueue.
     */
    @Test
    public void testClearDigits() {
        eventQueue.add(Event.named(Event.DTMF));
        eventQueue.add(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));
        assertFalse(eventQueue.isEmpty());
        eventQueue.clearDigits();
        assertFalse(eventQueue.isEmpty());
        assertTrue(eventQueue.poll().getEventName().equals(Event.CHANNEL_EXECUTE_COMPLETE));
        assertNull(eventQueue.poll());
    }
}
