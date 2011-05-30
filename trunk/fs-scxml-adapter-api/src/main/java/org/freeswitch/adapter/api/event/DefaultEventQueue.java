package org.freeswitch.adapter.api.event;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jocke
 */
public class DefaultEventQueue implements EventQueue {
    
    private BlockingQueue<Event> eventQueue;
    private final String uid;
    private static final ConcurrentHashMap<String, DefaultEventQueue> QUEUES = new ConcurrentHashMap<>();
    
    public DefaultEventQueue(String uid) {
        eventQueue = new ArrayBlockingQueue<>(50);
        this.uid = uid;
    }
    
    public DefaultEventQueue(BlockingQueue<Event> eventQueue, String uid) {
        this.eventQueue = eventQueue;
        this.uid = uid;
    }
    
    @Override
    public Event poll(int i, TimeUnit timeUnit) throws InterruptedException {
        return eventQueue.poll(i, timeUnit);
    }
    
    @Override
    public boolean add(Event e) {
        if (Event.CHANNEL_HANGUP.equals(e.getEventName())) {
            dispose();
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
    public void fireEvent(Event event, String uid) {
        DefaultEventQueue queue = QUEUES.get(uid);
        if (queue != null) {
            queue.add(event);
        }
    }
    
    @Override
    public void dispose() {
        QUEUES.remove(this.uid);
    }
}
