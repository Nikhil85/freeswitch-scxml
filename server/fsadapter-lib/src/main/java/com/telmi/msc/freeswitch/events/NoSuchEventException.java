/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.telmi.msc.freeswitch.events;

/**
 *
 * @author kristofer
 */
class NoSuchEventException extends Exception {

    public NoSuchEventException() {
    }

    NoSuchEventException(Exception ex) {
        super(ex);
    }

    NoSuchEventException(String string) {
        super(string);
    }

    NoSuchEventException(String string, Exception ex) {
        super(string, ex);
    }

}
