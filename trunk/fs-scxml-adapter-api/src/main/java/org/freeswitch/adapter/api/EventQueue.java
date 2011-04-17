/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freeswitch.adapter.api;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jocke
 */
public class EventQueue {

    private BlockingQueue<Event> eventQueue;

    public EventQueue() {
        eventQueue = new ArrayBlockingQueue<Event>(50);
    }

    public EventQueue(BlockingQueue<Event> eventQueue) {
        this.eventQueue = eventQueue;
    }

    Event poll(int i, TimeUnit timeUnit) throws InterruptedException {
        return eventQueue.poll(i, timeUnit);
    }

    public boolean add(Event e) {
        return eventQueue.add(e);
    }

    public boolean clearDigits() {
        boolean removed = false;
        Iterator<Event> it = eventQueue.iterator();
        while (it.hasNext()) {
            if (it.next().getEventName().equals(Event.DTMF)) {
                removed = true;
                it.remove();
            }
        }
        return removed;
    }

    public boolean isEmpty() {
        return eventQueue.isEmpty();
    }

    public Event poll() {
        return eventQueue.poll();
    }
}
