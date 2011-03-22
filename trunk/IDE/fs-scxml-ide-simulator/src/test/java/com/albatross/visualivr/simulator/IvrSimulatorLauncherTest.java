/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.albatross.visualivr.simulator;

import com.albatross.visualivr.utils.EventLookup;
import com.albatross.visualivr.utils.event.RunProjectEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class IvrSimulatorLauncherTest {

    private IvrSimulatorLauncher launcher;
    
    @Before
    public void setUp() {
      launcher = new IvrSimulatorLauncher();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class IvrSimulatorLauncher.
     */
    @Test
    @Ignore
    public void testInit() {
        System.out.println("init");
        launcher.init();
        //EventLookup.getDefault().add(new RunProjectEvent(null));
    }


}