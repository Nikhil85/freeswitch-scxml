package org.freeswitch.socket.xsocket.inbound;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Callable;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.adapter.api.EventQueueListener;
import org.freeswitch.adapter.api.InboundSessionFactory;
import org.freeswitch.adapter.api.OutboundSessionFactory;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.adapter.api.DefaultEventQueue;
import org.freeswitch.socket.xsocket.XsocketEventProducer;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public class DefaultInboundSessionFactory implements InboundSessionFactory {

    /**
     * TODO Maybe we should return a Future<Session>
     * 
     * @param dialUrl
     * @param docUrl
     * @param listener 
     */
    @Override
    public void create(String dialUrl, URL docUrl, EventQueueListener listener) {
        //TODO create 
    }

    private class AsyncSessionCreator implements Callable<Session> {

        private String dialString;
        private EventQueueListener listener;
        private URL docUrl;

        public AsyncSessionCreator(String dialString, EventQueueListener listener, URL docUrl) {
            this.dialString = dialString;
            this.listener = listener;
            this.docUrl = docUrl;
        }

        @Override
        public Session call() throws Exception {
            XsocketClient client = new XsocketClient(dialString);
            EventQueue eventQueue = new DefaultEventQueue();
            eventQueue.addListener(listener);
            XsocketEventProducer producer = new XsocketEventProducer(eventQueue, client);
            client.setProducer(producer);
            Event connect = client.connect();
            Session session = Lookup.getDefault().lookup(OutboundSessionFactory.class).create(new HashMap<String, Object>(connect.getBodyAsMap()), client, eventQueue);
            return session;
        }
    }
}
