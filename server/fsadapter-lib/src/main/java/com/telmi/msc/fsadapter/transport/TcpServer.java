package com.telmi.msc.fsadapter.transport;

/**
 *
 * @author jocke
 */
public interface TcpServer {

    /**
     * Set the port to listen on.
     *
     * @param port The port number.
     */
    void setPort(int port);

    /**
     *
     * @return the current port.
     */
    int getPort();

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


    /**
     * Reload the server.
     */
    void reload();

    /**
     *
     * @return The current status of the server.
     */
    String status();
}
