package com.telmi.msc.freeswitch;

import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public final class DTMFMessageTest {


    @Test
    public void valueof() {

        DTMFMessage.valueOfChar('1');
    }


    /**
     * Test of setFromString method, of class DTMF.
     */
    @Test
    public void testSetFromString() {
        String dtmfstring = "0123456789#*";

        Set<DTMFMessage> actualDTMFSet = DTMFMessage.createCollectionFromString(dtmfstring);
        Set<DTMFMessage> expectedDTMFSet = EnumSet.allOf(DTMFMessage.class);

        assertEquals("Failed to convert string to Set<DTMF> ",expectedDTMFSet, actualDTMFSet);

        actualDTMFSet = DTMFMessage.createCollectionFromString(dtmfstring + "#123414");
        assertEquals("Faild to convert string to Set<DTMF> ",expectedDTMFSet, actualDTMFSet);
    }


//    /**
//     * Test of fromString method, of class DTMF.
//     */
//    @Test
//    public void testlistFromString() {
//
//        String dtmfs = "0123456789#*";
//
//        List<DTMFMessage> dtmfList = DTMFMessage.listFromString(dtmfs);
//
//        assertTrue("Faild to convert string to Set<DTMF> ",
//                dtmfList.size() == 12);
//
//        //Add som duplicates
//        List<DTMFMessage> newList = DTMFMessage.listFromString(dtmfs + "#123414");
//
//        assertTrue("Faild to convert string to Set<DTMF> ",
//                newList.size() == 19);
//    }

}
