/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freeswitch.socket.xsocket;

import org.junit.Ignore;
import org.freeswitch.socket.xsocket.inbound.XsocketClient;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.EventQueue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
@Ignore
public class XsocketClientSessionTest {

    private XsocketClient session;

    @Before
    public void setUp() throws IOException, InterruptedException {
        session = new XsocketClient("sofia/external/jocke@192.168.0.1:5070");
        final EventQueue eventQueue = new EventQueue();
        XsocketEventProducer producer = new XsocketEventProducer(eventQueue, session);
        session.setProducer(producer);
        session.connect();

        while (!"CHANNEL_ANSWER".equals(eventQueue.poll(1, TimeUnit.MINUTES).getEventName())) {
            
        }
    }

    @After
    public void tearDown() throws IOException {
        session.close();
    }

    /**
     * Test of connect method, of class XsocketClientSession.
     */
    @Test
    public void testConnect() throws Exception {
    }
}
