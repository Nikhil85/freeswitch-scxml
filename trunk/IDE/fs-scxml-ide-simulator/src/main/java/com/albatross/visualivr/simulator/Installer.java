package com.albatross.visualivr.simulator;

import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.commons.scxml.model.CustomAction;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {
    
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    private IvrSimulatorLauncher launcher;
    
    @Override
    public void restored() {
        Properties properties = new Properties();
        properties.setProperty("scheduler.corePoolSize", "10");
        properties.setProperty("appPool.nThreads", "2");
        properties.setProperty("recording.path", "/tmp");
        properties.setProperty("scxml.cache", "false");     
        
        LOG.info("Simulator is up");
        Collection<? extends CustomAction> lookupAll = Lookup.getDefault().lookupAll(CustomAction.class);
    }

    @Override
    public boolean closing() {
        
        
        return true;
    }    
}
