package com.albatross.visualivr.utils;

import java.util.HashSet;
import java.util.Set;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

/**
 * Class used to house anything one might want to store
 * in a central lookup which can affect anything within
 * the application. It can be thought of as a central context
 * where any application data may be stored and watched.
 * 
 * A singleton instance is created using @see getDefault().
 * This class is as thread safe as Lookup. Lookup appears to be safe.
 * 
 * @author Wade Chandler
 * 
 * @version 1.0
 */
public final class EventLookup extends ProxyLookup {

    private InstanceContent content;
    
    static final Set<Lookup.Result<?>> RESULTS = new HashSet<Result<?>>();
    
    private static final EventLookup def = new EventLookup();

    /**
     * Creates a GlobalLookup instances with a specific content set.
     * @param content the InstanceContent to use
     */
    private EventLookup(InstanceContent content) {
        super(new AbstractLookup(content));
        this.content = content;
    }

    /**
     * Creates a new GlobalLookup
     */
    private EventLookup() {
        this(new InstanceContent());
    }
    
    /**
     * Adds an instance to the Lookup. The instance will be added with the classes
     * in its hierarchy as keys which may be used to lookup the instance(s).
     * @param instance The instance to add
     */
    public void add(Object instance) {
        content.add(instance);
    }

    @Override
    public <T> Result<T> lookupResult(Class<T> clazz) {
        Result<T> lookupResult = super.lookupResult(clazz);
        return new LookupResultDecorator<T>(lookupResult);
    }

    /**
     * Removes the specific instance from the Lookup content.
     * @param instance The specific instance to remove.
     */
    public void remove(Object instance) {
        content.remove(instance);
    }

    /**
     * Returns the default GlobalLookup. This can be used as an application context for
     * the entire application. If needed GlobalLookup may be used directly through the
     * constructors to allow for more than one if needed. GlobalLookup is nothing more
     * than an InstanceContent instance wrapped in a Lookup with the add and remove methods
     * added to make updating the data easier.
     * @return The default GlobalLookup which is global in nature.
     */
    public static EventLookup getDefault() {
        return def;
    }
    
    
    
}