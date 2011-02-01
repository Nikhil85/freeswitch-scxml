package org.freeswitch.adapter.module;

import org.ops4j.peaberry.activation.Start;
import org.ops4j.peaberry.activation.Stop;

/**
 *
 * @author joe
 */
public class BundleNotifier {
    
   
    @Start
    public void start() {
        System.out.println("The adapter was started");
    }
    
    @Stop
    public void stop() {
        System.out.println("The adapter was stopped");
    }
    
}
