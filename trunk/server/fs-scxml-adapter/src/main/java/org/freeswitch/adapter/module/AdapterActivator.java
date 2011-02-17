package org.freeswitch.adapter.module;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author jocke
 */
public class AdapterActivator implements BundleActivator , ManagedService  {
    
    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(ManagedService.class.getName(), this, getManagedServiceProperties());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    @Override
    public void updated(Dictionary dctnr) throws ConfigurationException {
    }
    
     protected Dictionary getManagedServiceProperties() {  
        Dictionary result = new Hashtable();  
        result.put(Constants.SERVICE_PID, "org.freeswitch.adapter");  
        return result;  
    }  

}