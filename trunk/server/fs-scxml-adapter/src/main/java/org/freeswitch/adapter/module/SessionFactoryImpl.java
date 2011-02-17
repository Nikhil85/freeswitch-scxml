package org.freeswitch.adapter.module;

import java.util.Map;
import org.freeswitch.adapter.Session;
import org.freeswitch.adapter.SessionFactory;
import org.freeswitch.adapter.SessionImpl;

/**
 *
 * @author joe
 */
public class SessionFactoryImpl implements SessionFactory {

    //private final String path;
    
    public SessionFactoryImpl() {
        //this.path = path;
    }

    @Override
    public Session create(Map<String, Object> map) {
        map.put(SessionImpl.REC_PATH, /* path */"");
        return new SessionImpl(map);
    }
}
