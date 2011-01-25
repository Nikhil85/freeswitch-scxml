package com.telmi.msc.fsadapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author jocke
 */
public final class DaemonTools {

    private static final int STATUS_IO_E = 202;
    private static final int STATUS_NO_WRITE = 201;

    private static final  Logger LOG =
        LoggerFactory.getLogger(DaemonTools.class);
    /**
     * The name of the pid file.
     */
    private static final String PIDFILE_PROPERTY = "pidfile";

    /**
     * private because it only contains static methods.
     */
    private DaemonTools() {
    }

    /**
     * Check pidfile.
     *
     * @param groupId    Id of group.
     *
     * @param artifactId Id of artifact.
     */
    public static void checkpid(final String groupId, final String artifactId) {

        String path = System.getProperty(PIDFILE_PROPERTY);

        if (path == null) {
            return;
        }

        File pidfile = new File(path);

        if (pidfile.exists()) {
            LOG.info("pid file already exist. Application already started.");
            System.exit(STATUS_NO_WRITE); //NOPMD
        }

        String parentpath = pidfile.getParent();
        File parentfile = new File(parentpath);

        if (!parentfile.exists() && !parentfile.mkdirs()) {
            LOG.error("Can not create parent folder for pidfile. '{}'"
                , pidfile);
            System.exit(STATUS_NO_WRITE);
        }

        try {

            boolean created = pidfile.createNewFile();

            if (!created) {
                LOG.warn("Could not create PID file '{}' ", pidfile.getName());
                return;
            }

            pidfile.deleteOnExit();
        } catch (IOException e) {
            System.err.println("Oops! Could not create file. " //NOPMD
                    + e.getMessage());
            System.exit(STATUS_IO_E);
        }

        try {
            if (!pidfile.canWrite()) {
                LOG.error("Cant write to pid file '" + pidfile + "'.");
                System.exit(STATUS_NO_WRITE); //NOPMD
            }

            FileOutputStream fos = new FileOutputStream(pidfile, false);
            int pid = getPid();
            fos.write(Integer.toString(pid).getBytes());
            fos.flush();
            fos.close();

        } catch (IOException e) {
            System.err.println("Oops! " + e.getMessage()); //NOPMD
            System.exit(STATUS_IO_E); //NOPMD
        }

    }

    /**
     * Close stream out and err stream out.
     */
    public static void deamonize() {
        System.out.close();
        System.err.close();
    }

    /**
     *
     * Add new ShutDownHook.
     *
     * @param shutdownHook The shutdownhook to add.
     */
    public static void addShutdownHook(final ShutdownHook shutdownHook) {
        Runtime.getRuntime().addShutdownHook(
            new Thread() {
                @Override
                public void run() {
                    shutdownHook.shutdown();
                }
            }
        );
    }

    /**
     * Get the pid used by the application.
     *
     * @return The process id .
     */
    public static Integer getPid() {
        final RuntimeMXBean rtb = ManagementFactory.getRuntimeMXBean();
        final String processName = rtb.getName();
        final Integer pid = tryPattern1(processName);

        return pid;
    }

    /**
     * Find process by name.
     *
     * @param  processName The name of the process to find.
     *
     * @return The number of the process or null.
     *
     */
    private static Integer tryPattern1(String processName) {
        Integer result = null;

        /* tested on: */
        /* - windows xp sp 2, java 1.5.0_13 */
        /* - mac os x 10.4.10, java 1.5.0 */
        /* - debian linux, java 1.5.0_13 */
        /* all return pid@host, e.g 2204@antonius */

        Pattern pattern = Pattern.compile(
                "^([0-9]+)@.+$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(processName);
        if (matcher.matches()) {
            result = Integer.valueOf(matcher.group(1));
        }
        return result;
    }

}
