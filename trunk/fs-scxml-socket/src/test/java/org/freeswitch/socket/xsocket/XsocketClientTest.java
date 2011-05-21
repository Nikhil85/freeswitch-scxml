package org.freeswitch.socket.xsocket;

import org.junit.Ignore;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.socket.xsocket.inbound.XsocketClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.SessionImpl;
import org.freeswitch.adapter.api.DefaultEventQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
@Ignore
public class XsocketClientTest {

    private XsocketClient client;

    @Before
    public void setUp() throws IOException, InterruptedException, HangupException {
        client = new XsocketClient("sofia/external/jocke@213.136.53.177:5050 &park()");
        DefaultEventQueue eventQueue = new DefaultEventQueue();
        XsocketEventProducer producer = new XsocketEventProducer(eventQueue, client);
        client.setProducer(producer);
        client.connect();
        
        Event event = eventQueue.poll(1, TimeUnit.MINUTES);
        SessionImpl impl = new SessionImpl(new HashMap<String, Object>(event.getBodyAsMap()), eventQueue, client);
        client.myEvents(impl.getUuid());
        
        System.out.println(event.getEventName());
        System.out.println(impl.getUuid());
        
        impl.speak("flite|kal| hello world");
        impl.hangup();
    
    }

    @After
    public void tearDown() throws IOException {
        client.close();
    }

    /**
     * Test of connect method, of class XsocketClientSession.
     */
    @Test
    public void testConnect() throws Exception {
    }
}
