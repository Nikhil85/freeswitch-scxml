package org.freeswitch.scxml.application.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 * @author joe
 */
public interface ThreadPoolManager {

    /**
     *
     * Get the application pool.
     *
     * @return The single instance of application pool.
     */
    ExecutorService getApplicationPool();

    /**
     * Get the scheduler for scheduling of tasks.
     *
     * @return The single instance of scheduler.
     */
    ScheduledExecutorService getScheduler();

    /**
     * Shutdown all executors managed by this ThreadPoolManager.
     */
    void shutdownAll();

    public ExecutorService getWorkerPool();

}
