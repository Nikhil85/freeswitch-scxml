package org.freeswitch.socket.xsocket;

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.freeswitch.scxml.application.ThreadPoolManager;
import org.freeswitch.socket.TcpServer;
import org.ops4j.peaberry.activation.Start;
import org.ops4j.peaberry.activation.Stop;

/**
 *
 * @author Jocke, Kristofer
 */
@Singleton
public final class XsocketTcpServerImpl implements TcpServer {

    private static final Logger LOG =
            LoggerFactory.getLogger(XsocketTcpServerImpl.class);

    private final IDataHandler iDataHandler;
    private final ThreadPoolManager threadPoolManager;

    private IServer iServer;
    
    private int port = 9696;
    /** The internal state. **/
    private enum SERVERSTATE { START, STOP, SHUTDOWN };

    private SERVERSTATE state = SERVERSTATE.START;


    /**
     *
     * Create a new instance of XsocketTcpServerImpl.
     *
     * @param dataHandler The data handler to use.
     * @param poolManager Handles all Thread pools.
     * @param tcpPort     The port to listen on.
     */
    @Inject
    XsocketTcpServerImpl(
            IDataHandler dataHandler,
            ThreadPoolManager poolManager) {
        this.iDataHandler = dataHandler;
        this.threadPoolManager = poolManager;
    }

    @Override
    public void setPort(int tcpPort) {
        this.port = tcpPort;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    @Start
    public void startServer() {
        System.out.println("Try to start Server ...");
        state = SERVERSTATE.START;

        if (iServer != null && iServer.isOpen()) {
            System.out.println("Server is already started");
            return;
        }

        if  (iServer == null) {
            try {
                iServer = new Server(port, iDataHandler);
            } catch (UnknownHostException e) {
                System.out.println(e.getMessage());
                return;
               
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }
        }


        if (iServer.getWorkerpool() == null) {
            iServer.setWorkerpool(threadPoolManager.getWorkerPool());
        }

        try {
            System.out.println("Start server now");
            iServer.start();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    @Override
    @Stop
    public void stopServer() {
        LOG.info("Try to stop Server ...");
        state = SERVERSTATE.STOP;

        if (iServer == null) {
            LOG.debug("Server is not initilized");
            return;
        }

        if (!iServer.isOpen()) {
            LOG.debug("Server is not open");
        }

        try {
            iServer.close();
        } catch (Exception ex) {
            LOG.warn("Oops! {}", ex.getMessage());
        }
    }

    @Override
    public void reload() {

        state = SERVERSTATE.STOP;
        threadPoolManager.shutdownAll();
        stopServer();

        state = SERVERSTATE.START;
    }

    @Override
    public String status() {
      return this.state.name();
    }

}

