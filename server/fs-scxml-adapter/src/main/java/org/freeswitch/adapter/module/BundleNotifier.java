package org.freeswitch.adapter.module;

import org.ops4j.peaberry.activation.Start;
import org.ops4j.peaberry.activation.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joe
 */
public class BundleNotifier {
    
    private static final Logger LOG = LoggerFactory.getLogger(BundleNotifier.class);
   
    @Start
    public void start() {
        LOG.info("The adapter was started");
    }
    
    @Stop
    public void stop() {
        LOG.info("The adapter was stopped");
    }
    
}
