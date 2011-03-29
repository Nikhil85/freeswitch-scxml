package org.freeswitch.scxml.engine;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

/**
 *
 * @author jocke
 */
public final class CountTest {

    /**
     * Test of countUp method, of class Count.
     */
    @Test
    public void testCountUp() {
        Count count = new Count();

        assertTrue("Count does not start from one ",
                count.getAny() == 1);

        count.countUp("maxtime");

        assertTrue("Count any is wrong ",
                count.getAny() == 2);

        assertTrue("max time has wrong count ",
                count.getMaxtime() == 2);

        count.reset();

        assertTrue("Failed to reset counter ",
                count.getMaxtime() == 1);

        assertTrue("Failed to reset counter ",
                count.getAny() == 1);

        Date date = new Date();

        System.out.println(System.currentTimeMillis() / 1000);


    }
}

