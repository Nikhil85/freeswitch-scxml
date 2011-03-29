package org.freeswitch.socket.xsocket;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;
import java.util.Date;
import java.util.HashSet;
import org.freeswitch.config.spi.ConfigChangeListener;
import org.freeswitch.scxml.ThreadPoolManager;
import org.freeswitch.socket.TcpServer;
import org.openide.util.Lookup;

/**
 *
 * @author Jocke
 */
public final class XsocketTcpServerImpl implements TcpServer, ConfigChangeListener {
    
    public static final String TCP_PORT = "tcp.port";

    private static final Logger LOG = LoggerFactory.getLogger(XsocketTcpServerImpl.class);
    private final IDataHandler iDataHandler;
    private IServer iServer;
    private int port = 9696;
    private static final Set<String> CONFIG_KEYS = new HashSet<String>(1);
    
    static {
        CONFIG_KEYS.add(TCP_PORT);
    }

    @Override
    public Set<String> getKeys() {
        return CONFIG_KEYS;
    }

    @Override
    public String getValue(String key) {
        return Integer.toString(port);
    }

    @Override
    public void setValue(String key, String value) {
        stopServer();
        this.port = Integer.valueOf(value);
        startServer();
    }
    
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
    public void startServer() {
        LOG.info("Try to start Server ...");

        if (iServer != null && iServer.isOpen()) {
            LOG.info("Server is already started");
            return;
        }

        if  (iServer == null) {
            try {
                iServer = new Server(port, iDataHandler);
            } catch (UnknownHostException e) {
                LOG.error(e.getMessage());
                return;
               
            } catch (IOException e) {
                LOG.error(e.getMessage());
                return;
            }
        }


        if (iServer.getWorkerpool() == null) {
            ThreadPoolManager poolManager = Lookup.getDefault().lookup(ThreadPoolManager.class);
            
            if(poolManager != null) {
              iServer.setWorkerpool(poolManager.getWorkerPool());   
            } else {
                LOG.warn("No thread pool manager found will use default");
            }
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

        if (iServer == null) {
            LOG.debug("Server is not initilized");
            return;
        }

        if (!iServer.isOpen()) {
            LOG.debug("Server is not open");
        }

        try {
            iServer.close();
            iServer = null;
           
        } catch (Exception ex) {
            LOG.warn("Oops! {}", ex.getMessage());
        }
    }

}

