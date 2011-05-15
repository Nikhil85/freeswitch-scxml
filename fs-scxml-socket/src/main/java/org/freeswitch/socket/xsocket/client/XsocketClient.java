package org.freeswitch.socket.xsocket.client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.socket.SocketWriter;
import org.freeswitch.socket.xsocket.EventReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;

/**
 *
 * @author jocke
 */
public class XsocketClient implements IDataHandler, SocketWriter {

    private static final Logger LOG = LoggerFactory.getLogger(XsocketClient.class);
    private INonBlockingConnection connection;
    private EventReader reader;

    public XsocketClient() throws IOException {
        connection = new NonBlockingConnection(InetAddress.getByName("localhost"), 8021, this);
        reader = new EventReader();
    }

    public void connect() throws IOException {
        connection.write("auth ClueCon\n\n");
        connection.write("event plain CHANNEL_ANSWER " + Event.DTMF + " " + Event.CHANNEL_EXECUTE_COMPLETE + "\n\n");
        connection.write("api originate sofia/external/jocke@192.168.0.1:5070 100\n\n");
    }

    public void close() throws IOException {
        connection.close();
    }

    @Override
    public boolean onData(INonBlockingConnection inbc) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
       return true;
    }

    @Override
    public void write(String data) throws IOException {
        connection.write(data);
    }

    @Override
    public boolean isConnected() {
        return connection.isOpen();
    }

}
