package org.freeswitch.scxml.test.actions;

import org.junit.Test;
import org.freeswitch.adapter.api.Event;
import java.io.IOException;
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
    public static final String PATH = "org/freeswitch/scxml/test/recordAudioTest.xml";

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
    public void testRecordAudioTermDigit() throws IOException {
        con.fireEvent(Event.CHANNEL_DATA, Fixture.createDataEventMap(PATH));
        con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SAY, "Start your recording after the beep");
        //TODO add expections
    }

}
