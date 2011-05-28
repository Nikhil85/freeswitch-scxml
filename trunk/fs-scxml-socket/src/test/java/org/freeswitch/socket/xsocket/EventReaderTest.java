package org.freeswitch.socket.xsocket;

import org.freeswitch.adapter.api.event.Event;
import org.junit.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.freeswitch.adapter.api.constant.VarName;
import org.junit.Before;
import org.xsocket.connection.INonBlockingConnection;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public final class EventReaderTest {

    private Map<String, String> testEvents;
    private static final String CHANNEL_DESTROY = "CHANNEL_DESTROY";
    private static final String DTMF_1 = "DTMF";
    private static final String DTMF_B = "DTMFB";
    private static final String EXECUTE_COMPLETE = "CHANNEL_EXECUTE_COMPLETE";
    private EventReader eventReader;
    private INonBlockingConnection connection;

    @Before
    public void setUp() throws FileNotFoundException {
        eventReader = new EventReader();
        testEvents = createTestData();
        connection = createMock(INonBlockingConnection.class);
    }

    @Test
    public void testReadEventDTMF() throws IOException {
        expect(connection.available()).andReturn(500);
        expect(connection.readStringByDelimiter("\n\n")).andReturn(testEvents.get(DTMF_1));
        Event evt = readEvent();
        assertEquals(Event.DTMF, evt.getEventName());
        assertEquals("1", evt.getVar(VarName.DTMF_DIGIT));
    }

    @Test
    public void testReadEventChannelCreate() throws IOException {
        expect(connection.available()).andReturn(500);
        expect(connection.readStringByDelimiter("\n\n")).andReturn(testEvents.get(CHANNEL_DESTROY));
        Event evt = readEvent();
        assertEquals(Event.CHANNEL_DESTROY, evt.getEventName());
    }
    
    @Test
    public void testReadEventByContentLength() throws IOException {
        expect(connection.available()).andReturn(500);
        expect(connection.readStringByDelimiter("\n\n")).andReturn("Content-Length: 600");
        expect(connection.readStringByLength(600, "UTF-8")).andReturn(testEvents.get(CHANNEL_DESTROY));
        Event evt = readEvent();
        assertEquals(Event.CHANNEL_DESTROY, evt.getEventName());
    }
    
    @Test
    public void testReadEventNoData() throws IOException {
        expect(connection.available()).andReturn(-1);
        assertNull(readEvent());
    }
    
    @Test
    public void testReadEventCommadReply() throws IOException {
        expect(connection.available()).andReturn(300);
        expect(connection.readStringByDelimiter("\n\n")).andReturn(EventReader.COMMAND_REPLY);
        assertNull(readEvent());
    }
    
    @Test
    public void testReadEventDisconnectNotice() throws IOException {
        expect(connection.available()).andReturn(300);
        expect(connection.readStringByDelimiter("\n\n")).andReturn(EventReader.DISCONNECT_NOTICE);
        assertNull(readEvent());
    }
    
    
    private Event readEvent() throws IOException {
        replay(connection);
        Event evt = eventReader.readEvent(connection);
        verify(connection);
        return evt;
    }

    private Map<String, String> createTestData() throws FileNotFoundException {
        Map<String, String> eventHolder = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(getClass().getResource("fsEvents.txt").getPath()))) {
            scanner.useDelimiter("\n\n");
            eventHolder.put(CHANNEL_DESTROY, scanner.next());
            eventHolder.put(EXECUTE_COMPLETE, scanner.next().replaceFirst("\n", ""));
            eventHolder.put(DTMF_1, scanner.next().replaceFirst("\n", ""));
            eventHolder.put(DTMF_B, scanner.next().replaceFirst("\n", ""));
        }
        return eventHolder;
    }
}
