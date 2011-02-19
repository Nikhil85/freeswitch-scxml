package org.freeswitch.adapter.osgi;

import org.freeswitch.adapter.SessionFactoryImpl;
import org.freeswitch.adapter.api.SessionFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author jocke
 */
public class AdapterActivator implements BundleActivator {
    
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Starting bundle " + AdapterActivator.class.getName());
        context.registerService(SessionFactory.class.getName(), new SessionFactoryImpl(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
  

}
