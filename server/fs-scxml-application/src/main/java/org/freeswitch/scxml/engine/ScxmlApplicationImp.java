package org.freeswitch.scxml.engine;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public final class ScxmlApplicationImp implements ScxmlApplication {

    private static final String BASE = "base";
    private volatile boolean cache = false;

    private static final Logger LOG = LoggerFactory.getLogger(ScxmlApplicationImp.class);
    
    private static final ConcurrentHashMap<URL, StateMachine> CACHE =
            new ConcurrentHashMap<URL, StateMachine>();

    @Override
    public void createAndStartMachine(URL url, Map<String, Object> map)  {

        map.put(BASE, url);

        JexlContext context = new JexlContext(map);

        if (CACHE.containsKey(url)) {
            LOG.trace("use a cached machine");
            CACHE.get(url).newMachine(context);

        } else {
            LOG.trace("No machine in cache with url {} ",  url.toString());
            StateMachine machine = new StateMachine(url);
            
            if (cache) {
                CACHE.put(url, machine);
                LOG.debug("adding machine to cache");
            }
            machine.newMachine(context);
        }
    }



    @Override
    public boolean isCache() {
        return cache;
    }

    @Override
    public void setCache(boolean use) {
        LOG.debug("Cache policy changed to {} ", use);
        this.cache = use;
    }

    @Override
    public void clearCache() {
        CACHE.clear();
    }
}
