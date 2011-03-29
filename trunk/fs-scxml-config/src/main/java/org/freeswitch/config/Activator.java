package org.freeswitch.config;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        final OsgiConfigManager osgiConfigManager = new OsgiConfigManager();
        context.registerService(ManagedService.class.getName(), osgiConfigManager, osgiConfigManager.getDict());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // TODO add deactivation code here
    }
}
