package org.freeswitch.scxml.module;

import com.google.inject.Inject;
import org.freeswitch.scxml.ApplicationLauncher;
import org.freeswitch.scxml.ThreadPoolManager;
import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.activation.Start;
import org.ops4j.peaberry.activation.Stop;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joe
 */
public class BundleNotifier {
    
   
    @Inject
    private Export<ThreadPoolManager> poolManagerhandle;

    @Inject
    private Export<ApplicationLauncher> launcherHandle;

    
    @Start
    public void start() {
        System.out.println("The application module was started pool manager is available ");
    }

    @Stop
    public void stop() {
        System.out.println("The application module was stopped");
        poolManagerhandle.unput();
        launcherHandle.unput();
    }
    
}
