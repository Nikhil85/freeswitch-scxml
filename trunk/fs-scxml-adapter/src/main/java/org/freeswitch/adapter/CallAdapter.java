package org.freeswitch.adapter;

import java.net.URL;
import org.freeswitch.adapter.api.EventQueueListener;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.InboundSessionFactory;
import org.freeswitch.adapter.api.Session;

/**
 *
 * @author jocke
 */
public class CallAdapter implements Extension {

    private Session session;

    public CallAdapter(Session session) {
        this.session = session;
    }

    public void call(String dialString, URL docUrl, EventQueueListener listener) {
        InboundSessionFactory factory = session.lookup(InboundSessionFactory.class);
        if (factory != null) {
            factory.create(dialString, docUrl, listener);
        }
    }
}
