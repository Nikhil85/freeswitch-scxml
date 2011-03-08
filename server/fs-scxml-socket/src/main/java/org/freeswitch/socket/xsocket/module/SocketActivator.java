package org.freeswitch.socket.xsocket.module;

import org.freeswitch.config.spi.ConfigChangeListener;
import org.freeswitch.socket.xsocket.EventSocketHandler;
import org.freeswitch.socket.xsocket.XsocketTcpServerImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author jocke
 */
public class SocketActivator implements BundleActivator {
    
    private XsocketTcpServerImpl serverImpl;
    
    @Override
    public void start(BundleContext context) throws Exception {
        EventSocketHandler handler = new EventSocketHandler();
        serverImpl = new XsocketTcpServerImpl(handler);
        serverImpl.startServer();
        context.registerService(ConfigChangeListener.class.getName(), serverImpl, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        
        if(serverImpl != null) {
            serverImpl.stopServer();
        }
    }

}
