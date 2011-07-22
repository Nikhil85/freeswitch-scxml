package org.freeswitch.adapter.api.session;

import org.freeswitch.adapter.api.event.EventQueue;
import java.util.Map;
import org.freeswitch.adapter.api.CommandExecutor;

/**
 *
 * @author joe
 */
public interface OutboundSessionFactory {
   Session create(Map<String, Object> vars, CommandExecutor ce, EventQueue queue);
}
