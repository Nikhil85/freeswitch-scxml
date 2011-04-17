package org.freeswitch.scxml.test.actions;

import org.freeswitch.adapter.api.DTMF;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.scxml.test.MockConnection;
import org.junit.After;
import org.junit.Before;
import static org.freeswitch.scxml.test.MockConnection.*;

/**
 *
 * @author jocke
 */
public class CountTest {

    private MockConnection con;

    @Before
    public void setUp() throws IOException {
        con = new MockConnection(9696);
        con.connect();
        onEntry();
    }

    private void onEntry() throws IOException {
        con.fireEvent(Event.CHANNEL_DATA, createDataEvent());
    }

    @After
    public void tearDown() throws IOException {
        con.close();
    }

    @Test
    public void testTermdigit() throws IOException {
        
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.fireEvent(DTMF.POUND);
        con.expectApp(SPEAK, "Termdigit count lt 3").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.fireEvent(DTMF.POUND);
        con.expectApp(SPEAK, "Termdigit count lt 3").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.fireEvent(DTMF.POUND);      
        con.expectApp(SPEAK, "Termdigit count eq 3").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        
    }

    private Map<String, String> createDataEvent() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("variable_scxml", this.getClass().getClassLoader().getResource("org/freeswitch/scxml/test/countTest.xml").toString());
        return data;
    }
}
