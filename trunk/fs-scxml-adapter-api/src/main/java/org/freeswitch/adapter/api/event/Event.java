package org.freeswitch.adapter.api.event;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class Event {

    private static final Logger LOG = LoggerFactory.getLogger(Event.class);
    private static final Pattern EVENT_PATTERN = Pattern.compile("(Event-Name:)(\\s)(\\w*)", Pattern.MULTILINE);
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
    public static final String CHANNEL_ANSWER = "CHANNEL_ANSWER";
    
    private final String eventName;
    private String body;
    final Map<String, String> vars;

    public Event(String eventName, String body) {
        this.eventName = eventName;
        this.body = body;
        this.vars = new HashMap<>();
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

    public String getBody() {
        return body;
    }

    public String getVar(String name) {

        if (vars.containsKey(name)) {
            return vars.get(name);

        } else if (body == null) {
            return null;
        }

        Matcher matcher = getVariableMatcher(name);

        if (matcher.find()) {
            return addVar(name, matcher.group(3));
        } else {
            return varNotFound(name);
        }
    }

    private String varNotFound(String name) {
        LOG.warn("Failed to get var {} ", name);
        LOG.trace(body);
        return null;
    }

    private String addVar(String name, String value) throws IllegalStateException {
        try {
            String var = URLDecoder.decode(value, "UTF-8");
            vars.put(name, var);
            return var;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("No decoder for UTF-8", ex);
        }
    }

    public static Event named(String name) {
        return new Event(name);
    }
    public static Event named(String name, Map<String, String> vars) {
        return new Event(name, vars);
    }

    public static Event fromData(String data) {
        return new Event(findEventName(data), data);
    }

    private static String findEventName(final String data) {
        Matcher matcher = EVENT_PATTERN.matcher(data);
        if (matcher.find()) {
            return matcher.group(3);
        }
        return null;
    }

    //TODO cache patterns 
    private Matcher getVariableMatcher(String var) {
        return Pattern.compile("^(" + var + ":)(\\s)(.*)$", Pattern.MULTILINE).matcher(body);
    }

    public Map<String, String> getBodyAsMap() {

        if (body == null) {
            return Collections.emptyMap();
        }

        Map<String, String> map = new HashMap<>();
        Scanner scanner = new Scanner(body.replaceAll(":", ""));

        while (scanner.hasNext()) {
            try {
                map.put(scanner.next(), URLDecoder.decode(scanner.next(), "UTF-8"));
            } catch (NoSuchElementException | UnsupportedEncodingException e) {
                LOG.error(e.getMessage());
            }
        }

        return map;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if (!Objects.equals(this.eventName, other.eventName)) {
            return false;
        }
        if (!Objects.equals(this.body, other.body)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.eventName);
        hash = 11 * hash + Objects.hashCode(this.body);
        return hash;
    }

    @Override
    public String toString() {
        return "Event{" + "eventName=" + eventName + '}';
    }
}
