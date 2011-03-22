package com.albatross.visualivr.simulator.api;

import java.util.Queue;
import org.freeswitch.adapter.api.Event;

/**
 *
 * @author jocke
 */
public interface CommandSimulator {

/**
 * Simulate an execution of an application action to FreeSwitch
 * 
 * @param args
 */
void execute(String[] args);

/**
 * Stop the current action from executing
 * 
 * @param args
 */
 void breakAction();
 
 /**
  * An implementation should return the name of the application that is supported.
  * 
  * @return application name as String
  */
 String supports();
 
 /**
  * Create a new command simulator for the given queue.
  * 
  * @param queue to put events on
  * 
  * @return a new instance ready to be executed
  * 
  */
 CommandSimulator create(Queue<Event> queue);
 
}
