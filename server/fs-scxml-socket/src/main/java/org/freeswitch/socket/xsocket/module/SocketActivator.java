package org.freeswitch.socket.xsocket.module;

import java.beans.EventSetDescriptor;
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
        XsocketTcpServerImpl serverImpl = new XsocketTcpServerImpl(handler);
        
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        
        if(serverImpl != null) {
            serverImpl.stopServer();
        }
    }

}
