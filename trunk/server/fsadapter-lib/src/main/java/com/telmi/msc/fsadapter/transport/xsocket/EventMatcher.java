/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.telmi.msc.fsadapter.transport.xsocket;

/**
 *
 * @author jocke
 */
public interface EventMatcher {

    /**
     * Test if an event matches the one expected after a write on the 
     * socket
     *
     *
     * @param event The event to match against.
     * 
     * @return true If the string matches the expected event false otherwise.
     *
     */
    boolean matches(String event);

}
