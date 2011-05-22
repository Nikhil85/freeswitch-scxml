package org.freeswitch.adapter.api;

/**
 *
 * @author jocke
 */
public interface EventQueueListener {
    
    public void onAdd(Event event);
   
}
