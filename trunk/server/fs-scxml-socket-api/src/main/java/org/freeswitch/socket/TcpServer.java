package org.freeswitch.socket;

/**
 *
 * @author jocke
 */
public interface TcpServer {

    /**
     * This will start TCP Server.
     *
     * <p>
     *   If the server is already running it will do nothing
     *   stop the current server and try again.
     * </p>
     *
     */
    void startServer();

    /**
     * Stop the server.
     *
     * <P>
     *  This will cause the server to stop listen for requests
     *  butt the application will still be running. To stop the hole
     *  application use shutdown.
     * </p>
     *
     */
    void stopServer();

}
