package org.freeswitch.adapter.api;

import java.util.Collections;
import java.util.Map;

/**
 *
 * @author jocke
 */
public class Event {

    private String eventName;
    private String body;
    private final Map<String, String> vars;
    
    public Event(String eventName, String body) {
        this.eventName = eventName;
        this.body = body;
        this.vars  = Collections.EMPTY_MAP;
    }

    public Event(String eventName, Map<String, String> vars) {
        this.eventName = eventName;
        this.vars = vars;
    }

    public Event(String eventName) {
        this.eventName = eventName;
        this.vars = Collections.emptyMap();
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public String getVar(String name) {
        return vars.get(name);
    }
    
    public static final String CHANNEL_EXECUTE_COMPLETE = "CHANNEL_EXECUTE_COMPLETE";
    public static final String CHANNEL_CREATE = "CHANNEL_CREATE";
    public static final String CHANNEL_EXECUTE = "CHANNEL_EXECUTE";
    public static final String CHANNEL_DESTROY = "CHANNEL_DESTROY";
    public static final String CHANNEL_HANGUP = "CHANNEL_HANGUP";
    public static final String CHANNEL_PARK = "CHANNEL_PARK";
    public static final String TIMEOUT = "TIMEOUT";
    public static final String DTMF = "DTMF";
    public static final String ERROR = "ERROR";
    public static final String CHANNEL_DATA = "CHANNEL_DATA";
    
}
