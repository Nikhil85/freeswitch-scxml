package com.albatross.visualivr.simulator;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.telmi.msc.fsadapter.ivr.ApplicationLauncher;
import java.util.Properties;

/**
 *
 * @author jocke
 */
public class IVRSimulatorModule extends AbstractModule {
    
    private final Properties properties;

    public IVRSimulatorModule(Properties properties) {
        this.properties = properties;
    }

    @Override
    protected void configure() {
         Names.bindProperties(binder(), properties);
         bind(ApplicationLauncher.class).to(IvrSimulatorLauncher.class);
    }
}
