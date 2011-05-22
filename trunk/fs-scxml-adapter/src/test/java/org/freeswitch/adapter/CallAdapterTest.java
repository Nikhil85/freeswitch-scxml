package org.freeswitch.adapter;

import java.net.MalformedURLException;
import java.net.URL;
import org.freeswitch.adapter.api.EventQueueListener;
import org.freeswitch.adapter.api.InboundSessionFactory;
import org.freeswitch.adapter.api.Session;
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
        URL docUrl = new URL("file://path");
        expect(session.lookup(InboundSessionFactory.class)).andReturn(factory);
        factory.create(toDial, docUrl, listener);
        replay(session, listener, factory);
        adapter.call(toDial, docUrl, listener);
        verify(session, listener, factory);
    }
    
    /**
     * Test of call method, of class CallAdapter.
     */
    @Test
    public void testCallFactoryNotFound() throws MalformedURLException {
        String toDial = "sip:doe@host";
        URL docUrl = new URL("file://path");
        expect(session.lookup(InboundSessionFactory.class)).andReturn(null);
        replay(session, listener, factory);
        adapter.call(toDial, docUrl, listener);
        verify(session, listener, factory);
    }
    
    
}
