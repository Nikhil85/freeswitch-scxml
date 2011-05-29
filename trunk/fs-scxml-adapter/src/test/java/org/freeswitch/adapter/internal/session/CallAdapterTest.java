package org.freeswitch.adapter.internal.session;

import java.util.concurrent.Future;
import java.net.MalformedURLException;
import org.freeswitch.adapter.api.event.EventQueueListener;
import org.freeswitch.adapter.api.session.InboundSessionFactory;
import org.freeswitch.adapter.api.session.Session;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class CallAdapterTest {
    
    private Session session;
    private CallAdapter adapter;
    private EventQueueListener listener;
    private InboundSessionFactory factory;
    
    @Before
    public void setUp() {
        session = createMock(Session.class);
        adapter = new CallAdapter(session);
        factory = createMock(InboundSessionFactory.class);
        listener = createMock(EventQueueListener.class);
        
    }
    
    /**
     * Test of call method, of class CallAdapter.
     */
    @Test
    public void testCall() throws MalformedURLException {
        String toDial = "sip:doe@host";
        expect(session.lookup(InboundSessionFactory.class)).andReturn(factory);
        expect(factory.create(toDial, listener)).andReturn(createNiceMock(Future.class));
        replay(session, listener, factory);
        adapter.call(toDial,listener);
        verify(session, listener, factory);
    }
    
    /**
     * Test of call method, of class CallAdapter.
     */
    @Test
    public void testCallFactoryNotFound() throws MalformedURLException {
        String toDial = "sip:doe@host";
        expect(session.lookup(InboundSessionFactory.class)).andReturn(null);
        replay(session, listener, factory);
        adapter.call(toDial, listener);
        verify(session, listener, factory);
    }
   
}
