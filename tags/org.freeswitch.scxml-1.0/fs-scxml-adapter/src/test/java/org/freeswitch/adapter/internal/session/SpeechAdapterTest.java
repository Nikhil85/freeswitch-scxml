package org.freeswitch.adapter.internal.session;

import org.freeswitch.adapter.internal.session.SpeechAdapter;
import org.freeswitch.adapter.api.event.DefaultEventQueue;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class SpeechAdapterTest {
    public static final String UID = "111";
    private Command command;

    private Session session;
    private SpeechAdapter adapter;
    private DefaultEventQueue queue;

    @Before
    public void setUp() {
        session = createMock(Session.class);
        command = new Command(UID);
        adapter = new SpeechAdapter(session, command);
        queue = createMock(DefaultEventQueue.class);
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

        expect(session.getUuid()).andReturn(UID);
        expect(session.execute(command.say(en, pronounced, iterated, value))).andReturn(queue);
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

        expect(session.getUuid()).andReturn(UID);
        expect(session.execute(command.speak("test"))).andReturn(queue);
        expect(queue.poll(5, TimeUnit.MINUTES)).andReturn(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));

        replay(session, queue);
        EventList list = adapter.speak("test");
        verify(session, queue);

        assertEquals(Event.named(Event.CHANNEL_EXECUTE_COMPLETE), list.get(Event.CHANNEL_EXECUTE_COMPLETE));
    }
}
