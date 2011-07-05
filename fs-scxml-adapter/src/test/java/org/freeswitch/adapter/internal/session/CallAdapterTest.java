package org.freeswitch.adapter.internal.session;

import org.junit.Ignore;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.session.Session;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
@Ignore
public class CallAdapterTest {
    public static final String DEST = "sip:test@test.se";
    
    private Session session;
    private CallAdapter adapter;
    private EventQueue eventQueue;
    
    @Before
    public void setUp() {
        session = createMock(Session.class);
        eventQueue = createMock(EventQueue.class);
        adapter = new CallAdapter(session);        
    }
    
    /**
     * Test of call method, of class CallAdapter.
     */
    @Test
    public void testCall() throws InterruptedException  {
        
        expect(session.execute(CallAdapter.API_ORIGINATE + DEST + "\n\n")).andReturn(eventQueue);
        expect(eventQueue.poll(5, TimeUnit.MINUTES)).andReturn(new Event(Event.API_RESPONSE));
        
        replay(session, eventQueue);
        EventList el = adapter.call(DEST);
        verify(session, eventQueue);
        
        assertTrue(el.contains(Event.API_RESPONSE));
    }
    
    /**
     * Test of call method, of class CallAdapter.
     */
    @Test()
    public void testCallInterrupted() throws InterruptedException {
        
        expect(session.execute(CallAdapter.API_ORIGINATE + DEST + "\n\n")).andReturn(eventQueue);
        expect(eventQueue.poll(5, TimeUnit.MINUTES)).andThrow(new InterruptedException());
        
        replay(session, eventQueue);
        EventList el = adapter.call(DEST);
        verify(session, eventQueue);
        
        assertTrue(el.contains(Event.CHANNEL_HANGUP));
    }
    
   
}
