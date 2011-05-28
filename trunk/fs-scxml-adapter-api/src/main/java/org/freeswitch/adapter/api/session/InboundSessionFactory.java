package org.freeswitch.adapter.api.session;

import org.freeswitch.adapter.api.event.EventQueueListener;
import java.net.URL;
import java.util.concurrent.Future;

/**
 *
 * @author joe
 */
public interface InboundSessionFactory {

    Future<Session> create(String dialUrl, EventQueueListener listener);
}
