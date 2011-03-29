package org.freeswitch.socket.xsocket;

import org.freeswitch.adapter.api.Event;
import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public final class XsocketServerSessionTest {

    private Map<String, String> testEvents;
    private static final String CHANNEL_CREATE = "CHANNEL_CREATE";
    private static final String DTMF_1 = "DTMF";
    private static final String DTMF_B = "DTMFB";
    private static final String EXECUTE_COMPLETE = "CHANNEL_EXECUTE_COMPLETE";
    private XsocketServerSession session;
    private EventMatcher eventMatcher;

    /**
     * Set up the test.
     *
     * @throws FileNotFoundException If the file with events is not found.
     */
    @Before
    public void setUp() throws FileNotFoundException {
        testEvents = new HashMap<String, String>();
        Scanner scanner = new Scanner(new File(getClass().getResource("fsEvents.txt").getPath()));
        scanner.useDelimiter("\n\n");

        testEvents.put(CHANNEL_CREATE, scanner.next());
        testEvents.put(EXECUTE_COMPLETE, scanner.next().replaceFirst("\n", ""));

        testEvents.put(DTMF_1, scanner.next().replaceFirst("\n", ""));
        testEvents.put(DTMF_B, scanner.next().replaceFirst("\n", ""));
        scanner.close();
        eventMatcher = EasyMock.createMock(EventMatcher.class);

        session = new XsocketServerSession(new ArrayBlockingQueue<Event>(5), eventMatcher);
    }

    /**
     * Test to see if event queue gets a CHANNEL_EXECUTE_COMPLETE
     * event.
     */
    @Test
    public void testOnDataEvent() {

        String data = testEvents.get(EXECUTE_COMPLETE);

        EasyMock.expect(eventMatcher.matches("answer")).andReturn(true);
        EasyMock.replay(eventMatcher);

        session.onDataEvent(data);

        Event event = session.getQueue().poll();

        Assert.assertTrue("Create a channel event it should be in event queue", event.getEventName().equals(Event.CHANNEL_EXECUTE_COMPLETE));

    }

    /**
     * Test so that there is a DTMF event in queue.
     */
    @Test
    public void testOnDataEventDTMF() {

        String data = testEvents.get(DTMF_1);

        session.onDataEvent(data);

        Event event = session.getQueue().poll();
        
        Assert.assertNotNull(event);

        Assert.assertTrue("Should have a DTMF event ", event.getEventName().equals(Event.DTMF));

        String data2 = testEvents.get(DTMF_B);

        session.onDataEvent(data2);

        Event event2 = session.getQueue().poll();

        Assert.assertTrue("Should have a DTMF event ", event2.getEventName().equals(Event.DTMF));

    }

}
