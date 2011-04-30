package org.freeswitch.adapter.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jocke
 */
public final class EventList implements Iterable<Event> {

    private final List<Event> events = new ArrayList<>();
    private final List<DTMF> dtmfs = new ArrayList<>();

    EventList() {
    }

    EventList add(Event e) {

        if (e.getEventName().endsWith(Event.DTMF)) {
            dtmfs.add(e.getDtmf());
        }
        events.add(e);
        return this;
    }

    public int sizeOfDtmfs() {
        return dtmfs.size();
    }

    void remove(String evtName) {
        Iterator<Event> iter = events.iterator();
        while (iter.hasNext()) {
            if (iter.next().getEventName().equals(evtName)) {
                iter.remove();
            }
        }
    }

    public boolean contains(String eventName) {
        for (Event event : events) {
            if (eventName.equals(event.getEventName())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAny(final Set<DTMF> subSet) {
        for (DTMF dtmf : subSet) {
            if (dtmfs.contains(dtmf)) {
                return true;
            }
        }
        return false;
    }

    public String dtmfsAsString() {
        StringBuilder sb = new StringBuilder();
        for (DTMF dtmf : dtmfs) {
            sb.append(dtmf.toString());
        }
        return sb.toString();
    }

    public String dtmfsAsString(Set<DTMF> filter) {

        StringBuilder sb = new StringBuilder();

        for (DTMF dtmf : dtmfs) {
            if (!filter.contains(dtmf)) {
                sb.append(dtmf.toString());
            }
        }
        return sb.toString();
    }

    public DTMF getSingleResult() {
        return dtmfs.get(0);
    }

    public boolean contains(DTMF dtmf) {
        return dtmfs.contains(dtmf);
    }

    public static EventList single(String event) {
        EventList el = new EventList();
        el.add(new Event(event));
        return el;
    }

    public static EventList single(Event event) {
        EventList el = new EventList();
        el.add(event);
        return el;
    }

    public static EventList single(DTMF dtmf) {
        EventList el = new EventList();
        createDtmfEvent(dtmf, el);
        return el;
    }

    public static EventList list(String dtmfChars) {
        return createList(dtmfChars);
    }

    public static EventList list(String dtmfChars, String event) {
        return createList(dtmfChars).add(new Event(event));
    }

    private static EventList createList(String dtmfChars) {
        Set<DTMF> dtmfs = DTMF.createCollectionFromString(dtmfChars);
        EventList el = new EventList();
        for (DTMF dtmf : dtmfs) {
            createDtmfEvent(dtmf, el);
        }
        return el;
    }

    private static void createDtmfEvent(DTMF dtmf, EventList el) {
        Map<String, String> vars = new HashMap<>();
        vars.put("DTMF-Digit", dtmf.toString());
        Event event = new Event(Event.DTMF, vars);
        el.add(event);
    }

    @Override
    public String toString() {
        return "EventList{" + "events=" + events + ", dtmfs=" + dtmfs + '}';
    }

    public Event get(String event) {

        for (Event evt : events) {
            if (evt.getEventName().equalsIgnoreCase(event)) {
                return evt;
            }
        }

        return null;
    }

    @Override
    public Iterator<Event> iterator() {
        return events.iterator();
    }

    List<DTMF> getDtmfs() {
        return dtmfs;
    }

    public DTMF peakDtmf() {
        return dtmfs.get(dtmfs.size() - 1);
    }
}
