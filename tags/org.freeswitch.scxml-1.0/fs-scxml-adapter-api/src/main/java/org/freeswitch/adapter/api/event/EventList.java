package org.freeswitch.adapter.api.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.constant.VarName;

/**
 *
 * @author jocke
 */
public final class EventList {

    private final List<Event> events = new ArrayList<>();
    private final List<DTMF> dtmfs = new ArrayList<>();

    EventList() {
    }

    void add(Event e) {

        if (e.getEventName().equals(Event.DTMF)) {
            dtmfs.add(DTMF.valueOfString(e.getVar(VarName.DTMF_DIGIT)));
        }
        events.add(e);
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

    public boolean contains(DTMF dtmf) {
        return dtmfs.contains(dtmf);
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

    public Event get(String event) {

        for (Event evt : events) {
            if (evt.getEventName().equalsIgnoreCase(event)) {
                return evt;
            }
        }

        return null;
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public DTMF getSingleResult() {
        return dtmfs.get(0);
    }

    List<DTMF> getDtmfs() {
        return dtmfs;
    }

    DTMF peakDtmf() {
        return dtmfs.get(dtmfs.size() - 1);
    }

    public int sizeOfDtmfs() {
        return dtmfs.size();
    }

    @Override
    public String toString() {
        return "EventList{" + "events=" + events + ", dtmfs=" + dtmfs + '}';
    }
}
