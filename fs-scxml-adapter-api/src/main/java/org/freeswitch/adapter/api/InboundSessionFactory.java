package org.freeswitch.adapter.api;

import java.util.Map;

/**
 *
 * @author joe
 */
public interface InboundSessionFactory {
   Session create(String dialUrl);
}
