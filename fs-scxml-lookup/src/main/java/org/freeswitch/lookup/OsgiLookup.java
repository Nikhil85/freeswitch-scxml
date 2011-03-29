package org.freeswitch.lookup;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jocke
 */
public final class OsgiLookup extends AbstractLookup implements ServiceListener {
    
    private static final Logger LOG =  LoggerFactory.getLogger(OsgiLookup.class);
    private static final InstanceContent CONTENT = new InstanceContent();

    public OsgiLookup() {
        super(CONTENT);
    }
    
    @Override
    public void serviceChanged(ServiceEvent se) {
        
        Object service = se.getServiceReference().getBundle().getBundleContext().getService(se.getServiceReference());
        
        String name = service.getClass().getName();
        
        /** Should really be configurable */
        if(name.startsWith("org.apache.karaf") || name.startsWith("org.apache.aries")) {
            return;
        }
        
        if (se.getType() == ServiceEvent.REGISTERED) {
            CONTENT.add(service);
            LOG.debug("Adding {} " , service.getClass());

        } else if (se.getType() == ServiceEvent.UNREGISTERING) {
            CONTENT.remove(service);
            LOG.debug("Removing {}" , service.getClass());
        }
    }
}
