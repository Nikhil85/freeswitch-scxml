package org.freeswitch.adapter.api;

/**
 *
 * @author jocke
 */
public class HangupException extends Exception {
    
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>HangupException</code> without detail message.
     */
    public HangupException() {
    }

    /**
     * Constructs an instance of <code>HangupException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public HangupException(String msg) {
        super(msg);
    }
}
