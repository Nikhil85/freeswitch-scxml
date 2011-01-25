package com.telmi.msc.fsadapter.ivr;

import com.telmi.msc.scxml.engine.*;
import com.google.inject.AbstractModule;

/**
 *
 * @author jocke
 */
public final class ApplicationModule extends AbstractModule {

    /**
     * Configure the application.
     */
    @Override
    protected void configure() {
        bind(ApplicationLauncher.class).to(ScxmlApplicationLauncher.class);
    }

}
