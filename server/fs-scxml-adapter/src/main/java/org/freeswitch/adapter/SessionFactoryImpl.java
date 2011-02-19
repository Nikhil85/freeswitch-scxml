package org.freeswitch.adapter;

import org.freeswitch.adapter.api.SessionFactory;
import org.freeswitch.adapter.api.Session;
import java.util.Map;

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
