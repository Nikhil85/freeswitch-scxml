package com.telmi.msc.fsadapter.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 *
 * @author jocke
 */
@Singleton
public final class ThreadPoolManager  {

    private static final Logger LOG =
            LoggerFactory.getLogger(ThreadPoolManager.class);


    private final ScheduledExecutorService schedulerPool;
    private final ExecutorService workerPool;
    private final ThreadPoolExecutor applicationPool;

    /**
     * Create a new instance of ThreadPoolManager.
     *
     * @param corePoolSize The size of the pool to execute read and writes.
     * @param nThreads     The size of the pool to execute applications.
     *
     */
    @Inject
    ThreadPoolManager(
            @Named("scheduler.corePoolSize") int corePoolSize,
            @Named("appPool.nThreads") int nThreads) {

        this.schedulerPool = Executors.newScheduledThreadPool(corePoolSize);
        this.workerPool = Executors.newCachedThreadPool();

        this.applicationPool =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
    }

    public int getNumberOfThreadsForApplicationPool() {
        return applicationPool.getCorePoolSize();
    }
    
    /**
     * Set the application pool size.
     *
     * @param nThreads Number of threads in the pool.
     */
    public void setNumberOfThreadsForApplicationPool(int nThreads) {
        applicationPool.setCorePoolSize(nThreads);
    }

    /**
     * Get the scheduler for scheduling of tasks.
     *
     * @return The single instance of scheduler.
     */
    public ScheduledExecutorService getScheduler() {
        return schedulerPool;
    }

    /**
     *
     * Get the worker pool.
     *
     * @return The single instance of the worker pool.
     */
    public ExecutorService getWorkerPool() {
        return workerPool;
    }

    /**
     *
     * Get the application pool.
     *
     * @return The single instance of application pool.
     */
    public ExecutorService getApplicationPool() {
        return applicationPool;
    }

    /**
     * Shutdown a ExecutorService.
     *
     * @param executorService The service to shutdown.
     * @param timeout         How long to wait before doing a forced shutdown.
     * @param unit            The time unit to be used.
     */
    public void shutDownExecutorService(
            final ExecutorService executorService,
            long timeout,
            TimeUnit unit) {

        if (executorService != null && !executorService.isShutdown()) {
            LOG.debug("Shutdown ExecutorService '{}'", executorService);

            try {
                executorService.shutdown();
                if (executorService.awaitTermination(timeout, unit)) {
                    LOG.debug("executorServcie terminated successfully");
                } else {
                    LOG.warn("timeout occured while waiting for "
                            + "executor service to shutdown[{} {}]",
                            timeout, unit.toString());
                }

            } catch (InterruptedException e1) {
                LOG.error("Oops! {}", e1.getMessage());
            }
        }
    }


    /**
     * Shutdown all executors managed by this ThreadPoolManager.
     */
    public void shutdownAll() {
        LOG.info("Shutdown all executors");
        shutDownExecutorService(schedulerPool, 10, TimeUnit.SECONDS);
        shutDownExecutorService(workerPool, 10, TimeUnit.SECONDS);
        shutDownExecutorService(applicationPool, 1, TimeUnit.MILLISECONDS);
    }

}
