/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telmi.msc.freeswitch.enums;

/**
 *
 * @author kristofer
 */
public enum SayMethod {

    NA("N/A"),
    PRONOUNCED("PRONOUNCED"),
    ITERATED("ITERATED"),
    COUNTED("COUNTED");

    private final String sayMethod;
    SayMethod(String method) {
        sayMethod = method;
    }
}
