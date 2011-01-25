package com.telmi.msc.fsadapter.transport;

/**
 *
 * @author jocke
 */
public interface ServerSessionListener {

    /**
     * An method that gets called when there is data
     * from the socket.
     * <p>
     *  The data should start with Event-Name:.
     *  A warning should be raised if doesn't.
     * </p>
     *
     * @param data The data that arrived.
     */
    void onDataEvent(String data);

    /**
     * Called when the socket closed.
     */
    void onClose();
}
