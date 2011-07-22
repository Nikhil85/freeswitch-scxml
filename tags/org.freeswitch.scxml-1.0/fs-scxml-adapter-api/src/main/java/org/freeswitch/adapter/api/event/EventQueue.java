package org.freeswitch.adapter.api.event;

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
    
    public void dispose();
 
    public void fireEvent(Event event, String uid);
    
}
