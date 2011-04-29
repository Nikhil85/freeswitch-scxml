package org.freeswitch.adapter;

import java.util.Collections;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.Session;
import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventList.EventListBuilder;
import org.freeswitch.adapter.api.EventQueue;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 *
 */
public final class SessionImpl implements Session, Callable<Boolean> { //NOPMD

    public static final String BEEP_TONE = "tone_stream://%(500, 0, 800)";
    public static final String SILENCE_STREAM = "silence_stream://10";
    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);
    private static final String EVENT_MAP_EQUALS = "sip_bye_h_X-EventMap=";
    private final Map<String, Object> sessionData;
    private final EventQueue eventQueue;
    private final ScheduledExecutorService scheduler;
    private final String sessionid;
    private boolean notAnswered = true;
    private volatile boolean alive = true;
    private final CommandExecutor executor;

    public SessionImpl(Map<String, Object> map, EventQueue eq, CommandExecutor executor) {
        this.sessionData = map;
        this.eventQueue = eq;
        this.scheduler = Lookup.getDefault().lookup(ScheduledExecutorService.class);
        this.executor = executor;
        this.sessionid = getUuid();
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public Map<String, Object> getVars() {
        return sessionData;
    }

    @Override
    public String getUuid() {
        return (String) sessionData.get("Channel-Unique-ID");
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    @Override
    public EventList answer() {
        LOG.trace("Session#{}: answer ...", sessionid);

        if (notAnswered) {
            execute(Command.answer());
            this.notAnswered = false;
            return new EventListBuilder(eventQueue).consume().build();
        } else {
            return EventList.single(Event.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public EventList say(String moduleName, String sayType, String sayMethod, String value) {
        LOG.trace("Session#{}: say ...", sessionid);
        execute(Command.say(moduleName, sayType, sayMethod, value));
        return new EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public EventList beep() {
        EventList.EventListBuilder evtBuilder = new EventList.EventListBuilder(eventQueue);
        execute(Command.playback(BEEP_TONE, true));
        evtBuilder.consume().reset();
        execute(Command.playback(SILENCE_STREAM, true));
        evtBuilder.consume().reset();
        return evtBuilder.build();
    }

    @Override
    public EventList recordFile(int timeLimitInMillis, boolean beep, Set<DTMF> terms, String format) {
        LOG.trace("Session#{}: Try to recordFile", sessionid);
        EventListBuilder builder = new EventListBuilder(eventQueue).termDigits(terms);
        if (beep) {
            beep();
        }
        execute(Command.record(getRecPath(format), timeLimitInMillis, null, null, false));
        builder.consume();

        if (builder.endsWithDtmf(terms)) {
            builder.reset();
            breakAction(builder);
        }

        return builder.build();
    }

    @Override
    public EventList speak(String text) {
        LOG.debug("Session#{}: speak ...", sessionid);
        execute(Command.speak(text, false));
        return new EventList.EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public EventList getDigits(int maxdigits, Set<DTMF> terms, long timeout) {
        LOG.debug("Session#{}: getDigits ...", sessionid);
        ScheduledFuture<Boolean> future = scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);
        EventList evt = new EventList.EventListBuilder(eventQueue).maxDigits(maxdigits).termDigits(terms).consume().build();

        if (!future.isDone()) {
            future.cancel(true);
        }

        return evt;
    }

    @Override
    public EventList read(int maxDigits, String prompt, long timeout, Set<DTMF> terms) {
        LOG.info("Session#{}: read ...  timeout -->{}", sessionid, timeout);

        execute(Command.playback(prompt, false));
        EventListBuilder builder = new EventListBuilder(eventQueue).maxDigits(maxDigits).termDigits(terms).consume();

        if (builder.containsAnyEvent(Event.CHANNEL_EXECUTE_COMPLETE)) {
            LOG.trace("Session#{} the prompt playing was stopped, start timer", sessionid);
            ScheduledFuture<Boolean> future = scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);
            builder.reset().consume();

            if (!future.isDone()) {
                future.cancel(false);
            }

        } else if (!builder.containsAnyEvent(Event.CHANNEL_HANGUP)) {
            LOG.trace("Session#{} the prompt is still playing, cancel it", sessionid);
            breakAction(builder.reset());
        }

        return builder.build();
    }

    @Override
    public EventList streamFile(String file) {
        LOG.debug("Session#{}: StreamFile: {}", sessionid, file);
        execute(Command.playback(file, false));
        return new EventList.EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public EventList streamFile(String file, Set<DTMF> terms) {
        LOG.debug("Session#{}: StreamFile: {}, with termination options", sessionid, file);
        execute(Command.playback(file, false));
        EventList.EventListBuilder builder = new EventList.EventListBuilder(eventQueue).termDigits(terms).consume();

        if (builder.endsWithDtmf(terms)) {
            breakAction(builder.reset());
        }

        return builder.build();
    }

    @Override
    public void sleep(long milliseconds) {
        LOG.debug("Session#{}: Channel Silence for '{}' millis.", sessionid, milliseconds);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            LOG.warn("Session#{}: Thread interupted while sleeping, {}", sessionid, ex.getMessage());
        }
    }

    @Override
    public EventList hangup(Map<String, Object> nameList) {
        LOG.debug("Session#{}: hang up ...", sessionid);
        execute(Command.set(EVENT_MAP_EQUALS + nameList.toString()));
        EventListBuilder builder = new EventListBuilder(eventQueue).consume();
        builder.reset();
        execute(Command.hangup(null));
        return builder.consume().build();
    }

    @Override
    public EventList hangup() {
        LOG.trace("Session#{}: hangup ...", sessionid);

        if (alive) {
            alive = false;
            execute(Command.hangup(null));
            return new EventListBuilder(eventQueue).consume().build();

        } else {
            return EventList.single(Event.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public EventList deflect(String target) {
        LOG.trace("Session#{}: deflect ...", sessionid);
        execute(Command.refer(target));
        return new EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public boolean clearDigits() {
        return eventQueue.clearDigits();
    }

    private EventListBuilder breakAction(EventListBuilder builder) {
        LOG.debug("Session#{}: breakAction ...", sessionid);
        execute(Command.breakcommand());
        sleep(1000L);
        return builder.consume().reset().consume();
    }

    @Override
    public Boolean call() {
        LOG.debug("Session#{}: Call Action timed out ???", sessionid);        
        return  eventQueue.add(Event.named(Event.TIMEOUT));
    }

    public String getRecPath(String format) {

        String recPath = System.getProperty("recording.path");

        if (recPath == null) {
            recPath = System.getProperty("java.io.tmpdir");
        }

        return "mp3".equalsIgnoreCase(format) ? recPath + "/" + getUuid() + ".mp3" : recPath + "/" + getUuid() + ".wav";

    }

    @Override
    public EventQueue execute(String data) {

        if (executor.isReady()) {

            try {
                executor.execute(data);
            } catch (IOException ex) {
                LOG.error("writeData error, trigger CHANNEL_HANGUP event. {}", ex.getMessage());
                eventQueue.add(new Event(Event.CHANNEL_HANGUP));
            }

        } else {
            eventQueue.add(new Event(Event.CHANNEL_HANGUP));
        }

        return eventQueue;
    }
}
