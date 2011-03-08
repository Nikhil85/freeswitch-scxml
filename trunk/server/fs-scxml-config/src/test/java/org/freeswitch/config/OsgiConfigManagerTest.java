package org.freeswitch.config;

import java.util.Hashtable;
import org.freeswitch.config.spi.ConfigChangeListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.osgi.service.cm.ConfigurationException;

/**
 *
 * @author jocke
 */
@Ignore
public class OsgiConfigManagerTest {
    
    public static final String LOCALHOST = "localhost";
    public static final int PORT = 9696;
    public static final String SCXML_HOST = "scxml.host";
    public static final String TCP_PORT = "tcp.port";
    public static final String USE_CACHE = "scxml.use.cache";

    private OsgiConfigManager configManager;
    
    @Before
    public void setUp() throws ConfigurationException {
        configManager = new OsgiConfigManager();
        configManager.updated(null);
    }

    @After
    public void tearDown() {
    }

    
    
    private Hashtable getManagedProperties() {
        Hashtable<String, String> table = new Hashtable<String, String>();
        table.put(SCXML_HOST, LOCALHOST);
        table.put(USE_CACHE, "true");
        table.put(TCP_PORT, Integer.toString(PORT));
        return table;
    }

}