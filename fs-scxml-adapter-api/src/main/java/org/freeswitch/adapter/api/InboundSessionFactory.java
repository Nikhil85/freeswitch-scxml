package org.freeswitch.adapter.api;

import java.net.URL;

/**
 *
 * @author joe
 */
public interface InboundSessionFactory {
   void create(String dialUrl, URL docUrl, EventQueueListener listener);
}
