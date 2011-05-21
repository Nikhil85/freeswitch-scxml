package org.freeswitch.adapter.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.adapter.api.EventQueueListener;

/**
 *
 * @author jocke
 */
public class DefaultEventQueue implements EventQueue {

    private BlockingQueue<Event> eventQueue;
    private EventQueueListener listener;
    private List<EventQueueListener> listeners;

    public DefaultEventQueue() {
        eventQueue = new ArrayBlockingQueue<>(50);
    }

    public DefaultEventQueue(BlockingQueue<Event> eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public Event poll(int i, TimeUnit timeUnit) throws InterruptedException {
        return eventQueue.poll(i, timeUnit);
    }

    @Override
    public boolean add(Event e) {
        if(listener != null) {
            listener.onAdd(e);
        }
        return eventQueue.add(e);
    }

    @Override
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

    @Override
    public boolean isEmpty() {
        return eventQueue.isEmpty();
    }

    @Override
    public Event poll() {
        return eventQueue.poll();
    }

    @Override
    public void addListener(EventQueueListener listener) {
        if(listeners == null) {
            listeners = new ArrayList<>(5);
        }
        this.listener = listener;
    }
    
    @Override
    public void removeListener(EventQueueListener listener) {
        if(listeners != null) {
            listeners.remove(listener);
        }
    }
}
