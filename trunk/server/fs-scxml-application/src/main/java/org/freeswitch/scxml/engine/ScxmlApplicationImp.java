package org.freeswitch.scxml.engine;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.apache.commons.scxml.model.CustomAction;
import org.freeswitch.scxml.sender.SenderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
@Singleton
public final class ScxmlApplicationImp implements ScxmlApplication {

    private static final String BASE = "base";
    private volatile boolean cache = false;
    private  List<CustomAction> actions;

    private static final Logger LOG =
            LoggerFactory.getLogger(ScxmlApplicationImp.class);
    
    //private final SenderFactory factory;

    private static final ConcurrentHashMap<URL, StateMachine> CACHE =
            new ConcurrentHashMap<URL, StateMachine>();

    /**
     * Creates a new instance.
     *
     * @param useCache To use cache or not.
     *
     * @param senderFactory of senders.
     *
     * @param customActions All the actions handled by this application.
     *
     */
    @Inject
    public ScxmlApplicationImp(Set<CustomAction> actions, @Named("scxml.cache") String cache) {
        this.actions = new ArrayList<CustomAction>(actions.size());
        this.cache = Boolean.valueOf(cache);
        this.actions.addAll(actions);

    }

    @Override
    public void createAndStartMachine(URL url, Map<String, Object> map)  {

        map.put(BASE, url);

        JexlContext context = new JexlContext(map);

        if (CACHE.containsKey(url)) {
            LOG.trace("use a cached machine");
            CACHE.get(url).newMachine(context);

        } else {
            LOG.trace("No machine in cache with url {} ",  url.toString());

            StateMachine machine = new StateMachine(url, actions, /*factory*/ null);
            if (cache) {
                CACHE.put(url, machine);
                LOG.debug("adding machine to cache");
            }
            machine.newMachine(context);
        }
    }

    /**
     *
     * Will return a string with all the custom actions bound
     * to this class and the size of the cache.
     *
     * @return A string representation of this class.
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("CustomActions \n");

        for (CustomAction customAction : actions) {
            builder.append(customAction.getLocalName()).append("\n");
        }
        builder.append("Cache size = ").append(CACHE.size()).append("\n");
        return builder.toString();
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
