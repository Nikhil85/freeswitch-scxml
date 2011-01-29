package org.freeswitch.socket.xsocket;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.freeswitch.socket.TcpServer;
import org.xsocket.connection.IDataHandler;
import static org.ops4j.peaberry.Peaberry.service;
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
       bind(export(IDataHandler.class)).toProvider(service(FSEventSocketHandler.class).export());
       bind(export(TcpServer.class)).toProvider(service(XsocketTcpServerImpl.class).export());
       bind(XsocketTcpServerImpl.class).in(Singleton.class);
    }
    
}
