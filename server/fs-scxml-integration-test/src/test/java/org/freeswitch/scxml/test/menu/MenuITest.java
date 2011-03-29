package org.freeswitch.scxml.test.menu;

import org.freeswitch.scxml.test.MockConnection;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class MenuITest {
    
    
    private MockConnection con;
    
    @Before
    public void setUp() throws IOException {
        //con = new MockConnection(9797);
    }

    @After
    public void tearDown() throws IOException {
        //con.close();
    }
    
    @Test
    @Ignore
    public void testMatch() throws IOException {
        con.connect();
    }

}