package org.freeswitch.socket.xsocket;

import java.util.Stack;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xsocket.connection.INonBlockingConnection;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class EventMangerTest {

    private EventManager eventManger;
    private INonBlockingConnection connection;
    private EventQueue eventQueue;

    @Before
    public void setUp() {
        connection = createMock(INonBlockingConnection.class);
        eventQueue = createMock(EventQueue.class);
        eventManger = new EventManager(connection, eventQueue);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of onEvent method, of class EventManger.
     */
    @Test
    public void testOnEvent() {
        eventManger.onEvent(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));

    }

    /**
     * Test of matches method, of class EventManger.
     */
    @Test
    public void testMatches() {
        Stack<String> set = new Stack<>();
        eventManger.setApps(set);
        set.push("say");
        set.push("break");
        assertTrue(eventManger.isCurrentTransaktion("break"));
        assertTrue(eventManger.isCurrentTransaktion("say"));
        assertFalse(eventManger.isCurrentTransaktion("break"));
        assertFalse(eventManger.isCurrentTransaktion("say"));
        assertFalse(eventManger.isCurrentTransaktion(null));
    }

    /**
     * Test of execute method, of class EventManger.
     */
    @Test
    public void testExecute() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("sendmsg ").append("1111").append("\n");
        builder.append("call-command: execute\n");
        builder.append("execute-app-name: break\n\n");
        String command = builder.toString();
        
        expect(connection.write(command.getBytes())).andReturn(1);
        replay(connection);
        eventManger.execute(command);
        verify(connection);
        assertTrue(eventManger.isCurrentTransaktion("break"));
    
    }
    
    /**
     * Test of execute method, of class EventManger.
     */
    @Test(expected = IllegalStateException.class)
    public void testExecuteNoAppName() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("sendmsg ").append("1111").append("\n");
        builder.append("call-command: execute\n\n");
        String command = builder.toString();
      
        replay(connection);
        eventManger.execute(command);
        verify(connection);
   
    }
    
    /**
     * Test of execute method, of class EventManger.
     */
    @Test
    public void testExecuteApi() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("api originate ").append("testData").append("\n\n");
        String command = builder.toString();
        
        expect(connection.write(command.getBytes())).andReturn(1);
        replay(connection);
        eventManger.execute(command);
        verify(connection);
        assertTrue(eventManger.getApps().isEmpty());
    }

}
