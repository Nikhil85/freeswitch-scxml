package org.freeswitch.socket.xsocket.outbound;

import java.io.IOException;
import org.easymock.EasyMock;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.socket.xsocket.XsocketEventProducer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.freeswitch.socket.xsocket.outbound.EventSocketHandler.*;
import org.xsocket.connection.INonBlockingConnection;

/**
 *
 * @author jocke
 */
public class EventSocketHandlerTest {

    private INonBlockingConnection connection;
    private EventSocketHandler handler;
    private XsocketEventProducer serverListener;

    @Before
    public void setUp() {
        connection = EasyMock.createMock(INonBlockingConnection.class);
        handler = new EventSocketHandler();
        serverListener = EasyMock.createMock(XsocketEventProducer.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of onData method, of class EventSocketHandler.
     */
    @Test
    public void testOnDataCommandReply() throws IOException {
        System.out.println("onData command reply");

        EasyMock.expect(connection.available()).andReturn(COMMAND_REPLY.length());
        EasyMock.expect(connection.readStringByDelimiter(LINE_BREAKS)).andReturn(COMMAND_REPLY);

        EasyMock.replay(connection);
        assertTrue("Command reply should be succefully handled", handler.onData(connection));
        EasyMock.verify(connection);
    }

    /**
     * Test of onData method, of class EventSocketHandler.
     */
    @Test
    public void testOnDataDisconnectNotice() throws IOException {
        System.out.println("onData disconnect notice");

        EasyMock.expect(connection.available()).andReturn(DISCONNECT_NOTICE.length());
        EasyMock.expect(connection.readStringByDelimiter(LINE_BREAKS)).andReturn(DISCONNECT_NOTICE);

        EasyMock.replay(connection);
        assertTrue("disconnect notice should be succefully handled", handler.onData(connection));
        EasyMock.verify(connection);
    }

    /**
     * Test of onData method, of class EventSocketHandler.
     */
    @Test
    public void testOnDataContentLength() throws IOException {
        System.out.println("onData content length");

        String body = createBody();
        String header = createHeader(body);

        EasyMock.expect(connection.available()).andReturn(body.length());
        EasyMock.expect(connection.readStringByDelimiter(LINE_BREAKS)).andReturn(header);
        EasyMock.expect(connection.readStringByLength(body.length(), UTF8)).andReturn(body);
        EasyMock.expect(connection.getAttachment()).andReturn(serverListener);
        serverListener.onEvent(EasyMock.isA(Event.class));

        EasyMock.replay(connection, serverListener);
        assertTrue("disconnect notice should be succefully handled", handler.onData(connection));
        EasyMock.verify(connection, serverListener);
    }

    private String createHeader(String body) {
        return "Content-Length: " + body.length() + "\nContent-Type: text/event-plain" + LINE_BREAKS;
    }

    private String createBody() {
        StringBuilder builder = new StringBuilder();
        builder.append("Event-Name: CHANNEL_UUID\n");
        builder.append("Core-UUID: 4611b345-99a3-4db4-b6aa-1a7886cb5474\n");
        builder.append("FreeSWITCH-Hostname: centos53_02005\n");
        builder.append("FreeSWITCH-IPv4: 192.168.2.5");
        builder.append(LINE_BREAKS);
        return builder.toString();
    }
}