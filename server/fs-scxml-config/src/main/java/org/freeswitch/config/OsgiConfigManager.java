package org.freeswitch.config;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.freeswitch.config.spi.ConfigChangeListener;
import org.freeswitch.config.spi.ConfigManager;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author jocke
 */
public class OsgiConfigManager implements ConfigManager, ManagedService {

    private static final Map<String, Object> PROPS = new HashMap<String, Object>();
    private static final Map<String, List<ConfigChangeListener>> LISTENERS = new HashMap<String, List<ConfigChangeListener>>();

    @Override
    public String get(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getBoolean(String key) {
      return false;
    }

    @Override
    public int getInt(String key) {
       
        if(PROPS.containsKey(key)) {
           return Integer.valueOf((String)PROPS.get(key));
       
       } else {
           return 0;
       }
        
    }

    @Override
    public void addConfigChangeListener(String key, ConfigChangeListener changeListener) {
        
        if (LISTENERS.containsKey(key)) {
            LISTENERS.get(key).add(changeListener);
        
        } else {
            List<ConfigChangeListener> ls = new ArrayList<ConfigChangeListener>();
            ls.add(changeListener);
            LISTENERS.put(key, ls);
        }
    }
    
    private void fireConfigChange(String key, Object value) {
        
        List<ConfigChangeListener> ccls = LISTENERS.get(key);
        
        for (ConfigChangeListener listener : ccls) {
            listener.configChange(key, value);
        }
    }

    @Override
    public void updated(Dictionary dict) throws ConfigurationException {

        Enumeration keys = dict.keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
        }

    }
}
