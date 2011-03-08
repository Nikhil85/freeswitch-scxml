package org.freeswitch.config.spi;


/**
 *
 * @author jocke
 */
public interface ConfigManager {

    String get(String key);

    boolean getBoolean(String key);

    int getInt(String key);

    void addConfigChangeListener(String key, ConfigChangeListener listener);
    
}
