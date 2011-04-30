package org.freeswitch.adapter;

/**
 *
 * @author jocke
 */
interface SessionState {

    boolean isAlive();

    boolean isNotAnswered();

    void setAlive(boolean b);

    void setNotAnswered(boolean notAnswered);
    
}
