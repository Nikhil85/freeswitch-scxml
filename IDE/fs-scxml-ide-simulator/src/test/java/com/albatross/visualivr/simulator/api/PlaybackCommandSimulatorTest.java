package com.albatross.visualivr.simulator.api;

import java.util.LinkedList;
import java.util.Queue;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class PlaybackCommandSimulatorTest {
    
    PlaybackCommandSimulator commandSimulator;
    
    @Before
    public void setUp() {
        commandSimulator = new PlaybackCommandSimulator();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of execute method, of class PlaybackCommandSimulator.
     */
    @Test
    @Ignore
    public void testExecute() throws InterruptedException {
        System.out.println("execute");
        
        String[] args = new String[1];
        args[0] = "/home/jocke/SERVICES/voicemail/vmms/prompts/goodby.wav"; //TODO test file
        
        commandSimulator.execute(args);
        
        Thread.sleep(10000);
    }
    /**
     * Test of execute method, of class PlaybackCommandSimulator.
     */
    @Test
    public void testExecuteNull() {
        System.out.println("execute");
        
        String[] args = null;
        
        commandSimulator.execute(args);
        
    }
    /**
     * Test of execute method, of class PlaybackCommandSimulator.
     */
    @Test
    public void testExecuteEmpty() {
        System.out.println("execute");
        
        String[] args = new String[0];
        
        commandSimulator.execute(args);
        
    }

    /**
     * Test of breakAction method, of class PlaybackCommandSimulator.
     */
    @Test
    public void testBreakAction() {
        System.out.println("breakAction");
    }

    /**
     * Test of supports method, of class PlaybackCommandSimulator.
     */
    @Test
    public void testSupports() {
        System.out.println("supports");
        String supports = commandSimulator.supports();
        assertEquals("Playback should be the supported application", supports, "playback");
    }

    /**
     * Test of create method, of class PlaybackCommandSimulator.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        Queue queue = new LinkedList();
        
        CommandSimulator simulator = commandSimulator.create(queue);
        
        assertTrue("Should not return null", simulator != null);
        assertTrue("Must be of instance of PlaybackCommandSimulator", simulator instanceof PlaybackCommandSimulator);
    }

}