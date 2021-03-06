package org.freeswitch.scxml.test.actions;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import org.freeswitch.adapter.api.event.Event;
import java.io.IOException;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.scxml.test.Fixture;
import org.freeswitch.scxml.test.MockConnection;
import org.junit.After;
import org.junit.Before;
import static org.freeswitch.scxml.test.MockConnection.*;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class InputDigitsTest {
    public static final String PATH = "org/freeswitch/scxml/test/inputDigitsTest.xml";

    private MockConnection con;

    @Before
    public void setUp() throws IOException {
        con = new MockConnection(9696);
        con.connect();
        onEntry();
    }
    
    @After
    public void tearDown() throws IOException {
        con.close();
    }

    private void onEntry() throws IOException {
        con.fireEvent(Event.CHANNEL_DATA, Fixture.createDataEventMap(PATH));
        con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(PLAYBACK, "say:Enter some digits").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

    private void onExit() throws IOException {
        con.expectApp(SPEAK, "Bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }
    
    @Test(timeout = 60000)
    public void testMaxDigits() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.fireEvent(DTMF.TWO);
        con.fireEvent(DTMF.THREE);
        con.fireEvent(DTMF.FOUR);
        con.fireEvent(DTMF.FIVE);
        con.fireEvent(DTMF.SIX);
        con.expectApp(SPEAK, "maxdigits 12345").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        onExit();
    }

    
    @Test(timeout = 60000)
    public void testMinDigits() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.fireEvent(DTMF.POUND);
        con.expectApp(SPEAK, "mindigits 1").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        onExit();
    }
    
   @Test(timeout = 60000)
    public void testTermdigit() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.fireEvent(DTMF.TWO);
        con.fireEvent(DTMF.THREE);
        con.fireEvent(DTMF.POUND);
        con.expectApp(SPEAK, "termdigit collected 123").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        onExit();
    }

    @Test(timeout = 60000)
    public void testMaxTime() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.fireEvent(DTMF.TWO);
        long start = System.currentTimeMillis();
        con.expectApp(SPEAK, "maxtime").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        long time = System.currentTimeMillis() - start;
        assertTrue(time > 9000 && time < 11000);
        onExit();
    }

}
