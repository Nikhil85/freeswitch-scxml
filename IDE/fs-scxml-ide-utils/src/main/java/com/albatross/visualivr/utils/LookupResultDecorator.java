package com.albatross.visualivr.utils;

import java.util.Collection;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;

/**
 *
 * @author joe
 */
public class LookupResultDecorator<T> extends Lookup.Result<T> {
    
    private final Lookup.Result<T> result;

    public LookupResultDecorator(Lookup.Result<T> result) {
        this.result = result;
    }
    
    @Override
    public void addLookupListener(LookupListener l) {
        result.addLookupListener(l);
        EventLookup.RESULTS.add(result); // Strong referens
    }

    @Override
    public void removeLookupListener(LookupListener listener) {
        result.removeLookupListener(listener);
        EventLookup.RESULTS.remove(result);
    }

    @Override
    public Collection<? extends T> allInstances() {
        return result.allInstances();
    }
}
