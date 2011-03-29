package org.freeswitch.lookup;

import org.openide.util.Lookup;
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
public final class OsgiLookup extends Lookup implements ServiceListener {
    
    private static final Logger LOG =  LoggerFactory.getLogger(OsgiLookup.class);
    private final InstanceContent content;
    private final AbstractLookup lookup;
    
    public OsgiLookup() {
        this.content = new InstanceContent();
        this.lookup = new AbstractLookup(content);
    }

    OsgiLookup(InstanceContent content) {
        this.content = content;
        this.lookup = new AbstractLookup(content);
    }
     
    @Override
    public void serviceChanged(ServiceEvent se) {
        
        Object service = se.getServiceReference().getBundle().getBundleContext().getService(se.getServiceReference());
        
        String name = service.getClass().getName();
        
        /** Should really be configurable */
        if(name.startsWith("org.apache.karaf") || name.startsWith("org.apache.aries")) {
            return;
        }
        final int type = se.getType();
        
        if (type == ServiceEvent.REGISTERED) {
            content.add(service);
            LOG.debug("Adding {} " , service.getClass());

        } else if (type == ServiceEvent.UNREGISTERING) {
            content.remove(service);
            LOG.debug("Removing {}" , service.getClass());
        }
    }

    @Override
    public <T> T lookup(Class<T> clazz) {
        return lookup.lookup(clazz);
    }

    @Override
    public <T> Result<T> lookup(Template<T> template) {
        return lookup.lookup(template);
    }
}
