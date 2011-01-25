package com.telmi.msc.fsadapter;

/**
 * Add a shutdown hook to know when the system is shutting down.
 * Used by DaemonTools
 *
 * @author jocke
 */
public interface ShutdownHook {

    /**
     * Called when the server should shutdown.
     */
    void shutdown();
}
