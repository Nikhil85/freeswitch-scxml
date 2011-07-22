package org.freeswitch.scxml.engine;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.freeswitch.config.spi.ConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public final class ScxmlApplicationImp implements ScxmlApplication, ConfigChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(ScxmlApplicationImp.class);
    private static final ConcurrentHashMap<URL, StateMachine> CACHE = new ConcurrentHashMap<URL, StateMachine>();
    private static final Set<String> KEYS = new HashSet<String>(1);
    private static final String SCXML_USE_CACHE = "scxml.use.cache";
    private static final String BASE = "base";
    
    private volatile boolean cache = false;

    static {
        KEYS.add(SCXML_USE_CACHE);
    }

    @Override
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    @Override
    public String getValue(String key) {
        return Boolean.toString(cache);
    }

    @Override
    public void setValue(String key, String value) {
        this.cache = Boolean.valueOf(value);

        if (!cache) {
            CACHE.clear();
        }
    }

    @Override
    public void createAndStartMachine(URL url, Map<String, Object> map) {

        map.put(BASE, url);

        JexlContext context = new JexlContext(map);

        if (cache && CACHE.containsKey(url)) {
            LOG.trace("use a cached machine");
            CACHE.get(url).newMachine(context);

        } else {
            LOG.trace("No machine in cache with url {} ", url.toString());
            StateMachine machine = new StateMachine(url);

            if (cache) {
                CACHE.put(url, machine);
                LOG.debug("adding machine to cache");
            }
            machine.newMachine(context);
        }
    }
}
