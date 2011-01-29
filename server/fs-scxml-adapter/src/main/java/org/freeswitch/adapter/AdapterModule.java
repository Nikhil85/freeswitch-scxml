package org.freeswitch.adapter;

import com.google.inject.AbstractModule;
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
    }

}
