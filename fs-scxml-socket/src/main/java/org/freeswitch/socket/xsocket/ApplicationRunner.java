package org.freeswitch.socket.xsocket;

import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.ApplicationLauncher;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ApplicationRunner implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationRunner.class);

    private final Session fss;

    public ApplicationRunner(Session fss) {
        this.fss = fss;
    }

    @Override
    public void run() {
        try {
            Lookup.getDefault().lookup(ApplicationLauncher.class).launch(fss);
        } catch (Exception ex) {
            LOG.error("Application Runnder Thread died \n", ex);
        }
        fss.hangup();
    }
}
