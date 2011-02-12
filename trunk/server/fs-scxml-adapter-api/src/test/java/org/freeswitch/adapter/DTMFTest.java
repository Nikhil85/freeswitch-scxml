package org.freeswitch.adapter;

import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public final class DTMFTest {


    @Test
    public void valueof() {

        DTMF.valueOfChar('1');
    }


    /**
     * Test of setFromString method, of class DTMF.
     */
    @Test
    public void testSetFromString() {
        String dtmtring = "0123456789#*";

        Set<DTMF> actualDTMet = DTMF.createCollectionFromString(dtmtring);
        Set<DTMF> expectedDTMet = EnumSet.allOf(DTMF.class);

        assertEquals("Failed to convert string to Set<DTMF> ",expectedDTMet, actualDTMet);

        actualDTMet = DTMF.createCollectionFromString(dtmtring + "#123414");
        assertEquals("Faild to convert string to Set<DTMF> ",expectedDTMet, actualDTMet);
    }



}
