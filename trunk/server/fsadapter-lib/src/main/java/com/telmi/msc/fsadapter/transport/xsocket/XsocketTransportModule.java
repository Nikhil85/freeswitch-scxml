package com.telmi.msc.fsadapter.transport.xsocket;

import com.telmi.msc.fsadapter.transport.TcpServer;
import org.xsocket.connection.IDataHandler;
import com.google.inject.AbstractModule;

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
