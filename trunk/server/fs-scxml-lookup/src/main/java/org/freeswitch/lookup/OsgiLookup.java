package org.freeswitch.lookup;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 * 
 * NOTE Not thread safe yet
 * 
 * @author jocke
 */
public final class OsgiLookup extends AbstractLookup implements ServiceListener {

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
            System.out.println("Adding " + service.getClass());

        } else if (se.getType() == ServiceEvent.UNREGISTERING) {
            CONTENT.remove(service);
            System.out.println("Removing " + service.getClass());
        }
    }
}
