package org.freeswitch.lookup;

import java.lang.reflect.Field;
import org.openide.util.Lookup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {

        Thread.currentThread().setContextClassLoader(new BundleWrapperClassLoader(context.getBundle()));
        System.setProperty("org.openide.util.Lookup", OsgiLookup.class.getName());

        if (!(Lookup.getDefault() instanceof OsgiLookup)) {
            if (!(forceCreateLookup() instanceof OsgiLookup)) {
                throw new IllegalStateException("Failed to create a lookup");
            }
        }

        OsgiLookup ol = (OsgiLookup) Lookup.getDefault();
        context.addServiceListener(ol);
        Bundle[] bundles = context.getBundles();

        for (int i = 0; i < bundles.length; i++) {
            registerBundleServices(bundles[i].getRegisteredServices(), ol);
        }

    }

    private Lookup forceCreateLookup() throws SecurityException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field defaultLookup = Lookup.class.getDeclaredField("defaultLookup");
        defaultLookup.setAccessible(true);
        defaultLookup.set(null, null);
        return Lookup.getDefault();
    }

    private void registerBundleServices(ServiceReference[] registeredServices, OsgiLookup ol) {
        if (registeredServices != null) {
            for (int j = 0; j < registeredServices.length; j++) {
                ol.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, registeredServices[j]));
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.setProperty("org.openide.util.Lookup", "");
    }
}
