package org.freeswitch.scxml.test.actions;

import org.junit.Ignore;
import java.util.Map;
import org.junit.Test;
import org.freeswitch.adapter.api.event.Event;
import java.io.IOException;
import java.util.HashMap;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.scxml.test.Fixture;
import org.freeswitch.scxml.test.MockConnection;
import org.junit.After;
import org.junit.Before;
import static org.freeswitch.scxml.test.MockConnection.*;

/**
 *
 * @author jocke
 */
public class RecordAudioTest {

    private static final String PATH = "org/freeswitch/scxml/test/recordAudioTest.xml";
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

    @Ignore
    @Test(timeout = 60000)
    public void testRecordAudioTimeout() throws IOException {
        Map<String, String> vars = new HashMap<>();
        vars.put("Application-Data", "/tmp/test.wav 20");
        con.fireEvent(Event.CHANNEL_DATA, Fixture.createDataEventMap(PATH));
        con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Start your recording after the beep").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(RECORD).andReply(Event.CHANNEL_EXECUTE_COMPLETE, vars);
        con.expectApp(SPEAK, "You said").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "/tmp/test.wav").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Duration of the recording is 20 seconds").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

    @Test(timeout = 60000)
    public void testRecordAudioTerm() throws IOException {
        Map<String, String> vars = new HashMap<>();
        vars.put("Application-Data", "/tmp/test.wav 20");
        vars.put("Application", RECORD);
        
        Map<String, String> bv = new HashMap<>();
        bv.put("Application", "break");
        
        con.fireEvent(Event.CHANNEL_DATA, Fixture.createDataEventMap(PATH));
        con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Start your recording after the beep").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(RECORD);
        
        con.fireEvent(DTMF.POUND);
        con.expectApp(BREAK).andReply(Event.CHANNEL_EXECUTE_COMPLETE, bv);
        con.fireEvent(Event.CHANNEL_EXECUTE_COMPLETE, vars);
        con.expectApp(SPEAK, "You said").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "/tmp/test.wav").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Duration of the recording is 20 seconds").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);    
    }
}
