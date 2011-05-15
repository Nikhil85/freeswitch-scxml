/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freeswitch.socket.xsocket;

import org.freeswitch.socket.xsocket.client.XsocketClient;
import java.io.IOException;
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
public class XsocketClientSessionTest {

    private XsocketClient session;

    @Before
    public void setUp() throws IOException, InterruptedException {
        session = new XsocketClient();
        session.connect();
        Thread.sleep(5000);
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

    /**
     * Test of onData method, of class XsocketClientSession.
     */
    @Test
    public void testOnData() throws Exception {
    }
}
