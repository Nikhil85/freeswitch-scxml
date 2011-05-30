package org.freeswitch.adapter.internal.session;

import java.util.concurrent.Future;
import java.net.MalformedURLException;
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
    
    @Before
    public void setUp() {
        session = createMock(Session.class);
        adapter = new CallAdapter(session);
        
    }
    
    /**
     * Test of call method, of class CallAdapter.
     */
    @Test
    public void testCall() throws MalformedURLException {
    }
    
    /**
     * Test of call method, of class CallAdapter.
     */
    @Test
    public void testCallFactoryNotFound() throws MalformedURLException {
    }
   
}
