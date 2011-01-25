package com.albatross.visualivr.simulator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.telmi.msc.fsadapter.pool.PoolModule;
import com.telmi.msc.scxml.engine.ScxmlModule;
import java.util.Properties;
import java.util.logging.Logger;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {
    
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    IvrSimulatorLauncher launcher;
    Injector injector;
    
    @Override
    public void restored() {
        Properties properties = new Properties();
        properties.setProperty("scheduler.corePoolSize", "10");
        properties.setProperty("appPool.nThreads", "2");
        properties.setProperty("recording.path", "/tmp");
        properties.setProperty("scxml.cache", "false");
    
        injector = Guice.createInjector(new PoolModule(), new ScxmlModule(), new IVRSimulatorModule(properties));
        
        launcher = injector.getInstance(IvrSimulatorLauncher.class);
        
        launcher.init();
        
        LOG.info("Simulator is up");
    
    }

    @Override
    public boolean closing() {
        
        if(launcher != null) {
            launcher.destroy();
        }
        
        return true;
    }    
}
