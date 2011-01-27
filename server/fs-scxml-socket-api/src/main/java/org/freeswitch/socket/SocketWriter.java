package org.freeswitch.socket;

import java.io.IOException;

/**
 *
 * @author jocke
 */
public interface SocketWriter {

    /**
     * Write a String to the socket.
     *
     * @param data The Data to write.
     *
     */
    void write(String data) throws IOException;

    /**
     * Test if the channel is open or not.
     *
     * @return True if the channel is open false otherwise.
     */
    boolean isConnected();
}
