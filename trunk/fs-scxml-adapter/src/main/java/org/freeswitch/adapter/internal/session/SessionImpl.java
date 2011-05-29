package org.freeswitch.adapter.internal.session;

import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.session.Session;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventQueue;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.adapter.api.event.EventQueueListener;
import org.freeswitch.adapter.api.Extension;
import org.freeswitch.adapter.api.HangupException;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 *
 */
public final class SessionImpl implements Session, Callable<Boolean>, SessionState {

    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);
    private final Map<String, Object> sessionData;
    private final EventQueue eventQueue;
    private final ScheduledExecutorService scheduler;
    private final String sessionid;
    private boolean notAnswered = true;
    private volatile boolean alive = true;
    private final CommandExecutor executor;
    private SpeechAdapter speechAdapter;
    private AudioAdapter audioAdapter;
    private ControlAdapter controlAdapter;
    private DigitsAdapter digitsAdapter;
    private CallAdapter callAdapter;
    private Lookup lkp;
    private InstanceContent content;
    private Command cmd;

    public SessionImpl(Map<String, Object> map, EventQueue eq, CommandExecutor executor) {
        this.sessionData = map;
        this.eventQueue = eq;
        this.scheduler = Lookup.getDefault().lookup(ScheduledExecutorService.class);
        this.executor = executor;
        this.sessionid = getUuid();
        this.content = new InstanceContent();
        this.cmd = new Command(getUuid());
        this.speechAdapter = new SpeechAdapter(this, cmd);
        this.audioAdapter = new AudioAdapter(this, cmd);
        this.controlAdapter = new ControlAdapter(this, this, cmd);
        this.digitsAdapter = new DigitsAdapter(this, cmd);
        this.callAdapter = new CallAdapter(this);
        content.add(speechAdapter);
        content.add(audioAdapter);
        content.add(controlAdapter);
        content.add(digitsAdapter);
        this.lkp = new ProxyLookup(Lookup.getDefault(), new AbstractLookup(content));

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
        return (String) sessionData.get("Unique-ID");
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    @Override
    public boolean isNotAnswered() {
        return notAnswered;
    }

    @Override
    public void setNotAnswered(boolean notAnswered) {
        this.notAnswered = notAnswered;
    }

    @Override
    public EventList answer() throws HangupException {
        return controlAdapter.answer();
    }

    @Override
    public EventList say(String moduleName, String sayType, String sayMethod, String value) throws HangupException {
        return this.speechAdapter.say(moduleName, sayType, sayMethod, value);
    }

    @Override
    public EventList beep() throws HangupException {
        return audioAdapter.beep();
    }

    @Override
    public EventList recordFile(int timeLimitInMillis, boolean beep, Set<DTMF> terms, String format) throws HangupException {
        return audioAdapter.recordFile(timeLimitInMillis, beep, terms, format);
    }

    @Override
    public EventList speak(String text) throws HangupException {
        return speechAdapter.speak(text);
    }

    @Override
    public EventList getDigits(int maxdigits, Set<DTMF> terms, long timeout) throws HangupException {
        return digitsAdapter.getDigits(maxdigits, terms, timeout);
    }

    @Override
    public EventList read(int maxDigits, String prompt, long timeout, Set<DTMF> terms) throws HangupException {
        return digitsAdapter.read(maxDigits, prompt, timeout, terms);
    }

    public ScheduledFuture<Boolean> scheduleTimeout(long timeout) {
        return scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public EventList streamFile(String file) throws HangupException {
        return audioAdapter.streamFile(file);
    }

    @Override
    public EventList streamFile(String file, Set<DTMF> terms) throws HangupException {
        return audioAdapter.streamFile(file, terms);
    }

    @Override
    public void sleep(long milliseconds) {
        controlAdapter.sleep(milliseconds);
    }

    @Override
    public EventList hangup() {
        try {
            return controlAdapter.hangup();
        } catch (HangupException ex) {
            return EventListBuilder.single(Event.CHANNEL_HANGUP);
        }
    }

    @Override
    public EventList deflect(String target) throws HangupException {
        LOG.trace("Session#{}: deflect ...", sessionid);
        execute(cmd.refer(target));
        return new EventListBuilder(eventQueue).consume().build();
    }

    @Override
    public boolean clearDigits() {
        return eventQueue.clearDigits();
    }

    public EventList breakAction() throws HangupException {
        return controlAdapter.breakAction();
    }

    public <T> T lookup(Class<T> clazz) {
        T lookup = lkp.lookup(clazz);

        if (isMissingExtension(lookup, clazz)) {
            try {
                return createExtension(clazz);
            } catch (NoSuchMethodException | SecurityException | InstantiationException |
                    IllegalAccessException | InvocationTargetException ex) {
                LOG.error(ex.getMessage());
                return null;
            }
        } else {
            return lookup;
        }
    }

    private <T> boolean isMissingExtension(T lookup, Class<T> clazz) {
        return lookup == null && Extension.class.isAssignableFrom(clazz);
    }

    private <T> T createExtension(Class<T> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException, SecurityException, InvocationTargetException, NoSuchMethodException {
        T obj = clazz.getConstructor(Session.class).newInstance(this);
        content.add(obj);
        return obj;
    }

    @Override
    public Boolean call() {
        LOG.debug("Session#{}: Call Action timed out ", sessionid);
        return eventQueue.add(Event.named(Event.TIMEOUT));
    }

    @Override
    public void call(String value, EventQueueListener listener) {
          callAdapter.call(value,listener);
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

    @Override
    public void setAlive(boolean b) {
        this.alive = b;
    }
}
