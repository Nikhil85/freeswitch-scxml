package org.freeswitch.scxml.pool;

import org.freeswitch.scxml.ThreadPoolManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author jocke
 */
public final class ThreadPoolManagerImpl implements ThreadPoolManager {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolManagerImpl.class);
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
    public ThreadPoolManagerImpl() {        
        this.schedulerPool = Executors.newScheduledThreadPool(Integer.valueOf(200));
        this.workerPool = Executors.newCachedThreadPool();
        this.applicationPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.valueOf(200));
    }

    public int getNumberOfThreadsForApplicationPool() {
        return applicationPool.getCorePoolSize();
    }

    public void start() {
        LOG.info("Starting thread pools " );
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
    @Override
    public ScheduledExecutorService getScheduler() {
        return schedulerPool;
    }

    /**
     *
     * Get the worker pool.
     *
     * @return The single instance of the worker pool.
     */
    @Override
    public ExecutorService getWorkerPool() {
        return workerPool;
    }

    /**
     *
     * Get the application pool.
     *
     * @return The single instance of application pool.
     */
    @Override
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

    @Override
    public void shutdownAll() {
        LOG.info("Shutting down all thread pools");
        shutDownExecutorService(schedulerPool, 10, TimeUnit.SECONDS);
        shutDownExecutorService(workerPool, 10, TimeUnit.SECONDS);
        shutDownExecutorService(applicationPool, 1, TimeUnit.MILLISECONDS);
    }
}
