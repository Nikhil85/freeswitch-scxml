package org.freeswitch.adapter.internal.session;

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
