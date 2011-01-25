/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telmi.msc.freeswitch;

/**
 * http://wiki.freeswitch.org/wiki/Hangup_causes
 *
 * @author kristofer
 */
public enum Q850HangupCauses {

    /**
     * Unspecified. No other cause codes applicable.
     *
     * This is usually given by the router when none of the other codes apply.
     * This cause usually occurs in the same type of situations as cause 1,
     * cause 88, and cause 100.
     */
    UNSPECIFIED(0),
    /**
     * Unallocated (unassigned) number [Q.850 value 1]
     *
     * This cause indicates that the called party cannot be reached because,
     * although the called party number is in a valid format,
     * it is not currently allocated (assigned).
     */
    UNALLOCATED_NUMBER(1),
    /**
     * incompatible destination [Q.850]
     *
     * This cause indicates that the equipment sending this cause has
     * received a request to establish a call which has low layer compatibility,
     * high layer compatibility or other compatibility attributes
     * (e.g. data rate) which cannot be accommodated.
     */
    INCOMPATIBLE_DESTINATION(88),
    /**
     * Invalid information element contents [Q.850].
     *
     * This cause indicates that the equipment sending this cause has received
     * and information element which it has implemented; however,
     * one or more fields in the I.E. are coded in such a way which has not been
     * implemented by the equipment sending this cause.
     */
    INVALID_IE_CONTENTS(100),
    /**
     * PROGRESS_TIMEOUT
     * See: progress_timeout
     */
    PROGRESS_TIMEOUT(607);
    private final int itucode;

    Q850HangupCauses(int code) {
        itucode = code;
    }

    public int ituQ850Code() {
        return itucode;
    }
}
