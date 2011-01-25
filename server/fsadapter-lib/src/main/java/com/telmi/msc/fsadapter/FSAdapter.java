package com.telmi.msc.fsadapter;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.telmi.msc.fsadapter.pool.ThreadPoolManager;
import com.telmi.msc.scxml.engine.ScxmlApplication;
import com.telmi.msc.fsadapter.transport.TcpServer;

/**
 *
 * @author jocke
 */
@Singleton
public final class FSAdapter implements Runnable, FSAdapterMBean, ShutdownHook {

    public static final String OBJECT_NAME = "com.telmi.msc.fsadapter:type=FsAdapter";
    private static final Logger LOG = LoggerFactory.getLogger(FSAdapter.class);
    private static final int WAIT_FOR_SHUTDOWN = 1000;
    private final ThreadPoolManager poolManager;
    private final TcpServer server;
    private final ScxmlApplication app;
    private boolean shutdownrequested = false;

    /**
     * Constructor used by IOC.
     *
     * @param application       The application to monitor.
     * @param tcpServer         The TcpServer to manage.
     * @param threadPoolManager The pools to start and stop.
     */
    @Inject
    FSAdapter(
            ScxmlApplication application,
            TcpServer tcpServer,
            ThreadPoolManager threadPoolManager) {

        this.app = application;
        this.server = tcpServer;
        this.poolManager = threadPoolManager;

    }

    /**
     * Register this class as an MBean.
     */
    public void registerAsMBean() {

        try {
            ObjectName objectName = new ObjectName(OBJECT_NAME);

            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(this, objectName);
        } catch (Exception ex) {
            LOG.error("oops! Could not create MBean. {} {}", ex.getCause(), ex.getMessage());
        }
    }

    @Override
    public void shutdown() {
        LOG.info("Try to shutdown ...");

        LOG.info("Shutdown monitoring Service");

        poolManager.shutdownAll();
        stopFSAdapter();

        LOG.info("shutdown completed");
        shutdownrequested = true;
    }

    @Override
    public void run() {

        startFSAdapter();

        while (!shutdownrequested) {
            try {
                Thread.sleep(WAIT_FOR_SHUTDOWN); //NOPMD
            } catch (InterruptedException e) {
                LOG.error("Oops! {}", e.getMessage());
            }
        }

    }

    @Override
    public void setScxmlCache(boolean use) {

        synchronized (app) {

            if (app.isCache() && !use) {
                app.clearCache();
            }
            app.setCache(use);
        }
    }

    @Override
    public void setServerPort(int port) {
        server.setPort(port);
    }

    @Override
    public int getServerPort() {
        return server.getPort();
    }

    @Override
    public boolean isScxmlCache() {
        return app.isCache();
    }

    @Override
    public int getNumberOfThreadsForApplicationPool() {
        return poolManager.getNumberOfThreadsForApplicationPool();
    }

    @Override
    public void setNumberOfThreadsForApplicationPool(int nThreads) {
        poolManager.setNumberOfThreadsForApplicationPool(nThreads);
    }

    @Override
    public void clearScxmlCache() {
        synchronized (app) {
            app.clearCache();
        }
    }

    @Override
    public void startFSAdapter() {
        LOG.info("start services");
        server.startServer();
    }

    @Override
    public void stopFSAdapter() {
        LOG.info("stop services");
        server.stopServer();
    }

    @Override
    public void reloadFSAdapter() {
        LOG.info("reload services");
        server.stopServer();
        server.startServer();
    }

    @Override
    public void statusFSAdapter() {
        LOG.info("status");
        server.status();
    }
}
