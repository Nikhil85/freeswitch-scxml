package org.freeswitch.socket.xsocket.module;

import java.beans.EventSetDescriptor;
import java.util.Collection;
import org.freeswitch.adapter.api.SessionFactory;
import org.freeswitch.scxml.ThreadPoolManager;
import org.freeswitch.socket.xsocket.XsocketTcpServerImpl;
import org.openide.util.Lookup;
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
        //EventSocketHandler handler = new EventSocketHandler();
        //XsocketTcpServerImpl serverImpl = new XsocketTcpServerImpl(handler);
        //serverImpl.startServer();
        ThreadPoolManager lookup = Lookup.getDefault().lookup(ThreadPoolManager.class);
        SessionFactory lookup1 = Lookup.getDefault().lookup(SessionFactory.class);
        
        System.out.println(lookup);
        System.out.println(lookup1);
        
        
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        
        if(serverImpl != null) {
            serverImpl.stopServer();
        }
    }

}
