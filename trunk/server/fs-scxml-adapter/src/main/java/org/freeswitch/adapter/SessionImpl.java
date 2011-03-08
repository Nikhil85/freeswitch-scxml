package org.freeswitch.adapter;

import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.adapter.api.EventName;
import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 *
 */
public final class SessionImpl implements Session, Callable<EventName> { //NOPMD
    
    public static final String REC_PATH = "REC_PATH";
    //
    // Class variables
    //
    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);
    private static final String EVENT_MAP_EQUALS = "sip_bye_h_X-EventMap=";
    //
    // final instance fields
    //
    private final Map<String, Object> sessionData;
    private final BlockingQueue<Event> eventQueue;
    private final ScheduledExecutorService scheduler;
    private final String recPath;
    private final String sessionid;
    //
    // instance fields
    //
    private boolean notAnswered = true;
    private volatile boolean alive = true;
    private final CommandExecutor executor;

    /**
     * Create a new instance of IvrSession.
     *
     * @param data           Variables.
     * @param con            Connection to write to.
     * @param eventScheduler Scheduler for events.
     * @param recordingPath  Where to save recordings.
     * @param blockingQueue  Queue with events.
     *
     */
    public SessionImpl(
            final Map<String, Object> data,
            final CommandExecutor executor,
            final ScheduledExecutorService eventScheduler,
            final String recordingPath,
            final BlockingQueue<Event> blockingQueue) {

        this.sessionData = data;
        this.scheduler = eventScheduler;
        this.recPath = recordingPath;
        this.eventQueue = blockingQueue;
        this.executor = executor;
        this.sessionid = getUuid();
    }

    public SessionImpl(Map<String, Object> map) {
        this.sessionData = map;
        this.scheduler = (ScheduledExecutorService) map.get(ScheduledExecutorService.class.getName());
        this.recPath = (String) map.get(REC_PATH);
        this.eventQueue = (BlockingQueue<Event>) map.get(BlockingQueue.class.getName());
        this.executor = (CommandExecutor) map.get(CommandExecutor.class.getName());
        this.sessionid = getUuid();
    }

    /**
     * Get the event queue in tests.
     *
     *
     * @return The event queue.
     */
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
    public Event answer() {
        LOG.trace("Session#{}: answer ...");
        if (notAnswered) {
            excecute(Command.answer());
            Event evt = new Event.EventCatcher(eventQueue).startPolling().newEvent();
            this.notAnswered = false;
            return evt;
        } else {
            return Event.getInstance(EventName.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public Event say(String moduleName, String sayType, String sayMethod, String value) {

        LOG.trace("Session#{}: say ...");
        excecute(Command.say(moduleName, sayType, sayMethod, value));
        Event evt = new Event.EventCatcher(eventQueue).startPolling().newEvent();
        return evt;
    }

    @Override
    public Event beep() {
        Event.EventCatcher eventcatcher = new Event.EventCatcher(eventQueue);
        excecute(Command.playback("tone_stream://%(500, 0, 800)", true));
        eventcatcher.startPolling().reset();

        excecute(Command.playback("silence_stream://10", true));
        eventcatcher.startPolling().reset();

        return eventcatcher.newEvent();
    }

    @Override
    public Event recordFile(
            final int timeLimitInMillis,
            final boolean beep,
            Set<DTMF> terms,
            String format) {

        LOG.trace("Session#{}: Try to recordFile");

        Event.EventCatcher eventcatcher = new Event.EventCatcher(eventQueue).termDigits(terms);

        if (beep) {
            excecute(Command.playback("tone_stream://%(500, 0, 800)", true));
            eventcatcher.startPolling().reset();

            excecute(Command.playback("silence_stream://10", true));
            eventcatcher.startPolling().reset();
        }

        String dstFileName = getRecPath(format);
        excecute(Command.record(dstFileName, timeLimitInMillis, null, null, false));

        long now = System.nanoTime();
        eventcatcher.startPolling();
        long after = System.nanoTime();

        if (eventcatcher.endsWithDtmf(terms)) {
            //Action was stopped because of termdigit.
            eventcatcher.reset();
            breakAction(eventcatcher);
        }

        Long duration = TimeUnit.MILLISECONDS.convert(after - now, TimeUnit.NANOSECONDS);
        sessionData.put("duration", duration);
        sessionData.put("last_rec", dstFileName);
        return eventcatcher.newEvent();
    }

    @Override
    public Event speak(String text) {

        LOG.debug("Session#{}: speak ...");

        excecute(Command.speak(text, false));

        Event evt = new Event.EventCatcher(eventQueue).startPolling().newEvent();

        return evt;
    }

    @Override
    public Event getDigits(int maxdigits, Set<DTMF> terms, long timeout) {
        LOG.debug("Session#{}: getDigits ...");
        ScheduledFuture<EventName> future = scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);
        Event evt = new Event.EventCatcher(eventQueue).maxDigits(maxdigits).termDigits(terms).startPolling().newEvent();

        if (!future.isDone()) {
            future.cancel(true);
        }

        return evt;
    }

    @Override
    public Event read(
            int maxDigits,
            String prompt,
            long timeout,
            Set<DTMF> terms) {

        LOG.info("Session#{}: read ...  timeout -->{}", timeout);

        excecute(Command.playback(prompt, false));
//        writeData(String.format(Command.PLAY, prompt));
        Event.EventCatcher builder =
                new Event.EventCatcher(eventQueue).maxDigits(maxDigits).termDigits(terms).startPolling();

        if (builder.contains(EventName.CHANNEL_EXECUTE_COMPLETE)) {
            LOG.trace("the prompt playing was stopped, start timer", sessionid);
            ScheduledFuture<EventName> future =
                    scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);

            builder.reset().startPolling();
            if (!future.isDone()) {
                future.cancel(false);
            }

        } else if (!builder.contains(EventName.CHANNEL_HANGUP)) {
            LOG.trace("the prompt is still playing, cancel it", sessionid);
            breakAction(builder.reset());
        }

        return builder.newEvent();
    }

    @Override
    public Event streamFile(String file) {
        LOG.debug("Session#{}: StreamFile: {}", sessionid, file);

        excecute(Command.playback(file, false));

        return new Event.EventCatcher(eventQueue).startPolling().newEvent();
    }

    @Override
    public Event streamFile(String file, Set<DTMF> terms) {
        LOG.debug("Session#{}: StreamFile: {}, with termination options", sessionid, file);

        excecute(Command.playback(file, false));

        Event.EventCatcher builder = new Event.EventCatcher(eventQueue).termDigits(terms).startPolling();

        if (builder.endsWithDtmf(terms)) {
            breakAction(builder.reset());
        }
        return builder.newEvent();
    }

    @Override
    public void sleep(long milliseconds) {
        try {
            LOG.debug("Session#{}: Channel Silence for '{}' millis.", sessionid, milliseconds);
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            LOG.warn("Session#{}: Thread interupted while sleeping, {}", sessionid, ex.getMessage());
        }
    }

    @Override
    public Event hangup(Map<String, Object> nameList) {
        LOG.debug("Session#{}: hang up ...", sessionid);

        excecute(Command.set(EVENT_MAP_EQUALS + nameList.toString()));
        Event.EventCatcher builder = new Event.EventCatcher(eventQueue).startPolling();
        builder.reset();

        excecute(Command.hangup(null));
        return builder.startPolling().newEvent();
    }

    @Override
    public Event hangup() {
        LOG.trace("Session#{}: hangup ...", sessionid);

        if (alive) {
            //Only call hangup once.
            alive = false;
            excecute(Command.hangup(null));
            Event evt = new Event.EventCatcher(eventQueue).startPolling().newEvent();
            return evt;
        } else {
            return Event.getInstance(EventName.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public Event deflect(String target) {
        LOG.trace("Session#{}: deflect ...", sessionid);
        excecute(Command.refer(target));
        return new Event.EventCatcher(eventQueue).startPolling().newEvent();
    }

    @Override
    public boolean clearDigits() {
        boolean removed = false;
        if (!eventQueue.isEmpty()) {
            for (Event ivrEvent : eventQueue) {
                if (!ivrEvent.contains(EventName.CHANNEL_HANGUP)) {
                    eventQueue.remove(ivrEvent);
                    removed = true;
                }
            }
        }
        return removed;
    }

    /**
     * In break we need to consume the the CHANNEL_EXECUTE_COMPLETE
     * event for break and the action we are breaking.
     *
     * @param builder The builder to use.
     *
     * @return The events from the break action.
     */
    private Event.EventCatcher breakAction(Event.EventCatcher builder) {
        LOG.debug("Session#{}: breakAction ...", sessionid);

        excecute(Command.breakcommand());
        sleep(1000L);
        return builder.startPolling().reset().startPolling().reset();
    }

    @Override
    public EventName call() {
        LOG.trace("Session#{}: call ...", sessionid);
        eventQueue.add(Event.getInstance(EventName.TIMEOUT));

        LOG.debug("Session#{}: Call Action timed out ???", sessionid);
        return EventName.TIMEOUT;
    }

    /**
     * Create path to store recording.
     *
     * @param  format wav or mp3.
     *
     * @return Path to recording
     *
     */
    public String getRecPath(String format) {
        String file = "";
        if (format != null && "mp3".equalsIgnoreCase(format)) {
            file = recPath + "/" + getUuid() + ".mp3";
        } else {
            file = recPath + "/" + getUuid() + ".wav";
        }
        return file;
    }

    /**
     * write a string on the socket.
     *
     * @param data To send.
     */
    private void excecute(String data) {
        
        if (executor.isReady()) {

            try {
                executor.execute(data);

            } catch (IOException ex) {
                LOG.error("writeData error, trigger CHANNEL_HANGUP event. {}", ex.getMessage());
                eventQueue.add(Event.getInstance(EventName.CHANNEL_HANGUP));
            }

        } else {
            eventQueue.add(Event.getInstance(EventName.CHANNEL_HANGUP));
        }
    }
}
