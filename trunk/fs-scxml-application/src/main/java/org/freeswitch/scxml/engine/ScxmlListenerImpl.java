package org.freeswitch.scxml.engine;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;

/**
 *
 * @author jocke
 */
class ScxmlListenerImpl implements SCXMLListener {

    private final Count count;

    
    ScxmlListenerImpl(Count counter) {
        this.count = counter;
    }
    
    @Override
    public void onEntry(TransitionTarget state) {
        //Do nothing.
    }
    
    @Override
    public void onExit(TransitionTarget state) {
        //Do nothing.
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition) {
        //Count up the count.event var. same state
        if (isSameState(from, to)) {
            incrementEventCount(transition.getEvent());
        } else {
            count.reset();
        }
    }

    private void incrementEventCount(String event) {
        if (event != null && !event.isEmpty()) {
            count.countUp(event);
        }
    }

    private boolean isSameState(TransitionTarget from, TransitionTarget target) {
        return from.getId().equals(target.getId());
    }
}
