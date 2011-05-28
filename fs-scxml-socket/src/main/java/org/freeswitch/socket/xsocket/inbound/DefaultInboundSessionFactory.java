package org.freeswitch.socket.xsocket.inbound;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.event.EventQueueListener;
import org.freeswitch.adapter.api.session.InboundSessionFactory;
import org.freeswitch.adapter.api.session.OutboundSessionFactory;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.adapter.api.event.DefaultEventQueue;
import org.freeswitch.socket.xsocket.EventManger;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public class DefaultInboundSessionFactory implements InboundSessionFactory {

    
    @Override
    public Future<Session> create(String dialUrl, EventQueueListener listener) {
        ExecutorService exe = Lookup.getDefault().lookup(ExecutorService.class);
        return exe.submit(new AsyncSessionCreator(dialUrl, listener));
    }

    private class AsyncSessionCreator implements Callable<Session> {

        private String dialString;
        private EventQueueListener listener;

        public AsyncSessionCreator(String dialString, EventQueueListener listener) {
            this.dialString = dialString;
            this.listener = listener;
        }

        @Override
        public Session call() throws Exception {
            Session session = null;
            EventQueue eventQueue = new DefaultEventQueue();
            eventQueue.addListener(listener);
            EventManger eventManger = new EventManger(eventQueue);
            XsocketClient client = new XsocketClient(dialString, eventManger);
            client.connect();
            Event connect = eventQueue.poll(1, TimeUnit.MINUTES);
            
            if (connect != null && Event.CHANNEL_ANSWER.equals(connect.getEventName())) {
                session = Lookup.getDefault().lookup(OutboundSessionFactory.class).create(new HashMap<String, Object>(connect.getBodyAsMap()), eventManger, eventQueue);
            }
            
            return session;
        }
    }
}
