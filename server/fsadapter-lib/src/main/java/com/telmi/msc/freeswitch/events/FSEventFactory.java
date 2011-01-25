/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telmi.msc.freeswitch.events;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kristofer
 */
public final class FSEventFactory {

    public enum FSEventEnum {

        /**
         * CHANNEL_CREATE.
         * Channel create is sent when an extension is going to do something.
         * It can either be dialling someone or it can be an incoming call to an extension.
         * The event do not have any additional information.
         */
        CHANNEL_CREATE(GenericChannelEvent.class),
        CHANNEL_DESTROY(GenericChannelEvent.class),
        CHANNEL_STATE(GenericChannelEvent.class),
        CHANNEL_ANSWER(GenericChannelEvent.class),
        CHANNEL_HANGUP(GenericChannelEvent.class),
        /**
         * The channel successfully executed an action.
         */
        CHANNEL_HANGUP_COMPLETE(GenericChannelEvent.class),
        CHANNEL_EXECUTE(GenericChannelEvent.class),
        CHANNEL_EXECUTE_COMPLETE(ChannelExecuteCompleteEvent.class),
        CHANNEL_BRIDGE(GenericChannelEvent.class),
        CHANNEL_UNBRIDGE(GenericChannelEvent.class),
        CHANNEL_PROGRESS(GenericChannelEvent.class),
        CHANNEL_PROGRESS_MEDIA(GenericChannelEvent.class),
        CHANNEL_OUTGOING(GenericChannelEvent.class),
        CHANNEL_PARK(GenericChannelEvent.class),
        CHANNEL_UNPARK(GenericChannelEvent.class),
        CHANNEL_APPLICATION(GenericChannelEvent.class),
        CHANNEL_HOLD(GenericChannelEvent.class),
        CHANNEL_UNHOLD(GenericChannelEvent.class),
        CHANNEL_ORIGINATE(GenericChannelEvent.class),
        CHANNEL_UUID(GenericChannelEvent.class),
        //
        // System Events
        //
        SHUTDOWN(GenericSystemEvent.class),
        MODULE_LOAD(GenericSystemEvent.class),
        MODULE_UNLOAD(GenericSystemEvent.class),
        RELOADXML(GenericSystemEvent.class),
        NOTIFY(GenericSystemEvent.class),
        SEND_MESSAGE(GenericSystemEvent.class),
        RECV_MESSAGE(GenericSystemEvent.class),
        REQUEST_PARAMS(GenericSystemEvent.class),
        CHANNEL_DATA(GenericSystemEvent.class),
        GENERAL(GenericSystemEvent.class),
        COMMAND(GenericSystemEvent.class),
        SESSION_HEARTBEAT(GenericSystemEvent.class),
        CLIENT_DISCONNECTED(GenericSystemEvent.class),
        SERVER_DISCONNECTED(GenericSystemEvent.class),
        SEND_INFO(GenericSystemEvent.class),
        RECV_INFO(GenericSystemEvent.class),
        CALL_SECURE(GenericSystemEvent.class),
        NAT(GenericSystemEvent.class),
        RECORD_START(GenericSystemEvent.class),
        RECORD_STOP(GenericSystemEvent.class),
        CALL_UPDATE(GenericSystemEvent.class),
        //
        // other events
        //
        API(GenericEvent.class),
        BACKGROUND_JOB(GenericEvent.class),
        CUSTOM(GenericEvent.class),
        RE_SCHEDULE(GenericEvent.class),
        HEARTBEAT(GenericEvent.class),
        DETECTED_TONE(GenericEvent.class),
        ALL(GenericEvent.class),
        //
        // Undocumented events
        //
        LOG(GenericEvent.class),
        INBOUND_CHAN(GenericEvent.class),
        OUTBOUND_CHAN(GenericEvent.class),
        STARTUP(GenericEvent.class),
        PUBLISH(GenericEvent.class),
        UNPUBLISH(GenericEvent.class),
        TALK(GenericEvent.class),
        NOTALK(GenericEvent.class),
        SESSION_CRASH(GenericEvent.class),
        DTMF(DTMFEvent.class),
        MESSAGE(GenericEvent.class),
        PRESENCE_IN(GenericEvent.class),
        PRESENCE_OUT(GenericEvent.class),
        PRESENCE_PROBE(GenericEvent.class),
        MESSAGE_WAITING(GenericEvent.class),
        MESSAGE_QUERY(GenericEvent.class),
        ROSTER(GenericEvent.class),
        CODEC(GenericEvent.class),
        DETECTED_SPEECH(GenericEvent.class),
        PRIVATE_COMMAND(GenericEvent.class),
        TRAP(GenericEvent.class),
        ADD_SCHEDULE(GenericEvent.class),
        DEL_SCHEDULE(GenericEvent.class),
        EXE_SCHEDULE(GenericEvent.class),
        TERMDIGIT(GenericEvent.class),
        /**
         * The action timed out.
         */
        TIMEOUT(GenericEvent.class),
        /**
         * Channel found error.
         */
        ERROR(GenericEvent.class);

        private FSEventEnum(Class clazz) {
            this.eventClass = clazz;
        }
        private final Class eventClass;

        private Class getEventClass() {
            return eventClass;
        }
    }
    private static final Pattern EVENT_PATTERN = Pattern.compile("(Event-Name:)(\\s)(\\w*)", Pattern.MULTILINE);

    public static AbstractEvent getFSEvent(final String data) throws NoSuchEventException {
        AbstractEvent result = null;
        Matcher matcher = EVENT_PATTERN.matcher(data);
        if (matcher.find()) {
            String eventname = null;
            try {
                // uses enum map and reflection to create new instance.
                eventname = matcher.group(3);
                result = (AbstractEvent) FSEventFactory.FSEventEnum.valueOf(eventname).getEventClass().getConstructor(String.class, String.class).newInstance(eventname, data);
            } catch (Exception ex) {
                throw new NoSuchEventException(eventname, ex);
            }
        }
        return result;
    }
}
