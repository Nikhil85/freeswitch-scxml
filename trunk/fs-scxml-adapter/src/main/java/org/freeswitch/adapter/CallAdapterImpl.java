package org.freeswitch.adapter;

import org.freeswitch.adapter.api.CallAdapter;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.Session;

/**
 *
 * @author jocke
 */
public class CallAdapterImpl implements CallAdapter, Extension {
    
    private Session session;

    public CallAdapterImpl(Session session) {
        this.session = session;
    }
    
    
    @Override
    public void originate(String dialString) {
       
    }

    @Override
    public void bridge(String uuid1, String uuid2) {
       
    }
    
}
