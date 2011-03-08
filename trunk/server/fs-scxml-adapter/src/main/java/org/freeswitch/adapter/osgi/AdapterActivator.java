package org.freeswitch.adapter.osgi;

import org.freeswitch.adapter.SessionFactoryImpl;
import org.freeswitch.adapter.api.SessionFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class AdapterActivator implements BundleActivator {
    
    private static final Logger LOG = LoggerFactory.getLogger(AdapterActivator.class);
    
    @Override
    public void start(BundleContext context) throws Exception {
        LOG.info("Starting bundle " + AdapterActivator.class.getName());
        context.registerService(SessionFactory.class.getName(), new SessionFactoryImpl(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
  

}
