package org.freeswitch.adapter.internal.session;

import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventQueue;
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
        EventQueue eventQueue = session.getEventQueue();
        final String dial = "api originate [tts_engine=flite,tts_voice=kal,scxml=file:/home/jocke/NetBeansProjects/fs-scxml/fs-scxml-integration-test/src/test/resources/org/freeswitch/scxml/test/countTest.xml]" + dialString + " &socket(127.0.0.1:9696 async full)\n\n";
        LOG.info(dial);
        session.execute(dial);
        Event poll;
        try {
            poll = eventQueue.poll(1, TimeUnit.MINUTES);
            LOG.info("#######################" + poll + "########################");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
       
        return null;
       
    }
}
