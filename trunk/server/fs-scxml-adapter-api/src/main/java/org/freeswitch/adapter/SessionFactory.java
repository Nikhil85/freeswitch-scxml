package org.freeswitch.adapter;

import java.util.Map;

/**
 *
 * @author joe
 */
public interface SessionFactory {
   Session create(Map<String, Object> vars);
}
