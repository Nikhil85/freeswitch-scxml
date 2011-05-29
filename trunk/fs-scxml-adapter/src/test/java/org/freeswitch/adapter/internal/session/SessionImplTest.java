package org.freeswitch.adapter.internal.session;

import java.io.IOException;
import org.freeswitch.adapter.api.event.DefaultEventQueue;
import java.util.Map;
import java.util.HashMap;
import org.easymock.Capture;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class SessionImplTest {

    private SessionImpl sessionImpl;
    private DefaultEventQueue eventQueue;
    private CommandExecutor executor;

    @Before
    public void setUp() {
        eventQueue = createMock(DefaultEventQueue.class);
        executor = createMock(CommandExecutor.class);
        Map<String, Object> vars = new HashMap<>();
        sessionImpl = new SessionImpl(vars, eventQueue, executor);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of lookup method, of class SessionImpl.
     */
    @Test
    public void testLookup() {
        assertNotNull(sessionImpl.lookup(AudioAdapter.class));
        assertNotNull(sessionImpl.lookup(TestExtension.class));
    }

    /**
     * Test of call method, of class SessionImpl.
     */
    @Test
    public void testCall() {
        Capture<Event> evt = new Capture<>();
        expect(eventQueue.add(capture(evt))).andReturn(Boolean.TRUE);
        replay(eventQueue);
        sessionImpl.call();
        verify(eventQueue);
        assertEquals(Event.TIMEOUT, evt.getValue().getEventName());
    }

    /**
     * Test of execute method, of class SessionImpl.
     */
    @Test
    public void testExecute() throws IOException {
        final String DATA = "DATA";
        expect(executor.isReady()).andReturn(Boolean.TRUE);
        executor.execute(DATA);
        
        replay(executor, eventQueue);
        EventQueue execute = sessionImpl.execute(DATA);
        verify(executor, eventQueue);
        
        assertSame(execute, eventQueue);
    }
    
    /**
     * Test of execute method, of class SessionImpl.
     */
    @Test
    public void testExecuteNotReady() throws IOException {
        final String DATA = "DATA";
        Capture<Event> evt = new Capture<>();
        expect(eventQueue.add(capture(evt))).andReturn(Boolean.TRUE);
        expect(executor.isReady()).andReturn(Boolean.FALSE);        
        
        replay(executor, eventQueue);
        EventQueue execute = sessionImpl.execute(DATA);
        verify(executor, eventQueue);
        
        assertSame(execute, eventQueue);
        assertEquals(Event.CHANNEL_HANGUP, evt.getValue().getEventName());
    }
    
    /**
     * Test of execute method, of class SessionImpl.
     */
    @Test
    public void testExecuteIOException() throws IOException {
        final String DATA = "DATA";
        Capture<Event> evt = new Capture<>();
        expect(eventQueue.add(capture(evt))).andReturn(Boolean.TRUE);
        expect(executor.isReady()).andReturn(Boolean.TRUE); 
        executor.execute(DATA);
        expectLastCall().andThrow(new IOException());
        
        replay(executor, eventQueue);
        EventQueue execute = sessionImpl.execute(DATA);
        verify(executor, eventQueue);
        
        assertSame(execute, eventQueue);
        assertEquals(Event.CHANNEL_HANGUP, evt.getValue().getEventName());
    }
}
