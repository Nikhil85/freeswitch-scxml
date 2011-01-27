package org.freeswitch.adapter;

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
    private final long sessionid;
    //
    // instance fields
    //
    private boolean notAnswered = true;
    private volatile boolean alive = true;
    private final CommandExecutor executor;

    /**
     * Create a new instance of FsIvrSession.
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
        this.sessionid = 1;
        this.executor = executor;

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
        LOG.trace("FSSession#{}: answer ...");


        if (notAnswered) {
            writeData(Command.answer());
            //Collect events until CHANNEL EXECUTE COMPLETE or hangup
            Event evt = new Event.EventCatcher(eventQueue).startPolling().newFSEvent();
            this.notAnswered = false;
            return evt;
        } else {
            return Event.getInstance(EventName.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public Event say(
            String moduleName, String sayType, String sayMethod, String value) {

        LOG.trace("FSSession#{}: say ...");

        writeData(Command.say(moduleName, sayType, sayMethod, value));
        Event evt = new Event.EventCatcher(eventQueue).startPolling().newFSEvent();
        return evt;
    }

    @Override
    public Event beep() {
        Event.EventCatcher eventcatcher = new Event.EventCatcher(eventQueue);
        writeData(Command.playback("tone_stream://%(500, 0, 800)", true));
        eventcatcher.startPolling().reset();

        writeData(Command.playback("silence_stream://10", true));
        eventcatcher.startPolling().reset();

        return eventcatcher.newFSEvent();
    }

    @Override
    public Event recordFile(
            final int timeLimitInMillis,
            final boolean beep,
            Set<DTMF> terms,
            String format) {

        LOG.trace("FSSession#{}: Try to recordFile");

        Event.EventCatcher eventcatcher = new Event.EventCatcher(eventQueue).termDigits(terms);

        if (beep) {
            writeData(Command.playback("tone_stream://%(500, 0, 800)", true));
            eventcatcher.startPolling().reset();

            writeData(Command.playback("silence_stream://10", true));
            eventcatcher.startPolling().reset();
        }

        String dstFileName = getRecPath(format);
        writeData(Command.record(dstFileName, timeLimitInMillis, null, null, false));

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
        return eventcatcher.newFSEvent();
    }

    @Override
    public Event speak(String text) {

        LOG.debug("FSSession#{}: speak ...");

        writeData(Command.speak(text, false));

        Event evt = new Event.EventCatcher(eventQueue).startPolling().newFSEvent();

        return evt;
    }

    @Override
    public Event getDigits(int maxdigits, Set<DTMF> terms, long timeout) {
        LOG.debug("FSSession#{}: getDigits ...");
        ScheduledFuture<EventName> future =
                scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);

        Event evt =
                new Event.EventCatcher(eventQueue).maxDigits(maxdigits).termDigits(terms).startPolling().newFSEvent();

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

        LOG.info("FSSession#{}: read ...  timeout -->{}", timeout);

        writeData(Command.playback(prompt, false));
//        writeData(String.format(FSCommand.PLAY, prompt));
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

        return builder.newFSEvent();
    }

    @Override
    public Event streamFile(String file) {
        LOG.debug("FSSession#{}: StreamFile: {}", sessionid, file);

        writeData(Command.playback(file, false));

        return new Event.EventCatcher(eventQueue).startPolling().newFSEvent();
    }

    @Override
    public Event streamFile(String file, Set<DTMF> terms) {
        LOG.debug("FSSession#{}: StreamFile: {}, with termination options", sessionid, file);

        writeData(Command.playback(file, false));

        Event.EventCatcher builder = new Event.EventCatcher(eventQueue).termDigits(terms).startPolling();

        if (builder.endsWithDtmf(terms)) {
            breakAction(builder.reset());
        }
        return builder.newFSEvent();
    }

    @Override
    public void sleep(long milliseconds) {
        try {
            LOG.debug("FSSession#{}: Channel Silence for '{}' millis.", sessionid, milliseconds);
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            LOG.warn("FSSession#{}: Thread interupted while sleeping, {}", sessionid, ex.getMessage());
        }
    }

    @Override
    public Event hangup(Map<String, Object> nameList) {
        LOG.debug("FSSession#{}: hang up ...", sessionid);

        writeData(Command.set(EVENT_MAP_EQUALS + nameList.toString()));
        Event.EventCatcher builder = new Event.EventCatcher(eventQueue).startPolling();
        builder.reset();

        writeData(Command.hangup(null));
        return builder.startPolling().newFSEvent();
    }

    @Override
    public Event hangup() {
        LOG.trace("FSSession#{}: hangup ...", sessionid);

        if (alive) {
            //Only call hangup once.
            alive = false;
            writeData(Command.hangup(null));
            Event evt = new Event.EventCatcher(eventQueue).startPolling().newFSEvent();
            return evt;
        } else {
            return Event.getInstance(EventName.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public Event deflect(String target) {
        LOG.trace("FSSession#{}: deflect ...", sessionid);
        writeData(Command.refer(target));
        return new Event.EventCatcher(eventQueue).startPolling().newFSEvent();
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
        LOG.debug("FSSession#{}: breakAction ...", sessionid);

        writeData(Command.breakcommand());
        sleep(1000L);
        return builder.startPolling().reset().startPolling().reset();
    }

    @Override
    public EventName call() {
        LOG.trace("FSSession#{}: call ...", sessionid);
        eventQueue.add(Event.getInstance(EventName.TIMEOUT));

        LOG.debug("FSSession#{}: Call Action timed out ???", sessionid);
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
    private void writeData(String data) {
        
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
