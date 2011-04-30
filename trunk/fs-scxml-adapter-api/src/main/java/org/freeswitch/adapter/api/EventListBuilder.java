/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freeswitch.adapter.api;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jocke
 */
public final class EventListBuilder {
    
    private EventList eventList;
    private final EventQueue eventQueue;
    private int maxNumOfDTMFChars = Integer.MAX_VALUE;
    private Set<DTMF> termDtmfs = Collections.emptySet();

    public EventListBuilder(EventQueue queue) {
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

    public EventListBuilder consume() throws HangupException {
        do {
            try {
                poll();
            } catch (InterruptedException ex) {
                throw new HangupException("Call ended");
            }
        } while (noFinalEvent());
        return this;
    }

    public EventListBuilder reset() {
        eventList.remove(Event.CHANNEL_EXECUTE_COMPLETE);
        return this;
    }

    public boolean endsWithDtmf(Set<DTMF> terms) {
        if (eventList.sizeOfDtmfs() == 0) {
            return false;
        } else {
            return terms.contains(eventList.peakDtmf());
        }
    }

    private boolean noFinalEvent() {
        return !buildIsFinal() && !endsWithDtmf(termDtmfs) && !(maxNumberOfDtmfs());
    }

    private void poll() throws InterruptedException, HangupException {
        Event event = this.eventQueue.poll(5, TimeUnit.MINUTES);
        if (event == null || Event.CHANNEL_HANGUP.equals(event.getEventName())) {
            throw new HangupException("Call ended");
        } else {
            eventList.add(event);
        }
    }

    private boolean maxNumberOfDtmfs() {
        return maxNumOfDTMFChars <= eventList.sizeOfDtmfs();
    }

    private boolean buildIsFinal() {
        return containsAnyEvent(Event.CHANNEL_EXECUTE_COMPLETE, Event.TIMEOUT, Event.CHANNEL_HANGUP);
    }

    public boolean containsAnyEvent(String... evts) {
        for (String name : evts) {
            if (eventList.contains(name)) {
                return true;
            }
        }
        return false;
    }

    public EventList build() {
        return eventList;
    }
    
}
