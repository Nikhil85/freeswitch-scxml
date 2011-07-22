package org.freeswitch.adapter.internal.session;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class CallAdapter implements Extension {
    
    static final String API_ORIGINATE = "api originate ";
    //&socket(127.0.0.1:9696 async full)
    
    private Session session;
    private static final Logger LOG = LoggerFactory.getLogger(CallAdapter.class);

    public CallAdapter(Session session) {
        this.session = session;
    }

    public EventList call(String dialString) {
        final String dial = API_ORIGINATE + dialString + " &park()\n\n";
        LOG.trace(dial);
        EventQueue eventQueue = session.execute(dial);
        try {
            return EventListBuilder.single(eventQueue.poll(5, TimeUnit.MINUTES));
        } catch (InterruptedException ex) {
            return EventListBuilder.single(Event.CHANNEL_HANGUP);
        }
    }
    
    public EventList bridge(String sessionId1, String sessionId2) {
        final String dial = "api uuid_bridge " + sessionId1 + " " + sessionId2 + "\n\n";
        LOG.trace(dial);
        session.execute(dial);
        try {
            return EventListBuilder.single(session.getEventQueue().poll(5, TimeUnit.MINUTES));
        } catch (InterruptedException ex) {
            return EventListBuilder.single(Event.CHANNEL_HANGUP);
        }
    }
}
