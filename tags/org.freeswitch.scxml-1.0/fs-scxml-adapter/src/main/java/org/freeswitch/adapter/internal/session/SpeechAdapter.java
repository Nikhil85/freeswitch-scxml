package org.freeswitch.adapter.internal.session;

import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class SpeechAdapter implements Extension {
    
    private static final Logger LOG = LoggerFactory.getLogger(SpeechAdapter.class); 
    private Session session;
    private final Command cmd;

    public SpeechAdapter(Session session, Command cmd) {
        this.session = session;
        this.cmd = cmd;
    }
   
    public EventList say(String moduleName, String sayType, String sayMethod, String value) throws HangupException {
        LOG.trace("Session#{}: say ...", session.getUuid());
        EventQueue eventQueue = session.execute(cmd.say(moduleName, sayType, sayMethod, value));
        return new EventListBuilder(eventQueue).consume().build();
    }
    
    public EventList speak(String text) throws HangupException {
        LOG.debug("Session#{}: speak ...", session.getUuid());
        EventQueue eventQueue = session.execute(cmd.speak(text));
        return new EventListBuilder(eventQueue).consume().build();
    }

}
