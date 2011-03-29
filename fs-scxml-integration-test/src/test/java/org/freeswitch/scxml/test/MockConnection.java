package org.freeswitch.scxml.test;

import java.util.Map;
import java.io.IOException;
import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.IBlockingConnection;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class MockConnection {
    
    private IBlockingConnection ibc;
    private String delimiter = "\n\n";

    public MockConnection(int port) throws IOException {
        ibc = new BlockingConnection("localhost", port);
    }
    
    public MockConnection connect() throws IOException {
        assertTrue(ibc.readStringByDelimiter(delimiter).equals("connect"));
        assertTrue(ibc.readStringByDelimiter(delimiter).equals("myevents"));
        assertTrue(ibc.readStringByDelimiter(delimiter).contains("filter Event-Name"));
        assertTrue(ibc.readStringByDelimiter(delimiter).contains("filter Event-Name"));
        return this;
    }

    public void close() throws IOException {
        ibc.close();
    }
    
    public MockConnection fireEvent(String event, Map<String, String> vars) {
        return this;
    }
   
}
