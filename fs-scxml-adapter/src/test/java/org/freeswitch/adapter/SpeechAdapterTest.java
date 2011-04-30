package org.freeswitch.adapter;

import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class SpeechAdapterTest {

    private Session session;
    private SpeechAdapter adapter;
    private EventQueue queue;

    @Before
    public void setUp() {
        session = createMock(Session.class);
        adapter = new SpeechAdapter(session);
        queue = createMock(EventQueue.class);
    }

    /**  
     * Test of say method, of class SpeechAdapter.
     */
    @Test
    public void testSay() throws InterruptedException, HangupException {
        String en = "en";
        String iterated = "iterated";
        String pronounced = "pronounced";
        String value = "1";

        expect(session.getUuid()).andReturn("111");
        expect(session.execute(Command.say(en, pronounced, iterated, value))).andReturn(queue);
        expect(queue.poll(5, TimeUnit.MINUTES)).andReturn(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));

        replay(session, queue);
        EventList list = adapter.say(en, pronounced, iterated, value);
        verify(session, queue);

        assertEquals(Event.named(Event.CHANNEL_EXECUTE_COMPLETE), list.get(Event.CHANNEL_EXECUTE_COMPLETE));

    }

    /**
     * Test of speak method, of class SpeechAdapter.
     */
    @Test
    public void testSpeak() throws InterruptedException, HangupException {

        expect(session.getUuid()).andReturn("111");
        expect(session.execute(Command.speak("test", false))).andReturn(queue);
        expect(queue.poll(5, TimeUnit.MINUTES)).andReturn(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));

        replay(session, queue);
        EventList list = adapter.speak("test");
        verify(session, queue);

        assertEquals(Event.named(Event.CHANNEL_EXECUTE_COMPLETE), list.get(Event.CHANNEL_EXECUTE_COMPLETE));
    }
}
