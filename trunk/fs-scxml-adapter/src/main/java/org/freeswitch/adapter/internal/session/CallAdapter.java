package org.freeswitch.adapter.internal.session;

import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class CallAdapter implements Extension {
    
    private Session session;
    private static final Logger LOG = LoggerFactory.getLogger(CallAdapter.class);
    
    public CallAdapter(Session session) {
        this.session = session;
    }
    
    public String call(String dialString) {
      
        return "";
    }
}
