package org.freeswitch.adapter.internal.session;

import java.util.Set;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class AudioAdapter implements Extension {

    private static final Logger LOG = LoggerFactory.getLogger(AudioAdapter.class);
    public static final String BEEP_TONE = "tone_stream://%(500, 0, 800)";
    public static final String SILENCE_STREAM = "silence_stream://10";
    private Session session;
    private Command cmd;

    public AudioAdapter(Session session, Command cmd) {
        this.session = session;
        this.cmd = cmd;
    }

    public EventList beep() throws HangupException {
        EventListBuilder evtBuilder = new EventListBuilder(session.getEventQueue());
        session.execute(cmd.playback(BEEP_TONE));
        evtBuilder.consume().reset();
        session.execute(cmd.playback(SILENCE_STREAM));
        evtBuilder.consume().reset();
        return evtBuilder.build();
    }

    public EventList streamFile(String file) throws HangupException {
        LOG.debug("Session#{}: StreamFile: {}", session.getUuid(), file);
        EventQueue eventQueue = session.execute(cmd.playback(file));
        return new EventListBuilder(eventQueue).consume().build();
    }

    public EventList streamFile(String file, Set<DTMF> terms) throws HangupException {
        LOG.debug("Session#{}: StreamFile: {}, with termination options", session.getUuid(), file);
        EventQueue eventQueue = session.execute(cmd.playback(file));
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
        session.execute(cmd.record(getRecPath(format), timeLimitInMillis, null, null));
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
