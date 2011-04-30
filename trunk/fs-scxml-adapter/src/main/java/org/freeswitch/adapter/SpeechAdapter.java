package org.freeswitch.adapter;

import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventListBuilder;
import org.freeswitch.adapter.api.EventListBuilder;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class SpeechAdapter {
    
    private static final Logger LOG = LoggerFactory.getLogger(SpeechAdapter.class); 
    private Session session;

    public SpeechAdapter(Session session) {
        this.session = session;
    }
   
    public EventList say(String moduleName, String sayType, String sayMethod, String value) throws HangupException {
        LOG.trace("Session#{}: say ...", session.getUuid());
        EventQueue eventQueue = session.execute(Command.say(moduleName, sayType, sayMethod, value));
        return new EventListBuilder(eventQueue).consume().build();
    }
    
    public EventList speak(String text) throws HangupException {
        LOG.debug("Session#{}: speak ...", session.getUuid());
        EventQueue eventQueue = session.execute(Command.speak(text, false));
        return new EventListBuilder(eventQueue).consume().build();
    }

}
