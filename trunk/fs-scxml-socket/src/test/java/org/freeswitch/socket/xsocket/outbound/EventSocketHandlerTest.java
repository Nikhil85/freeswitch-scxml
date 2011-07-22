package org.freeswitch.socket.xsocket.outbound;

import org.freeswitch.adapter.api.session.Session;
import java.util.Map;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.session.OutboundSessionFactory;
import org.freeswitch.scxml.application.api.ThreadPoolManager;
import org.freeswitch.socket.xsocket.ApplicationRunner;
import org.freeswitch.socket.xsocket.EventManager;
import org.freeswitch.socket.xsocket.EventReader;
import org.freeswitch.test.utils.MockLookup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xsocket.connection.INonBlockingConnection;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class EventSocketHandlerTest {

    private INonBlockingConnection connection;
    private EventSocketHandler handler;
    private EventManager eventManager;
    private EventReader eventReader;
    private OutboundSessionFactory factory;
    private ThreadPoolManager poolManager;
    private ExecutorService executorService;

    @Before
    public void setUp() {
        connection = createMock(INonBlockingConnection.class);
        eventManager = createMock(EventManager.class);
        eventReader  = createMock(EventReader.class);
        factory = createMock(OutboundSessionFactory.class);
        handler = new EventSocketHandler(eventReader);
        poolManager = createMock(ThreadPoolManager.class);
        executorService = createMock(ExecutorService.class);
        
        MockLookup.setInstances(factory, poolManager);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of onData method, of class EventSocketHandler.
     */
    @Test
    public void testOnDataNewSession() throws IOException {
        
        expect(eventReader.readEvent(connection)).andReturn(Event.named(Event.CHANNEL_DATA));
        expect(connection.getAttachment()).andReturn(null);
        connection.setAttachment(isA(EventManager.class));
        expect(factory.create(isA(Map.class), isA(EventManager.class), isA(EventQueue.class))).andReturn(createMock(Session.class));
        expect(poolManager.getWorkerPool()).andReturn(executorService);
        executorService.execute(isA(ApplicationRunner.class));
        
        replay(connection, eventManager, eventReader, factory, poolManager, executorService);
        handler.onData(connection);
        verify(connection, eventManager, eventReader, factory, poolManager, executorService);
    }
    
    /**
     * Test of onData method, of class EventSocketHandler.
     */
    @Test
    public void testOnDataHasSession() throws IOException {
        final Event event = Event.named(Event.CHANNEL_EXECUTE_COMPLETE);
        
        expect(eventReader.readEvent(connection)).andReturn(event);
        expect(connection.getAttachment()).andReturn(eventManager);
        eventManager.onEvent(event);
        
        replay(connection, eventManager, eventReader);
        handler.onData(connection);
        verify(connection, eventManager, eventReader);
    }
    /**
     * Test of onData method, of class EventSocketHandler.
     */
    @Test
    public void testOnDataEventNull() throws IOException {
        
        expect(eventReader.readEvent(connection)).andReturn(null);
        
        replay(connection, eventManager, eventReader);
        handler.onData(connection);
        verify(connection, eventManager, eventReader);
    }
}
