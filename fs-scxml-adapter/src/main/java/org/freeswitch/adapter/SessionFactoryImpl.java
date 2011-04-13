package org.freeswitch.adapter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.freeswitch.adapter.api.SessionFactory;
import org.freeswitch.adapter.api.Session;
import java.util.Map;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.config.spi.ConfigChangeListener;

/**
 *
 * @author joe
 */
public class SessionFactoryImpl implements SessionFactory, ConfigChangeListener {

    private String path;
    private static final String RECORDING_PATH = "recording.path";
    private static final Set<String> KEYS = new HashSet<String>();

    static {
        KEYS.add(RECORDING_PATH);
    }

    @Override
    public Session create(Map<String, Object> map, CommandExecutor executor) {
        return new SessionImpl(map, new EventQueue(), executor);
    }

    @Override
    public Set<String> getKeys() {
        return KEYS;
    }

    @Override
    public String getValue(String key) {
        return path;
    }

    @Override
    public void setValue(String key, String value) {

        if (value == null) {
            throw new NullPointerException("Recording path null please provide a valid value");
        }

        if (new File(value).exists()) {
            System.setProperty(RECORDING_PATH, value);
        
        } else {
            throw new IllegalArgumentException("The recording path must point to an existing folder -->" + value);
        }

    }
}
