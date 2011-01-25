package com.telmi.msc.fsadapter.pool;

import com.google.inject.AbstractModule;

/**
 *
 * @author jocke
 */
public final class PoolModule extends AbstractModule {

    /**
     * Configure the pool module.
     */
    @Override
    protected void configure() {
        bind(ThreadPoolManager.class);
    }

}
