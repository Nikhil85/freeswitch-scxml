package org.freeswitch.adapter.api;

import java.io.IOException;

/**
 *
 * @author joe
 */
public interface CommandExecutor {
    
   
    void execute(String data) throws IOException;

    /**
     * Test if the channel is open or not.
     *
     * @return True if the channel is open false otherwise.
     */
    boolean isReady();

}
