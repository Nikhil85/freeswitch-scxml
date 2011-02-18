package org.freeswitch.socket.xsocket;

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;
import java.util.Date;
import org.freeswitch.socket.TcpServer;

/**
 *
 * @author Jocke, Kristofer
 */
public final class XsocketTcpServerImpl implements TcpServer {

    private static final Logger LOG =
            LoggerFactory.getLogger(XsocketTcpServerImpl.class);

    private final IDataHandler iDataHandler;

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
    public XsocketTcpServerImpl(IDataHandler dataHandler) {
        this.iDataHandler = dataHandler;
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
    public void startServer() {
        LOG.info("Try to start Server ...");
        state = SERVERSTATE.START;

        if (iServer != null && iServer.isOpen()) {
            LOG.info("Server is already started");
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
            //iServer.setWorkerpool(threadPoolManager.getWorkerPool());
        }

        try {
            iServer.start();
            LOG.info("Server started {} " , new Date());
        } catch (IOException ex) {
            LOG.error("Failed to start the server" , ex.getMessage());
        }
    }

    

    @Override
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
        //threadPoolManager.shutdownAll();
        stopServer();

        state = SERVERSTATE.START;
    }

    @Override
    public String status() {
      return this.state.name();
    }

}

