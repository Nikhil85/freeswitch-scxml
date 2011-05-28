package org.freeswitch.socket.xsocket;

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
        ApplicationLauncher launcher = Lookup.getDefault().lookup(ApplicationLauncher.class);
        
        if(launcher == null) {
            LOG.warn("No application launcher found. Unable to launch!!");
            return;
        }
        
        try {
            launcher.launch(fss);
        } catch (Exception ex) {
            LOG.error("Application Thread died \n", ex);
        }
        fss.hangup();
    }
}
