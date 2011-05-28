package org.freeswitch.adapter.api.event;

import org.freeswitch.adapter.api.event.Event;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jocke
 */
public interface EventQueue {

    boolean add(Event e);

    boolean clearDigits();

    boolean isEmpty();

    Event poll(int i, TimeUnit timeUnit) throws InterruptedException;

    Event poll();

    void addListener(EventQueueListener listener);
    
    void removeListener(EventQueueListener listener);
    
}
