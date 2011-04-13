package org.freeswitch.adapter.api;

import java.util.Map;

/**
 *
 * @author joe
 */
public interface SessionFactory {
   Session create(Map<String, Object> vars, CommandExecutor ce);
}
