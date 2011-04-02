package org.freeswitch.scxml.test.menu;

import java.util.Map;
import org.freeswitch.scxml.test.MockConnection;
import java.io.IOException;
import java.util.HashMap;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jocke
 */
public class MenuIT {
   
    private MockConnection con;
    
    @Before
    public void setUp() throws IOException {
        con = new MockConnection(9696);
    }

    @After
    public void tearDown() throws IOException {
        con.close();
    }
    
    @Test
    public void testMatch() throws IOException {
        con.connect();
        con.fireEvent(Event.CHANNEL_DATA, createDataEvent());
        con.expectApp("answer").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp("playback").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.fireEvent(DTMF.ONE);
        con.expectApp("speak").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp("speak").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp("hangup").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        
    }
    
    private Map<String, String> createDataEvent() {
        Map<String, String> data =  new HashMap<String, String>();
        data.put("variable_scxml", getClass().getResource("menuTest.xml").toString());
        return data;
    }

}