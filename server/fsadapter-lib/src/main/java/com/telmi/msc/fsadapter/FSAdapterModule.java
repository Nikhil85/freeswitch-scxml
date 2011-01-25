package com.telmi.msc.fsadapter;
import com.google.inject.AbstractModule;

/**
 * 
 * @author jocke
 */
public final class FSAdapterModule extends AbstractModule {

    /**
     * Configure the monitor module.
     */
    @Override
    protected void configure() {
        bind(FSAdapterMBean.class).to(FSAdapter.class);
    }

}
