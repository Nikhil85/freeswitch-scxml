package org.freeswitch.scxml.test.actions;

import org.junit.Test;
import org.freeswitch.scxml.test.Fixture;
import org.freeswitch.adapter.api.event.Event;
import java.io.IOException;
import org.freeswitch.scxml.test.MockConnection;
import org.junit.After;
import org.junit.Before;
import static org.freeswitch.scxml.test.MockConnection.*;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class CallTest {

    public static final String PATH = "org/freeswitch/scxml/test/callTest.xml";
    private MockConnection con;
    
    @Before
    public void setUp() throws IOException {
        con = new MockConnection(9696);
        con.connect();
    }

    @After
    public void tearDown() throws IOException {
        con.close();
    }
   
   @Test
   public void testCall() throws IOException {
//       con.fireEvent(Event.CHANNEL_DATA, Fixture.createDataEventMap(PATH));
//       con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
//       con.expectApi(ORIGINATE).andApiReply("+OK 1234abc");
//       con.expectApi(BRIDGE).andApiReply("+OK 1234abc");
   }
}
