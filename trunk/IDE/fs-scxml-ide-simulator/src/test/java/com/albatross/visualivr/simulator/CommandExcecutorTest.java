package com.albatross.visualivr.simulator;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class CommandExcecutorTest {

    /**
     * Test of write method, of class CommandExcecutor.
     */
    @Test
    public void testFindAppArgs() throws Exception {
        
        String s = "sendmsg\n"
                + "call-command: execute\n"
                + "execute-app-name: playback\n"
                + "execute-app-arg: /home/test/path/End_Info.wav\n\n";

        CommandExcecutor instance = new CommandExcecutor(null, null, null);
        String[] arg = instance.findApplicationArgs(s);
        
        assertEquals("One argument should have been found", 1, arg.length);
        assertFalse(arg[0].contains("\n"));        
    }
}
