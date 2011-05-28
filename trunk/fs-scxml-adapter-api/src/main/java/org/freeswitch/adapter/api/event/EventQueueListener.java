package org.freeswitch.adapter.api.event;

import org.freeswitch.adapter.api.event.Event;

/**
 *
 * @author jocke
 */
public interface EventQueueListener {
    
    public void onAdd(Event event);
   
}
