package org.freeswitch.scxml.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.scxml.model.CustomAction;

/**
 *
 * @author jocke
 */
public final class ScxmlClassLoader extends ClassLoader {

    private Map<String, Class> managedClasses;
   

    public ScxmlClassLoader(ClassLoader parent, Collection<? extends CustomAction> actions) {
        super(parent);
        managedClasses = new HashMap<String, Class>();
        for (CustomAction action : actions) {
            managedClasses.put(action.getActionClass().getName(), action.getActionClass());
        }
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
