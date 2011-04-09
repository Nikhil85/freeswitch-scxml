package org.freeswitch.scxml.test.actions;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.scxml.test.MockConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.freeswitch.scxml.test.MockConnection.*;
import org.osgi.framework.BundleContext;

/**
 *
 * @author jocke
 */
public class MenuTest {
       
    private MockConnection con;
    
    @Before
    public void setUp() throws IOException {
        con = new MockConnection(9696);
        con.connect();
        onEntry();
    }

    private void onEntry() throws IOException {
        con.fireEvent(Event.CHANNEL_DATA, createDataEvent());
        con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

    @After
    public void tearDown() throws IOException {
        con.close();
    }
    
    @Test
    public void testMatchChoiceOne() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.expectApp(SPEAK, "Choice one").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);  
    }
    
    @Test
    public void testMatchChoiceTwo() throws IOException {
        con.fireEvent(DTMF.TWO);
        con.expectApp(SPEAK, "Choice two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);  
    }
  
    @Test
    public void testTermdigit() throws IOException {
        con.fireEvent(DTMF.POUND);
        con.expectApp(SPEAK, "Termdigit").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        testMatchChoiceOne();
    }
  
    @Test
    public void testNoMatch() throws IOException {
        con.fireEvent(DTMF.FIVE);
        con.expectApp(SPEAK, "No match").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        testMatchChoiceOne();
    }
    
    @Test
    public void testTimeout() throws IOException {
        con.expectApp(SPEAK, "Max time").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:enter one or two").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        testMatchChoiceOne();
    }
    
    private Map<String, String> createDataEvent() {
        Map<String, String> data =  new HashMap<String, String>();
        data.put("variable_scxml", this.getClass().getClassLoader().getResource("org/freeswitch/scxml/test/menuTest.xml").toString());
        return data;
    }

}