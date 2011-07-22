package org.freeswitch.socket.xsocket;

import java.util.Collection;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.application.api.ApplicationLauncher;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationRunner implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationRunner.class);
    private final Session fss;
    
    public ApplicationRunner(Session fss) {
        this.fss = fss;
    }
    
    @Override
    public void run() {
        try {
   
            for (ApplicationLauncher launcher : getLaunchers()) {
                if (launcher.isLaunchable(fss)) {
                    launcher.launch(fss);
                    fss.hangup();
                    return;
                }
            }
            
            LOG.warn("No launcher found for this session");
        } catch (Exception ex) {
            LOG.error("Application Thread died \n", ex);
        }
        
    }
    
    private Collection<? extends ApplicationLauncher> getLaunchers() {
        return Lookup.getDefault().lookupAll(ApplicationLauncher.class);
    }
}
