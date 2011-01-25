package com.telmi.msc.freeswitch;

import com.telmi.msc.fsadapter.transport.SocketWriter;
import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.NotThreadSafe;

/**
 *
 * @author jocke
 *
 */
@NotThreadSafe
public final class FSSessionImpl implements FSSession, Callable<FSEventName> { //NOPMD

    //
    // Class variables
    //
    private static final Logger LOG = LoggerFactory.getLogger(FSSessionImpl.class);
    private static final String EVENT_MAP_EQUALS = "sip_bye_h_X-EventMap=";
    private static Long sessioncounter = 0L;
    //
    // final instance fields
    //
    private final Map<String, Object> sessionData;
    private final SocketWriter connection;
    private final BlockingQueue<FSEvent> eventQueue;
    private final ScheduledExecutorService scheduler;
    private final String recPath;
    private final long sessionid;
    //
    // instance fields
    //
    private boolean notAnswered = true;
    private volatile boolean alive = true;

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
    public FSSessionImpl(
            final Map<String, Object> data,
            final SocketWriter con,
            final ScheduledExecutorService eventScheduler,
            final String recordingPath,
            final BlockingQueue<FSEvent> blockingQueue) {

        this.sessionData = data;
        this.connection = con;
        this.scheduler = eventScheduler;
        this.recPath = recordingPath;
        this.eventQueue = blockingQueue;

        synchronized (sessioncounter) {
            sessionid = sessioncounter++;
        }
    }

    /**
     * Get the event queue in tests.
     *
     *
     * @return The event queue.
     */
    public BlockingQueue<FSEvent> getEventQueue() {
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
    public FSEvent answer() {
        LOG.trace("FSSession#{}: answer ...", sessionid);


        if (notAnswered) {
            writeData(FSCommand.answer());
            //Collect events until CHANNEL EXECUTE COMPLETE or hangup
            FSEvent evt = new FSEvent.EventCatcher(eventQueue).startPolling().newFSEvent();
            this.notAnswered = false;
            return evt;
        } else {
            return FSEvent.getInstance(FSEventName.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public FSEvent say(
            String moduleName, String sayType, String sayMethod, String value) {

        LOG.trace("FSSession#{}: say ...", sessionid);

        writeData(FSCommand.say(moduleName, sayType, sayMethod, value));
        FSEvent evt = new FSEvent.EventCatcher(eventQueue).startPolling().newFSEvent();
        return evt;
    }

    @Override
    public FSEvent beep() {
        FSEvent.EventCatcher eventcatcher = new FSEvent.EventCatcher(eventQueue);
        writeData(FSCommand.playback("tone_stream://%(500, 0, 800)", true));
        eventcatcher.startPolling().reset();

        writeData(FSCommand.playback("silence_stream://10", true));
        eventcatcher.startPolling().reset();

        return eventcatcher.newFSEvent();
    }

    @Override
    public FSEvent recordFile(
            final int timeLimitInMillis,
            final boolean beep,
            Set<DTMFMessage> terms,
            String format) {

        LOG.trace("FSSession#{}: Try to recordFile", sessionid);

        FSEvent.EventCatcher eventcatcher = new FSEvent.EventCatcher(eventQueue).termDigits(terms);

        if (beep) {
            writeData(FSCommand.playback("tone_stream://%(500, 0, 800)", true));
            eventcatcher.startPolling().reset();

            writeData(FSCommand.playback("silence_stream://10", true));
            eventcatcher.startPolling().reset();
        }

        String dstFileName = getRecPath(format);
        writeData(FSCommand.record(dstFileName, timeLimitInMillis, null, null, false));

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
    public FSEvent speak(String text) {

        LOG.debug("FSSession#{}: speak ...", sessionid);

        writeData(FSCommand.speak(text, false));
        
        FSEvent evt = new FSEvent.EventCatcher(eventQueue).startPolling().newFSEvent();

        return evt;
    }

    @Override
    public FSEvent getDigits(int maxdigits, Set<DTMFMessage> terms, long timeout) {
        LOG.debug("FSSession#{}: getDigits ...", sessionid);
        ScheduledFuture<FSEventName> future =
                scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);

        FSEvent evt =
                new FSEvent.EventCatcher(eventQueue).maxDigits(maxdigits).termDigits(terms).startPolling().newFSEvent();

        if (!future.isDone()) {
            future.cancel(true);
        }

        return evt;
    }

    @Override
    public FSEvent read(
            int maxDigits,
            String prompt,
            long timeout,
            Set<DTMFMessage> terms) {

        LOG.info("FSSession#{}: read ...  timeout -->{}", sessionid , timeout);

        writeData(FSCommand.playback(prompt, false));
//        writeData(String.format(FSCommand.PLAY, prompt));
        FSEvent.EventCatcher builder =
                new FSEvent.EventCatcher(eventQueue).maxDigits(maxDigits).termDigits(terms).startPolling();

        if (builder.contains(FSEventName.CHANNEL_EXECUTE_COMPLETE)) {
            LOG.trace("the prompt playing was stopped, start timer", sessionid);
            ScheduledFuture<FSEventName> future =
                    scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);

            builder.reset().startPolling();
            if (!future.isDone()) {
                future.cancel(false);
            }

        } else if (!builder.contains(FSEventName.CHANNEL_HANGUP)) {
            LOG.trace("the prompt is still playing, cancel it", sessionid);
            breakAction(builder.reset());
        }

        return builder.newFSEvent();
    }

    @Override
    public FSEvent streamFile(String file) {
        LOG.debug("FSSession#{}: StreamFile: {}", sessionid, file);

        writeData(FSCommand.playback(file, false));

        return new FSEvent.EventCatcher(eventQueue).startPolling().newFSEvent();
    }

    @Override
    public FSEvent streamFile(String file, Set<DTMFMessage> terms) {
        LOG.debug("FSSession#{}: StreamFile: {}, with termination options", sessionid, file);

        writeData(FSCommand.playback(file, false));
        
        FSEvent.EventCatcher builder = new FSEvent.EventCatcher(eventQueue).termDigits(terms).startPolling();

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
    public FSEvent hangup(Map<String, Object> nameList) {
        LOG.debug("FSSession#{}: hang up ...", sessionid);

        writeData(FSCommand.set(EVENT_MAP_EQUALS + nameList.toString()));
        FSEvent.EventCatcher builder = new FSEvent.EventCatcher(eventQueue).startPolling();
        builder.reset();

        writeData(FSCommand.hangup(null));
        return builder.startPolling().newFSEvent();
    }

    @Override
    public FSEvent hangup() {
        LOG.trace("FSSession#{}: hangup ...", sessionid);

        if (alive) {
            //Only call hangup once.
            alive = false;
            writeData(FSCommand.hangup(null));
            FSEvent evt = new FSEvent.EventCatcher(eventQueue).startPolling().newFSEvent();
            return evt;
        } else {
            return FSEvent.getInstance(FSEventName.CHANNEL_EXECUTE_COMPLETE);
        }
    }

    @Override
    public FSEvent deflect(String target) {
        LOG.trace("FSSession#{}: deflect ...", sessionid);
        writeData(FSCommand.refer(target));
        return new FSEvent.EventCatcher(eventQueue).startPolling().newFSEvent();
    }

    @Override
    public boolean clearDigits() {
        boolean removed = false;
        if (!eventQueue.isEmpty()) {
            for (FSEvent ivrEvent : eventQueue) {
                if (!ivrEvent.contains(FSEventName.CHANNEL_HANGUP)) {
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
    private FSEvent.EventCatcher breakAction(FSEvent.EventCatcher builder) {
        LOG.debug("FSSession#{}: breakAction ...", sessionid);

        writeData(FSCommand.breakcommand());
        sleep(1000L);
        return builder.startPolling().reset().startPolling().reset();
    }

    @Override
    public FSEventName call() {
        LOG.trace("FSSession#{}: call ...", sessionid);
        eventQueue.add(FSEvent.getInstance(FSEventName.TIMEOUT));

        LOG.debug("FSSession#{}: Call Action timed out ???", sessionid);
        return FSEventName.TIMEOUT;
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
        if (connection.isConnected()) {

            try {
                connection.write(data);

            } catch (IOException ex) {
               LOG.error("writeData error, trigger CHANNEL_HANGUP event. {}", ex.getMessage());
               eventQueue.add(FSEvent.getInstance(FSEventName.CHANNEL_HANGUP));
            }

        } else {
            eventQueue.add(FSEvent.getInstance(FSEventName.CHANNEL_HANGUP));
        }
    }
}
