package org.freeswitch.scxml.test.actions;

import java.lang.annotation.Annotation;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import org.freeswitch.adapter.api.Event;
import java.io.IOException;
import org.freeswitch.adapter.api.DTMF;
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
public class GetDigitsTest {
    public static final String PATH = "org/freeswitch/scxml/test/getdigitsTest.xml";

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
        con.expectApp(SPEAK, "Enter some digits").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

    @After
    public void tearDown() throws IOException {
        con.close();
    }
    
    @Test
    public void testMaxDigits() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.fireEvent(DTMF.TWO);
        con.fireEvent(DTMF.THREE);
        con.fireEvent(DTMF.FOUR);
        con.fireEvent(DTMF.FIVE);
        con.fireEvent(DTMF.SIX);
        con.expectApp(SPEAK, "maxdigits 12345").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }
    
    @Test
    public void testTermdigit() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.fireEvent(DTMF.TWO);
        con.fireEvent(DTMF.THREE);
        con.fireEvent(DTMF.POUND);
        con.expectApp(SPEAK, "termdigit collected 123").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SPEAK, "Bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

    @Test
    public void testMaxTime() throws IOException {
        con.fireEvent(DTMF.ONE);
        con.fireEvent(DTMF.TWO);
        long start = System.currentTimeMillis();
        con.expectApp(SPEAK, "maxtime").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        long time = System.currentTimeMillis() - start;
        assertTrue(time > 9000 && time < 11000);
        con.expectApp(SPEAK, "Bye").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

}
