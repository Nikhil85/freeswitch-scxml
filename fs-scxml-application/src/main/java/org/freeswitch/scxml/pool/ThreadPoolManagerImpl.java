package org.freeswitch.scxml.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.freeswitch.scxml.ThreadPoolManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.freeswitch.config.spi.ConfigChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public final class ThreadPoolManagerImpl implements ThreadPoolManager, ConfigChangeListener {
    
    private static final String SCHEDULER_CORE_POOL_SIZE = "scheduler.corePoolSize";
    private static final String APP_POOL_N_THREADS = "appPool.nThreads";
    private static final String WORK_POOL_N_THREADS = "workPool.nThreads";
    
    private static final Map<String, String> KEYS = new HashMap<String, String>(3);

    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolManagerImpl.class);
    private final ScheduledThreadPoolExecutor schedulerPool;
    private final ThreadPoolExecutor workerPool;
    private final ThreadPoolExecutor applicationPool;
    
    static {
        KEYS.put(APP_POOL_N_THREADS, "");
        KEYS.put(WORK_POOL_N_THREADS, "");
        KEYS.put(SCHEDULER_CORE_POOL_SIZE, "");
    }

    /**
     * Create a new instance of ThreadPoolManager.
     *
     * @param corePoolSize The size of the pool to execute read and writes.
     * @param nThreads     The size of the pool to execute applications.
     *
     */
    public ThreadPoolManagerImpl() {
        this.schedulerPool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        this.workerPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.applicationPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    public int getNumberOfThreadsForApplicationPool() {
        return applicationPool.getCorePoolSize();
    }

    @Override
    public Set<String> getKeys() {
        return KEYS.keySet();
    }

    @Override
    public String getValue(String key) {
        return KEYS.get(key);
    }

    @Override
    public void setValue(String key, String value) {
        KEYS.put(key, value);
        
        if(key.equals(APP_POOL_N_THREADS)) {
           applicationPool.setCorePoolSize(Integer.valueOf(value));               
        
        } else if(key.equals(WORK_POOL_N_THREADS)) {
           workerPool.setCorePoolSize(Integer.valueOf(value));
        
        } else if(key.equals(SCHEDULER_CORE_POOL_SIZE)) {
           schedulerPool.setCorePoolSize(Integer.valueOf(value));
        }
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
