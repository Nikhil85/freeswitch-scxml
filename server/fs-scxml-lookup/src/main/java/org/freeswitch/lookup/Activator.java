package org.freeswitch.lookup;

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
        Lookup lookup = Lookup.getDefault();

        if (lookup instanceof OsgiLookup) {
            OsgiLookup ol = (OsgiLookup) lookup;
            context.addServiceListener(ol);
            System.out.println("System will use lookup " + lookup.getClass().getName());
            Bundle[] bundles = context.getBundles();
            
            for (int i = 0; i < bundles.length; i++) {
                registerBundelServices(bundles[i], ol);
            }

        } else {
            System.err.println("Lookup creation failed");
            return;
        }


    }

    private void registerBundelServices(Bundle bundle, OsgiLookup ol) {
        
        ServiceReference[] registeredServices = bundle.getRegisteredServices();

        if (registeredServices != null) {
            for (int j = 0; j < registeredServices.length; j++) {
                ServiceReference serviceReference = registeredServices[j];
                ServiceEvent event = new ServiceEvent(ServiceEvent.REGISTERED, serviceReference);
                ol.serviceChanged(event);
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.setProperty("org.openide.util.Lookup", "");
    }
}