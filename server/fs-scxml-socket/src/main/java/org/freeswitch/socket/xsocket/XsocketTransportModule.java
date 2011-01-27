package org.freeswitch.socket.xsocket;

import org.xsocket.connection.IDataHandler;
import com.google.inject.AbstractModule;
import org.freeswitch.socket.TcpServer;

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
        bind(IDataHandler.class).to(FSEventSocketHandler.class);
        bind(TcpServer.class).to(XsocketTcpServerImpl.class);
    }
}
