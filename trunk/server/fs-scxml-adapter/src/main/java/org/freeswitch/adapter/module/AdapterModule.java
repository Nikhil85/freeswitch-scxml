package org.freeswitch.adapter.module;
import org.ops4j.peaberry.activation.Configurables;
import com.google.inject.name.Names;
import org.freeswitch.adapter.SessionFactoryImpl;
import org.freeswitch.adapter.SessionFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import static org.ops4j.peaberry.Peaberry.service;
import static org.ops4j.peaberry.util.TypeLiterals.export;


/**
 *
 * @author joe
 */
public class AdapterModule extends AbstractModule {
    
    @Override
    protected void configure() {
        
        bind(String.class).annotatedWith(Names.named("recording.path")).toProvider(
        Configurables.configurable(String.class).from("org.freeswitch.scxml").named("recording.path"));
        
        bind(export(SessionFactory.class)).toProvider(service(SessionFactoryImpl.class).export());
        bind(export(BundleNotifier.class)).toProvider(service(BundleNotifier.class).export());
        bind(BundleNotifier.class).in(Singleton.class);
    }

}
