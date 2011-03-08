package org.freeswitch.config.spi;

/**
 *
 * @author jocke
 */
public interface ConfigChangeListener {
    void configChange(String key, Object value);
}
