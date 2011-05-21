package org.freeswitch.config;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.freeswitch.config.spi.ConfigChangeListener;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
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
    private static final Map<String, String> PROPS = new ConcurrentHashMap<>();
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
                LOG.debug("will use Config entry --> Key={}, value={}", key, value);
            }
        }

        updateListeners();
    }

    @Override
    public void resultChanged(LookupEvent le) {
        updateListeners();
    }

    private void updateListeners() {
        Collection<? extends ConfigChangeListener> listeners = getListeners();
        for (ConfigChangeListener listener : listeners) {
            updateListener(listener);
        }
    }

    private void updateListener(ConfigChangeListener listener) {
        for (String key : listener.getKeys()) {
            if (PROPS.containsKey(key) && valueChanged(key, listener)) {                    
                synchronized (listener) {
                    listener.setValue(key, PROPS.get(key));
                }
            }
        }
    }

    private boolean valueChanged(String key, ConfigChangeListener ls) {
        return !PROPS.get(key).equals(ls.getValue(key));
    }

    protected Collection<? extends ConfigChangeListener> getListeners() {
        return Lookup.getDefault().lookupAll(ConfigChangeListener.class);
    }
}
