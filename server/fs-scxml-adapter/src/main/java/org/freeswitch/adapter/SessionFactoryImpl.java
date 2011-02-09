package org.freeswitch.adapter;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Map;

/**
 *
 * @author joe
 */
public class SessionFactoryImpl implements SessionFactory {

    private String recordingPath;
    
    @Inject
    public SessionFactoryImpl(@Named("recording.path") String recordingPath) {
        this.recordingPath = recordingPath;
    }

    @Override
    public Session create(Map<String, Object> map) {
        map.put(SessionImpl.REC_PATH, recordingPath);
        return new SessionImpl(map);
    }
}
