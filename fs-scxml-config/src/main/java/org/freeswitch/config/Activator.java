package org.freeswitch.config;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedService;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(ManagedService.class.getName(),  new OsgiConfigManager(), getDict());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // TODO add deactivation code here
    }

    private Dictionary<String, Object> getDict() {
        Hashtable<String, Object> dict = new Hashtable<>();
        dict.put(Constants.SERVICE_PID, "org.freeswitch.scxml");
        return dict;
    }
}
