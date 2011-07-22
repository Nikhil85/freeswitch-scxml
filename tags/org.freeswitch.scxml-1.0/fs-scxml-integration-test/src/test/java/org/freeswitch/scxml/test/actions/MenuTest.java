package org.freeswitch.scxml.test.actions;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.scxml.test.Fixture;
import org.freeswitch.scxml.test.MockConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.freeswitch.scxml.test.MockConnection.*;

/**
 *
 * @author jocke
 */
public class MenuTest {
    public static final String PATH = "org/freeswitch/scxml/test/menuTest.xml";
       
    private MockConnection con;
    
    @Before
    public void setUp() throws IOException {
        con = new MockConnection(9696);
        con.connect();
        onEntry();
    }

    private void onEntry() throws IOException {
        con.fireEvent(Event.CHANNEL_DATA, Fixture.createDataEventMap(PATH));
        con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

    @After
    public void tearDown() throws IOException {
        con.close();
    }
    
    @Test(timeout = 60000)
    public void testMatchChoiceOne() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.expectApp(SPEAK, "Choice one").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);  
    }
    
    @Test(timeout = 60000)
    public void testMatchChoiceTwo() throws IOException {
        con.fireEvent(DTMF.TWO);
        con.expectApp(SPEAK, "Choice two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);  
    }
    
    @Test(timeout = 60000)
    public void testTermdigit() throws IOException {
        con.fireEvent(DTMF.POUND);
        con.expectApp(SPEAK, "Termdigit").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        testMatchChoiceOne();
    }
  
    @Test(timeout = 60000)
    public void testNoMatch() throws IOException {
        con.fireEvent(DTMF.FIVE);
        con.expectApp(SPEAK, "No match").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        testMatchChoiceOne();
    }
    
    @Test(timeout = 60000)
    public void testTimeout() throws IOException {
        con.expectApp(SPEAK, "Max time").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        testMatchChoiceOne();
    }
    
}