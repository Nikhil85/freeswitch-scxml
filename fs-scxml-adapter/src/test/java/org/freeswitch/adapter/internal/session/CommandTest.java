package org.freeswitch.adapter.internal.session;

import org.freeswitch.adapter.api.constant.Q850HangupCauses;
import org.freeswitch.adapter.test.utils.CommandMatcher;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class CommandTest {
    
    private static final String UID = "111";
    private Command cmd;
    
    
        @Before
    public void setUp() {
        cmd = new Command(UID);
    }

    
    /**
     * Test of answer method, of class Command.
     */
    @Test
    public void testAnswer() {
        assertThat(cmd.answer(), CommandMatcher.appName("answer", UID)); 
    }

    /**
     * Test of hangup method, of class Command.
     */
    @Test
    public void testHangup() {
        assertThat(cmd.hangup(null), CommandMatcher.appName("hangup", UID)); 
    }
    /**
     * Test of hangup method, of class Command.
     */
    @Test
    public void testHangupWithCause() {
        assertThat(cmd.hangup(Q850HangupCauses.INCOMPATIBLE_DESTINATION), CommandMatcher.appName("hangup", UID).args("88"));
    }

    /**
     * Test of breakcommand method, of class Command.
     */
    @Test
    public void testBreakcommand() {
         assertThat(cmd.breakcommand(), CommandMatcher.appName("break", UID)); 
    }

    /**
     * Test of speak method, of class Command.
     */
    @Test
    public void testSpeak() {
         assertThat(cmd.speak("hello"), CommandMatcher.appName("speak", UID).args("hello")); 
    }

    /**
     * Test of say method, of class Command.
     */
    @Test
    public void testSay() {
        assertThat(cmd.say("en", "digits", "iterated", "hello"), CommandMatcher.appName("say", UID).args("en", "digits", "iterated", "hello"));
    }

    /**
     * Test of record method, of class Command.
     */
    @Test
    public void testRecord() {
        assertThat(cmd.record("/tmp/file.wav", 2000 , 3000 , 4000), CommandMatcher.appName("record", UID).args("/tmp/file.wav", "2" , "3000" , "4000"));
    }

    /**
     * Test of set method, of class Command.
     */
    @Test
    public void testSet() {
       assertThat(cmd.set("test=value"), CommandMatcher.appName("set", UID).args("test=value"));
       
    }

    /**
     * Test of refer method, of class Command.
     */
    @Test
    public void testRefer() {
        assertThat(cmd.refer("sip:0708989889@nowhere.se"), CommandMatcher.appName("deflect", UID).args("sip:0708989889@nowhere.se"));
    }

    /**
     * Test of playback method, of class Command.
     */
    @Test
    public void testPlayback() {
        assertThat(cmd.playback("/tmp/file.wav"), CommandMatcher.appName("playback", UID).args("/tmp/file.wav"));
    }
}
