package org.freeswitch.scxml.pool;

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
        bind(ThreadPoolManagerImpl.class);
    }

}
