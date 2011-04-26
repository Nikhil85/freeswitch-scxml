package org.freeswitch.scxml.test.actions;

import org.junit.Test;
import org.freeswitch.adapter.api.Event;
import java.util.HashMap;
import java.util.Map;
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
public class PhraseTest {
    public static final String PATH = "org/freeswitch/scxml/test/phraseTest.xml";

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
        con.fireEvent(Event.CHANNEL_DATA, Fixture.createDataEventMap(PATH));
        con.expectApp(ANSWER).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(SAY, "en NUMBER ITERATED '1234'").andReply(Event.CHANNEL_EXECUTE_COMPLETE);
        con.expectApp(HANGUP).andReply(Event.CHANNEL_EXECUTE_COMPLETE);
    }

    
}
