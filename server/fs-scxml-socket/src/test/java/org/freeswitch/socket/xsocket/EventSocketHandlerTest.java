package org.freeswitch.socket.xsocket;

import java.io.IOException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.freeswitch.socket.xsocket.EventSocketHandler.*;
import org.xsocket.connection.INonBlockingConnection;

/**
 *
 * @author jocke
 */
public class EventSocketHandlerTest {
    
    private INonBlockingConnection connection;
    private EventSocketHandler handler;
    
    @Before
    public void setUp() {
        connection = EasyMock.createMock(INonBlockingConnection.class);
        handler = new EventSocketHandler();
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
        assertTrue("Command reply should be succefully handled" , handler.onData(connection));
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
        assertTrue("disconnect notice should be succefully handled" , handler.onData(connection));
        EasyMock.verify(connection);
    }
    /**
     * Test of onData method, of class EventSocketHandler.
     */
    @Test
    public void testOnDataContentLength() throws IOException {
        System.out.println("onData content length");
        
        EasyMock.expect(connection.available()).andReturn(DISCONNECT_NOTICE.length());
        EasyMock.expect(connection.readStringByDelimiter(LINE_BREAKS)).andReturn(DISCONNECT_NOTICE);
        
        EasyMock.replay(connection);        
        assertTrue("disconnect notice should be succefully handled" , handler.onData(connection));
        EasyMock.verify(connection);
    }

//    /**
//     * Test of onConnect method, of class EventSocketHandler.
//     */
//    @Test
//    public void testOnConnect() {
//        System.out.println("onConnect");
//        INonBlockingConnection connection = null;
//        EventSocketHandler instance = new EventSocketHandler();
//        boolean expResult = false;
//        boolean result = instance.onConnect(connection);
//        assertEquals(expResult, result);
//         TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of onDisconnect method, of class EventSocketHandler.
//     */
//    @Test
//    public void testOnDisconnect() {
//        System.out.println("onDisconnect");
//        INonBlockingConnection connection = null;
//        EventSocketHandler instance = new EventSocketHandler();
//        boolean expResult = false;
//        boolean result = instance.onDisconnect(connection);
//        assertEquals(expResult, result);
//         TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}