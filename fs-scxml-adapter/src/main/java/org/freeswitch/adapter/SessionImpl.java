package org.freeswitch.adapter;

import java.util.Iterator;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.Session;
import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventList.EventListBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 *
 */
public final class SessionImpl implements Session, Callable<Event> { //NOPMD
    public static final String BEEP_TONE = "tone_stream://%(500, 0, 800)";
    public static final String REC_PATH = "REC_PATH";
    public static final String SILENCE_STREAM = "silence_stream://10";
    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);
    private static final String EVENT_MAP_EQUALS = "sip_bye_h_X-EventMap=";
    private final Map<String, Object> sessionData;
    private final BlockingQueue<Event> eventQueue;
    private final ScheduledExecutorService scheduler;
    private final String sessionid;
    private boolean notAnswered = true;
    private volatile boolean alive = true;
    private final CommandExecutor executor;

    SessionImpl(
            final Map<String, Object> data,
            final CommandExecutor executor,
            final ScheduledExecutorService eventScheduler,
            final String recordingPath,
            final BlockingQueue<Event> blockingQueue) {

        this.sessionData = data;
        this.scheduler = eventScheduler;
        this.eventQueue = blockingQueue;
        this.executor = executor;
        this.sessionid = getUuid();
    }

    public SessionImpl(Map<String, Object> map) {
        this.sessionData = map;
        this.scheduler = (ScheduledExecutorService) map.get(ScheduledExecutorService.class.getName());
        this.eventQueue = (BlockingQueue<Event>) map.get(BlockingQueue.class.getName());
        this.executor = (CommandExecutor) map.get(CommandExecutor.class.getName());
        this.sessionid = getUuid();
    }

    public BlockingQueue<Event> getEventQueue() {
        return eventQueue;
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

    @Override
    public EventList answer() {
        LOG.trace("Session#{}: answer ...");

        if (notAnswered) {
            excecute(Command.answer());
            this.notAnswered = false;
            return new EventListBuilder(eventQueue).consume().build();
        } else {
            return EventList.single(Event.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public EventList say(String moduleName, String sayType, String sayMethod, String value) {
        LOG.trace("Session#{}: say ...");
        excecute(Command.say(moduleName, sayType, sayMethod, value));
        return new EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public EventList beep() {
        EventList.EventListBuilder evtBuilder = new EventList.EventListBuilder(eventQueue);
        excecute(Command.playback(BEEP_TONE, true));
        evtBuilder.consume().reset();
        excecute(Command.playback(SILENCE_STREAM, true));
        evtBuilder.consume().reset();
        return evtBuilder.build();
    }

    @Override
    public EventList recordFile(int timeLimitInMillis, boolean beep, Set<DTMF> terms, String format) {
        
        LOG.trace("Session#{}: Try to recordFile");
        EventListBuilder builder = new EventListBuilder(eventQueue).termDigits(terms);

        if (beep) {
            beep();
        }

        excecute(Command.record(getRecPath(format), timeLimitInMillis, null, null, false));

        builder.consume();

        if (builder.endsWithDtmf(terms)) {
            builder.reset();
            breakAction(builder);
        }

        return builder.build();
    }

    @Override
    public EventList speak(String text) {
        LOG.debug("Session#{}: speak ...");
        excecute(Command.speak(text, false));
        return new EventList.EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public EventList getDigits(int maxdigits, Set<DTMF> terms, long timeout) {
        LOG.debug("Session#{}: getDigits ...");
        ScheduledFuture<Event> future = scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);
        EventList evt = new EventList.EventListBuilder(eventQueue).maxDigits(maxdigits).termDigits(terms).consume().build();

        if (!future.isDone()) {
            future.cancel(true);
        }

        return evt;
    }

    @Override
    public EventList read(int maxDigits, String prompt, long timeout, Set<DTMF> terms) {
        LOG.info("Session#{}: read ...  timeout -->{}", timeout);

        excecute(Command.playback(prompt, false));
        EventListBuilder builder = new EventListBuilder(eventQueue)
                .maxDigits(maxDigits)
                .termDigits(terms)
                .consume();

        if (builder.contains(Event.CHANNEL_EXECUTE_COMPLETE)) {
            LOG.trace("the prompt playing was stopped, start timer", sessionid);
            ScheduledFuture<Event> future = scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);
            builder.reset()
                   .consume();
            
            if (!future.isDone()) {
                future.cancel(false);
            }

        } else if (!builder.contains(Event.CHANNEL_HANGUP)) {
            LOG.trace("the prompt is still playing, cancel it", sessionid);
            breakAction(builder.reset());
        }

        return builder.build();
    }

    @Override
    public EventList streamFile(String file) {
        LOG.debug("Session#{}: StreamFile: {}", sessionid, file);
        excecute(Command.playback(file, false));
        return new EventList.EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public EventList streamFile(String file, Set<DTMF> terms) {
        LOG.debug("Session#{}: StreamFile: {}, with termination options", sessionid, file);
        excecute(Command.playback(file, false));
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
        excecute(Command.set(EVENT_MAP_EQUALS + nameList.toString()));
        EventListBuilder builder = new EventListBuilder(eventQueue).consume();
        builder.reset();
        excecute(Command.hangup(null));
        return builder.consume().build();
    }

    @Override
    public EventList hangup() {
        LOG.trace("Session#{}: hangup ...", sessionid);

        if (alive) {
            alive = false;
            excecute(Command.hangup(null));
            return new EventListBuilder(eventQueue).consume().build();

        } else {
            return EventList.single(Event.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public EventList deflect(String target) {
        LOG.trace("Session#{}: deflect ...", sessionid);
        excecute(Command.refer(target));
        return new EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public boolean clearDigits() {

        boolean removed = false;
        Iterator<Event> it = eventQueue.iterator();

        while (it.hasNext()) {
            if (it.next().getEventName().equals(Event.DTMF)) {
                it.remove();
            }
        }

        return removed;
    }

    private EventListBuilder breakAction(EventListBuilder builder) {
        LOG.debug("Session#{}: breakAction ...", sessionid);
        excecute(Command.breakcommand());
        sleep(1000L);
        return builder.consume().reset().consume().reset();
    }

    @Override
    public Event call() {
        LOG.debug("Session#{}: Call Action timed out ???", sessionid);
        final Event event = new Event(Event.TIMEOUT);
        eventQueue.add(event);
        return event;
    }

    public String getRecPath(String format) {

        String recPath = System.getProperty("recording.path");

        if (recPath == null) {
            recPath = System.getProperty("java.io.tmpdir");
        }

        return "mp3".equalsIgnoreCase(format) ? recPath + "/" + getUuid() + ".mp3" : recPath + "/" + getUuid() + ".wav";

    }

    private void excecute(String data) {

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
    }
}
