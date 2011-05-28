package org.freeswitch.socket.xsocket.module;

import org.freeswitch.adapter.api.session.InboundSessionFactory;
import org.freeswitch.config.spi.ConfigChangeListener;
import org.freeswitch.socket.xsocket.inbound.DefaultInboundSessionFactory;
import org.freeswitch.socket.xsocket.outbound.EventSocketHandler;
import org.freeswitch.socket.xsocket.outbound.XsocketTcpServerImpl;
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
        context.registerService(InboundSessionFactory.class.getName(), new DefaultInboundSessionFactory(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if(serverImpl != null) {
            serverImpl.stopServer();
        }
    }
}
