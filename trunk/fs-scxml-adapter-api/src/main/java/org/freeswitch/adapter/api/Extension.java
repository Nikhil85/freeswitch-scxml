package org.freeswitch.adapter.api;

/**
 * This is an marker interface for classes that
 * is extensions to the {@link org.freeswitch.adapter.api.Session} class. 
 * Implementations should have an constructor taken a session object
 * as there solely argument. To obtain an implementation use 
 * {@link org.freeswitch.adapter.api.Session#lookup(java.lang.Class)}, this 
 * will create an instance ones and then reuse it in all actions.
 * 
 * @author jocke
 */
public interface Extension {}
