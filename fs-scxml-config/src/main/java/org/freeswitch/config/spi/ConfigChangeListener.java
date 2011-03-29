package org.freeswitch.config.spi;

import java.util.Set;

/**
 *
 * @author jocke
 */
public interface ConfigChangeListener {
    Set<String> getKeys();
    String getValue(String key);
    void setValue(String key, String value);
}
