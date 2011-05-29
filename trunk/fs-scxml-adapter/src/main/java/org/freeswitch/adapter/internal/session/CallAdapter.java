package org.freeswitch.adapter.internal.session;

import java.util.concurrent.Future;
import org.freeswitch.adapter.api.event.EventQueueListener;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.session.InboundSessionFactory;
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
    
    public Future<Session> call(String dialString, EventQueueListener listener) {
        InboundSessionFactory factory = session.lookup(InboundSessionFactory.class);
        if (factory != null) {
            return factory.create(dialString, listener);
        } else {
            LOG.warn("No InboundSessionFactory found unable to create new session!");
            return null;
        }
    }
}
