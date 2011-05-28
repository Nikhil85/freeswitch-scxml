package org.freeswitch.socket.xsocket.inbound;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.socket.xsocket.EventReader;
import org.freeswitch.socket.xsocket.EventManger;
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
public class XsocketClient implements IDataHandler {

    private static final Logger LOG = LoggerFactory.getLogger(XsocketClient.class);
    private INonBlockingConnection connection;
    private EventReader reader;
    private String dialString;
    private EventManger eventManger;

    public XsocketClient(String dialString, EventManger eventManger) {
        this.dialString = dialString + " &park()";
        this.connection = null;
        this.reader = new EventReader();
        this.connection = createConnection();
        this.eventManger = eventManger;
        this.eventManger.setConnection(connection);
    }

    private NonBlockingConnection createConnection() {
        try {
            return new NonBlockingConnection(InetAddress.getByName("localhost"), 8021, this);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
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

        Event evt = reader.readEvent(connection);

        if (evt != null) {
            eventManger.onEvent(evt);
        }
        
       return true;
    }


}
