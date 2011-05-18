package org.freeswitch.socket.xsocket.inbound;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.socket.xsocket.EventMatcher;
import org.freeswitch.socket.xsocket.EventReader;
import org.freeswitch.socket.xsocket.XsocketEventProducer;
import org.freeswitch.socket.xsocket.XsocketSocketWriter;
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
public class XsocketClient implements CommandExecutor, EventMatcher, IDataHandler {

    private static final Logger LOG = LoggerFactory.getLogger(XsocketClient.class);
    private INonBlockingConnection connection;
    private EventReader reader;
    private String dialString;
    private XsocketEventProducer producer;
    private XsocketSocketWriter writer;

    public XsocketClient(String dialString) {
        this.dialString = dialString;
        this.connection = null;
        this.reader = new EventReader();
        this.connection = createConnection();
        this.writer = new XsocketSocketWriter(connection);

    }

    private NonBlockingConnection createConnection() {
        try {
            return new NonBlockingConnection(InetAddress.getByName("localhost"), 8021, this);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void setProducer(XsocketEventProducer producer) {
        this.producer = producer;
    }

    public void connect() throws IOException {
        connection.write("auth ClueCon\n\n");
        connection.write("event plain CHANNEL_ANSWER DTMF CHANNEL_EXECUTE_COMPLETE\n\n");
        connection.write("api originate " + dialString + " 100\n\n");
    }

    public void close() throws IOException {
        connection.close();
    }

    @Override
    public boolean onData(INonBlockingConnection inbc) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {

        String readEvent = reader.readEvent(connection);

        if (readEvent != null) {
            producer.onDataEvent(readEvent);
        }

        return true;
    }

    @Override
    public void execute(String data) throws IOException {
        writer.execute(data);
    }

    @Override
    public boolean isReady() {
        return writer.isConnected();
    }

    @Override
    public boolean matches(String event) {
        return writer.matches(event);
    }
    
    public static void main(String [] args) {
        System.out.println("hello");
    }
}