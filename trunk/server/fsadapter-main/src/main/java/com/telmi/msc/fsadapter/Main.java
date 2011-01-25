package com.telmi.msc.fsadapter;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.telmi.msc.fsadapter.config.ConfigModule;
import com.telmi.msc.fsadapter.ivr.ApplicationModule;
import com.telmi.msc.fsadapter.pool.PoolModule;
import com.telmi.msc.scxml.engine.ScxmlModule;
import com.telmi.msc.fsadapter.transport.xsocket.XsocketTransportModule;

/**
 *
 * @author jocke
 */
public final class Main {

    private static final int PAUSE = 4000;

    /**
     * Our group.
     */
    public static final String GROUP_ID = "com.telmi";
    /**
     * This artifact.
     */
    public static final String ARTIFACT_ID = "tivrbridge";

    /**
     * This class should not be created.
     */
    private Main() {
    }

    /**
     * Creates the modules and starts the application.
     *
     * @param args Arguments to start the application
     */
    public static void main(final String[] args) {

        boolean daemonize = false;

        for (String arg : args) {

            if (arg.equalsIgnoreCase("DAEMON")) {
                daemonize = true;
            }
        }

        final Logger logger = LoggerFactory.getLogger(Main.class);
        try {
            int pid = DaemonTools.getPid();

            final Injector injector = Guice.createInjector(
                    new PoolModule(),
                    new FSAdapterModule(),
                    new ScxmlModule(),
                    new XsocketTransportModule(),
                    new ApplicationModule(),
                    new ConfigModule());

            FSAdapter monitorInstance = injector.getInstance(FSAdapter.class);
            
            try {
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                mbs.registerMBean(monitorInstance, new ObjectName(FSAdapter.OBJECT_NAME));
            } catch (Exception ex) {
                logger.error("oops! Could not create MBean. {} {}", ex.getCause(), ex.getMessage());
            }
            
            

            DaemonTools.addShutdownHook(monitorInstance);
            DaemonTools.checkpid(GROUP_ID, ARTIFACT_ID);

            logger.info("pid [{}]", pid);

            new Thread(monitorInstance).start();

            Thread.sleep(PAUSE);

            if (daemonize) {
                DaemonTools.deamonize();
            }


           logger.info("All services start up successfully");
//
//            synchronized (monitorInstance) {
//                TODO what is most correct.
//                while (!Thread.currentThread().isInterrupted()) {
//                if (monitorInstance.)
//                    monitorInstance.wait();
//                }
//            }
//
        } catch (Exception e) {
            logger.error("Oops! Startup Failed", e);
        }
    }
}
