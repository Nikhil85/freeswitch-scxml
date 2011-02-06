package org.freeswitch.socket.xsocket.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.freeswitch.adapter.SessionFactory;
import org.freeswitch.scxml.application.ApplicationLauncher;
import org.freeswitch.scxml.application.ThreadPoolManager;
import org.freeswitch.socket.TcpServer;
import org.freeswitch.socket.xsocket.FSEventSocketHandler;
import org.freeswitch.socket.xsocket.XsocketTcpServerImpl;
import org.xsocket.connection.IDataHandler;
import static org.ops4j.peaberry.Peaberry.*;
import static org.ops4j.peaberry.util.TypeLiterals.export;

/**
 *
 * @author jocke
 */
public final class XsocketTransportModule extends AbstractModule {

    /**
     * Configure the transport module.
     */
    @Override
    protected void configure() {
        bind(export(ModuleStarter.class)).toProvider(service(ModuleStarter.class).export());
        bind(ModuleStarter.class).in(Singleton.class); 
        bind(ThreadPoolManager.class).toProvider(service(ThreadPoolManager.class).single());
        bind(SessionFactory.class).toProvider(service(SessionFactory.class).single());
        bind(ApplicationLauncher.class).toProvider(service(ApplicationLauncher.class).single());
        bind(IDataHandler.class).to(FSEventSocketHandler.class);
        bind(TcpServer.class).to(XsocketTcpServerImpl.class).in(Singleton.class);
    }

}
