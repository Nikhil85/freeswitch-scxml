package org.freeswitch.scxml.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.scxml.model.CustomAction;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public final class ScxmlClassLoader extends ClassLoader {

    private final Map<String, Class> managedClasses;
   
    public ScxmlClassLoader() {
        managedClasses = new HashMap<String, Class>();
        Collection<? extends CustomAction> actions = getActions();
        for (CustomAction action : actions) {
            managedClasses.put(action.getActionClass().getName(), action.getActionClass());
        }
    }

    private Collection<? extends CustomAction> getActions() {
        return Lookup.getDefault().lookupAll(CustomAction.class);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        if (managedClasses.containsKey(name)) {
            return managedClasses.get(name);
        
        } else {
            return super.loadClass(name);
        }
    }
}
