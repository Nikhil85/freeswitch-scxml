package org.freeswitch.adapter.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public final class EventList {

    private static final Logger LOG = LoggerFactory.getLogger(EventList.class);
    private final List<Event> events = new ArrayList<Event>();
    private final List<DTMF> dtmfs = new ArrayList<DTMF>();

    private EventList() {
    }

    private boolean add(Event e) {

        if (e.getEventName().endsWith(Event.DTMF)) {
            dtmfs.add(DTMF.valueOfString(e.getVar("DTMF-Digit"))); //TODO Will fail %23 == #
        }

        return events.add(e);
    }

    public int sizeOfDtmfs() {
        return dtmfs.size();
    }

    private void remove(String evtName) {

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
    
    public static EventList single(DTMF dtmf) {
        EventList el = new EventList();
        Map<String, String> vars = new HashMap<String, String>();
        vars.put("DTMF-Digit", dtmf.toString());
        Event event = new Event(Event.DTMF, vars);
        el.add(event);
        return el;
    }

    /**
     * A class for building IvrEvents.
     *
     * @author jocke.
     */
    public static final class EventListBuilder {

        private EventList eventList;
        private final BlockingQueue<Event> eventQueue;
        private int maxNumOfDTMFChars = Integer.MAX_VALUE;
        private Set<DTMF> termDtmfs = Collections.emptySet();

        public EventListBuilder(BlockingQueue<Event> queue) {
            this.eventList = new EventList();
            this.eventQueue = queue;
        }

        public EventListBuilder maxDigits(final int maxDigits) {
            this.maxNumOfDTMFChars = maxDigits > 0 ? maxDigits : Integer.MAX_VALUE;
            return this;
        }

        public EventListBuilder termDigits(Set<DTMF> terms) {
            this.termDtmfs = terms;
            return this;
        }

        public EventListBuilder startPolling() {
            do {

                Event event = null;

                try {
                    event = this.eventQueue.poll(5, TimeUnit.MINUTES);

                    if (event == null) {
                        eventList.add(new Event(Event.CHANNEL_HANGUP));

                    } else {
                        eventList.add(event);
                    }

                } catch (InterruptedException ex) {
                    LOG.error("Oops! event poll timout", ex);
                }

            } while (!buildIsFinal()
                    && !endsWithDtmf(termDtmfs)
                    && !(maxNumOfDTMFChars <= eventList.sizeOfDtmfs()));

            return this;
        }

        public EventListBuilder reset() {
            eventList.remove(Event.CHANNEL_EXECUTE_COMPLETE);
            return this;
        }

        /**
         * Test if this builder has collected a final event.
         *
         * @return true if it has false otherwise.
         */
        private boolean buildIsFinal() {
            return (contains(Event.CHANNEL_EXECUTE_COMPLETE)
                    || contains(Event.TIMEOUT)
                    || contains(Event.CHANNEL_HANGUP));
        }

        public boolean endsWithDtmf(Set<DTMF> terms) {

            if (eventList.dtmfs.isEmpty()) {
                return false;

            } else {
                return terms.contains(eventList.dtmfs.get(eventList.dtmfs.size() - 1));
            }

        }

        public boolean contains(String eventName) {
            return eventList.contains(eventName);
        }

        public EventList build() {
            return eventList;
        }
    }
}
