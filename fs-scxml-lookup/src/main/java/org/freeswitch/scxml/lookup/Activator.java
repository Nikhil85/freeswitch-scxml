package org.freeswitch.scxml.lookup;

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
        setClassLoader(context);
        OsgiLookup ol = getLookup();
        context.addServiceListener(ol);
        registerServices(context.getBundles(), ol);
    }

    protected void setClassLoader(BundleContext context) {
        Thread.currentThread().setContextClassLoader(new BundleWrapperClassLoader(context.getBundle()));
    }

    private OsgiLookup getLookup() throws Exception {
        System.setProperty("org.openide.util.Lookup", OsgiLookup.class.getName());
        if (!(Lookup.getDefault() instanceof OsgiLookup)) {
            if (!(forceCreateLookup() instanceof OsgiLookup)) {
                throw new IllegalStateException("Failed to create a lookup");
            }
        }
        return (OsgiLookup) Lookup.getDefault();
    }

    private Lookup forceCreateLookup() throws Exception {
        Field defaultLookup = Lookup.class.getDeclaredField("defaultLookup");
        defaultLookup.setAccessible(true);
        defaultLookup.set(null, null);
        return Lookup.getDefault();
    }

    private void registerServices(Bundle[] bundles, OsgiLookup ol) {
        for (int i = 0; i < bundles.length; i++) {
            registerBundleServices(bundles[i].getRegisteredServices(), ol);
        }
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
