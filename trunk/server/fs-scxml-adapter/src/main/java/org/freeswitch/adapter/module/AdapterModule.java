package org.freeswitch.adapter.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.freeswitch.adapter.SessionFactory;
import org.freeswitch.adapter.SessionFactoryImpl;
import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.TypeLiterals.export;


/**
 *
 * @author joe
 */
public class AdapterModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(export(SessionFactory.class)).toProvider(service(SessionFactoryImpl.class).export());
        bind(export(BundleNotifier.class)).toProvider(service(BundleNotifier.class).export());
        bind(BundleNotifier.class).in(Singleton.class);
    }

}
