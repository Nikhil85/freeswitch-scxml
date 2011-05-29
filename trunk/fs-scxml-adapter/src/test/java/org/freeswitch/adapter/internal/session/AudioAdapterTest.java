package org.freeswitch.adapter.internal.session;

import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.session.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class AudioAdapterTest {

    private static final String UID = "111";
    private Session session;
    private AudioAdapter adapter;
    private Command command;
    private EventQueue eventQueue;

    @Before
    public void setUp() {
        command = new Command(UID);
        session = createMock(Session.class);
        eventQueue = createMock(EventQueue.class);
        adapter = new AudioAdapter(session, command);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of beep method, of class AudioAdapter.
     */
    @Test
    public void testBeep() throws Exception {

        expect(session.getEventQueue()).andReturn(eventQueue);
        expect(session.execute(isA(String.class))).andReturn(eventQueue).times(2);
        expect(eventQueue.poll(anyInt(), eq(TimeUnit.MINUTES))).andReturn(Event.named(Event.CHANNEL_EXECUTE_COMPLETE)).times(2);

        replay(session, eventQueue);
        EventList beep = adapter.beep();
        verify(session, eventQueue);

        assertFalse(beep.contains(Event.CHANNEL_EXECUTE_COMPLETE));
    }

    /**
     * Test of streamFile method, of class AudioAdapter.
     */
    @Test
    public void testStreamFile_String() throws Exception {
        final String file = "/tmp/test.wav";

        expect(session.getUuid()).andReturn(UID);
        expect(session.execute(command.playback(file))).andReturn(eventQueue);
        expect(eventQueue.poll(anyInt(), eq(TimeUnit.MINUTES))).andReturn(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));

        replay(session, eventQueue);
        EventList streamFile = adapter.streamFile(file);
        verify(session, eventQueue);

        assertTrue(streamFile.contains(Event.CHANNEL_EXECUTE_COMPLETE));
    }
}
