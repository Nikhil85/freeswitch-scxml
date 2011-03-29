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

    /**
     * Create a new instance new a new counter.
     *
     * @param counter Counts transitions between states.
     */
    ScxmlListenerImpl(Count counter) {
      this.count = counter;
    }

    /**
     * We do not need to do anything.
     *
     * @param state The state that is associated with this transition.
     */
    @Override
    public void onEntry(TransitionTarget state) {
    //Do nothing.
    }

    /**
     * We do not need to do anything.
     *
     * @param state The state that is associated with this transition.
     */
    @Override
    public void onExit(TransitionTarget state) {
     //Do nothing.
    }

    @Override
    public void onTransition(
            TransitionTarget from,
            TransitionTarget target,
            Transition transition) {

        String event = transition.getEvent();

        if (count.isSupported(event)) {

            //Count up the count.event var. same state
            if (from.getId().equals(target.getId())) {
               count.countUp(event);

             //Reset counter new state
            } else {
               count.reset();

            }
        }
    }
}


