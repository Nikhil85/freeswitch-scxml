package org.freeswitch.config;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.freeswitch.config.spi.ConfigChangeListener;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class OsgiConfigManager implements ManagedService, LookupListener {

    private Lookup.Result<ConfigChangeListener> result;
    private static final Map<String, String> PROPS = new ConcurrentHashMap<String, String>();
    private static final Logger LOG = LoggerFactory.getLogger(OsgiConfigManager.class);

    @Override
    public void updated(Dictionary dict) throws ConfigurationException {

        if (result == null) {
            result = Lookup.getDefault().lookupResult(ConfigChangeListener.class);
            result.addLookupListener(this);
            result.allInstances();
        }

        if (dict == null) {
            PROPS.clear();

        } else {
            Enumeration keys = dict.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String) dict.get(key);
                PROPS.put(key, value);
                LOG.info("will use Config entry --> Key={}, value={}", key, value);
            }
        }

        update();
    }

    Dictionary<String, Object> getDict() {
        Hashtable<String, Object> dict = new Hashtable<String, Object>();
        dict.put(Constants.SERVICE_PID, "org.freeswitch.scxml");
        return dict;
    }

    @Override
    public void resultChanged(LookupEvent le) {
        update();
    }

    private void update() {

        Collection<? extends ConfigChangeListener> listeners = getListeners();

        for (ConfigChangeListener ls : listeners) {
            Set<String> keys = ls.getKeys();
            for (String key : keys) {
                if (PROPS.containsKey(key) && !PROPS.get(key).equals(ls.getValue(key))) {
                    
                    synchronized (ls) {
                        ls.setValue(key, PROPS.get(key));
                    }
                }
            }
        }
    }

    protected Collection<? extends ConfigChangeListener> getListeners() {
        return Lookup.getDefault().lookupAll(ConfigChangeListener.class);
    }
}
