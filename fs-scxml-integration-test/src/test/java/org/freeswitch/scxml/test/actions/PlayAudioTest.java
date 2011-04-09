package org.freeswitch.scxml.test.actions;

import org.junit.Test;
import org.freeswitch.adapter.api.Event;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import org.freeswitch.scxml.test.MockConnection;
import org.junit.After;
import org.junit.Before;
import static org.freeswitch.scxml.test.MockConnection.*;

/**
 *
 * @author jocke
 */
public class PlayAudioTest {

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
    public void testPhrase() throws IOException {
        con.fireEvent(Event.CHANNEL_DATA, createDataEvent());
        con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

    private Map<String, String> createDataEvent() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("variable_scxml", this.getClass().getClassLoader().getResource("org/freeswitch/scxml/test/playAudioTest.xml").toString());
        return data;
    }
}
