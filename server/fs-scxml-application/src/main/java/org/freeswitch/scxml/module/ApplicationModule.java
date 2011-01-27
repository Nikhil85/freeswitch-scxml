package org.freeswitch.scxml.module;

import com.google.inject.AbstractModule;
import org.freeswitch.scxml.application.ApplicationLauncher;
import org.freeswitch.scxml.engine.ScxmlApplicationLauncher;

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
