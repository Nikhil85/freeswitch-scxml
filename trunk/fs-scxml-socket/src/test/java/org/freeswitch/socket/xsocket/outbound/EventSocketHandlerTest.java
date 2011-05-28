package org.freeswitch.socket.xsocket.outbound;

import java.io.IOException;
import org.easymock.EasyMock;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.socket.xsocket.EventManger;
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
    private EventManger serverListener;

    @Before
    public void setUp() {
        connection = EasyMock.createMock(INonBlockingConnection.class);
        handler = new EventSocketHandler();
        serverListener = EasyMock.createMock(EventManger.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of onData method, of class EventSocketHandler.
     */
    @Test
    public void testOnDataCommandReply() throws IOException {
    }

}
