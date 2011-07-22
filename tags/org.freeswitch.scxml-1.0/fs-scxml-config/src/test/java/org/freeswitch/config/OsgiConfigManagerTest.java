package org.freeswitch.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
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
public class OsgiConfigManagerTest {
    
    public static final String LOCALHOST = "localhost";
    public static final int PORT = 9696;
    public static final String SCXML_HOST = "scxml.host";
    public static final String TCP_PORT = "tcp.port";
    public static final String USE_CACHE = "scxml.use.cache";

    private OsgiConfigManager configManager;
    private Collection<ConfigChangeListener> listeners;
    
    @Before
    public void setUp() throws ConfigurationException {
        
        listeners = new HashSet<ConfigChangeListener>();  
        
        configManager = new OsgiConfigManager() {
            @Override
            protected Collection<? extends ConfigChangeListener> getListeners() {
                return listeners;
            }  
        };
        
        configManager.updated(null);
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testUpdate() throws ConfigurationException {
        final Hashtable managedProperties = getManagedProperties();
        
        configManager.updated(managedProperties);
        TcpConfigListener tcpConfigListener = new TcpConfigListener();
        listeners.add(tcpConfigListener);
        configManager.resultChanged(null);  
        assertTrue(tcpConfigListener.port == 9696);
        
        managedProperties.put(TCP_PORT, "9898");
        configManager.updated(managedProperties);
        assertTrue(tcpConfigListener.port == 9898);
        
    }
    
    private class TcpConfigListener implements ConfigChangeListener {
        
        private int port = 0;

        public int getPort() {
            return port;
        }
        
        @Override
        public Set<String> getKeys() {
            Set<String> keys = new HashSet<String>();
            keys.add(TCP_PORT);
            return keys;
        }

        @Override
        public String getValue(String key) {
            return Integer.toString(port);
        }

        @Override
        public void setValue(String key, String value) {
            this.port = Integer.valueOf(value);
        }
        
    }
    
    private Hashtable getManagedProperties() {
        Hashtable<String, String> table = new Hashtable<String, String>();
        table.put(SCXML_HOST, LOCALHOST);
        table.put(USE_CACHE, "true");
        table.put(TCP_PORT, Integer.toString(PORT));
        return table;
    }

}