package org.freeswitch.adapter.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jocke
 */
public class Event {

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
    private final String eventName;
    private String body;
    private final Map<String, String> vars;

    public Event(String eventName, String body) {
        this.eventName = eventName;
        this.body = body;
        this.vars = new HashMap<String, String>();
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

        if (vars.containsKey(name)) {
            return vars.get(name);

        } else if (body == null) {
            return null;

        } else {
            Matcher matcher = getVariableMatcher(name);

            if (matcher.matches()) {
                try {
                    vars.put(matcher.group(1), URLDecoder.decode(matcher.group(3), "UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    throw new IllegalStateException("No decoder for UTF-8", ex);
                }
            }
            
            return vars.get(name);
        }

    }
    
    public static Event named(String name) {
        return new Event(name);
    }

    private Matcher getVariableMatcher(String var) {
        return Pattern.compile("^(" + var + ":)(\\s)(\\.*)$", Pattern.MULTILINE).matcher(body);
    }

    @Override
    public String toString() {
        return "Event{" + "eventName=" + eventName + '}';
    }
}
