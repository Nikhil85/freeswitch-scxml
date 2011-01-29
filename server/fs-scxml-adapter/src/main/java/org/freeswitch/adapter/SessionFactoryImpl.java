package org.freeswitch.adapter;

import java.util.Map;

/**
 *
 * @author joe
 */
public class SessionFactoryImpl implements SessionFactory {
    
    @Override
    public Session create(Map<String, Object> map) {
       return new SessionImpl(map);
    }

}
