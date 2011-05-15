package org.freeswitch.adapter;

import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.Session;

/**
 *
 * @author jocke
 */
public class TestExtension implements Extension {
    private Session session;
    
    public TestExtension(Session session) {
        this.session = session;
    }
    
}
