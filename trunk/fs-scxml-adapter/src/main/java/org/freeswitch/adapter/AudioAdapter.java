package org.freeswitch.adapter;

import java.util.Set;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventListBuilder;
import org.freeswitch.adapter.api.EventListBuilder;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class AudioAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(AudioAdapter.class);
    public static final String BEEP_TONE = "tone_stream://%(500, 0, 800)";
    public static final String SILENCE_STREAM = "silence_stream://10";
    private Session session;

    public AudioAdapter(Session session) {
        this.session = session;
    }

    public EventList beep() throws HangupException {
        EventListBuilder evtBuilder = new EventListBuilder(session.getEventQueue());
        session.execute(Command.playback(BEEP_TONE, true));
        evtBuilder.consume().reset();
        session.execute(Command.playback(SILENCE_STREAM, true));
        evtBuilder.consume().reset();
        return evtBuilder.build();
    }

    public EventList streamFile(String file) throws HangupException {
        LOG.debug("Session#{}: StreamFile: {}", session.getUuid(), file);
        EventQueue eventQueue = session.execute(Command.playback(file, false));
        return new EventListBuilder(eventQueue).consume().build();
    }

    public EventList streamFile(String file, Set<DTMF> terms) throws HangupException {
        LOG.debug("Session#{}: StreamFile: {}, with termination options", session.getUuid(), file);
        EventQueue eventQueue = session.execute(Command.playback(file, false));
        EventListBuilder builder = new EventListBuilder(eventQueue).termDigits(terms).consume();
        if (builder.endsWithDtmf(terms)) {
            session.breakAction();
        }
        return builder.build();
    }

    public EventList recordFile(int timeLimitInMillis, boolean beep, Set<DTMF> terms, String format) throws HangupException {
        LOG.trace("Session#{}: Try to recordFile", session.getUuid());
        if (beep) {
            beep();
        }
        EventListBuilder builder = new EventListBuilder(session.getEventQueue()).termDigits(terms);
        session.execute(Command.record(getRecPath(format), timeLimitInMillis, null, null, false));
        builder.consume();

        if (builder.endsWithDtmf(terms)) {
            session.breakAction();
            return builder.consume().build();

        } else {
            return builder.build();
        }
    }

    private String getRecPath(String format) {
        String path = System.getProperty("recording.path") == null
                ? System.getProperty("java.io.tmpdir") : System.getProperty("recording.path");
        return createPath(path, "." + format);

    }

    private String createPath(String path, String suffix) {
        return path + "/" + session.getUuid() + suffix;
    }
}
