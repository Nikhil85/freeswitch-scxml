package com.telmi.msc.freeswitch;

//import com.telmi.msc.freeswitch.FSEventName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
@Immutable
public final class FSEvent {

    private static final Logger LOG =
            LoggerFactory.getLogger(FSEvent.class);
    private final Set<FSEventName> channelEvents;
    private final List<DTMFMessage> dtmfEvents;
    private static final int LIST_SIZE = 15;
    private static final Map<DTMFMessage, FSEvent> dtmfCacheMap =
            new EnumMap<DTMFMessage, FSEvent>(DTMFMessage.class);
    private static final Map<FSEventName, FSEvent> eventCacheMap =
            new EnumMap<FSEventName, FSEvent>(FSEventName.class);

    static {

        for (DTMFMessage dtmf : DTMFMessage.values()) {
            dtmfCacheMap.put(dtmf, new FSEvent(dtmf));
        }

        for (FSEventName event : FSEventName.values()) {
            eventCacheMap.put(event, new FSEvent(event));
        }

    }

    /**
     *
     * Create a new IvrEvent with a ChannelEvent.
     *
     * @param channelEvent The first channel event.
     */
    private FSEvent(FSEventName channelEvent) {
        this.channelEvents = EnumSet.of(channelEvent);
        this.dtmfEvents = Collections.emptyList();
    }

    /**
     *
     * Create a new IvrEvent with a ChannelEvent.
     *
     * @param dtmf The dtmf event.
     */
    private FSEvent(DTMFMessage dtmf) {
        this.dtmfEvents = new ArrayList<DTMFMessage>(LIST_SIZE);
        this.dtmfEvents.add(dtmf);
        this.channelEvents = EnumSet.of(FSEventName.DTMF);
    }

    /**
     * Create a new IvrEvent from a given builder.
     *
     * @param builder The builder to use.
     */
    private FSEvent(EventCatcher builder) {
        this.channelEvents = builder.events;
        this.dtmfEvents = builder.dtmfs;
    }

    /**
     * Get the single instance of a predefined IvrEvent.
     *
     * @param event The associated ChannelEvent.
     *
     * @return      The single instance.
     */
    public static FSEvent getInstance(final FSEventName event) {
        return eventCacheMap.get(event);
    }

    /**
     * Get the single instance of a predefined IvrEvent.
     *
     * @param  dtmf The associated ChannelEvent.
     *
     * @return The  single instance.
     **/
    public static FSEvent getInstance(final DTMFMessage dtmf) {
        return dtmfCacheMap.get(dtmf);
    }

    /**
     * Test if this IvrEvent has any of the DTMF events
     * in the dtmfs Set.
     *
     * @param dtmfs
     *        The DTMF's to check.
     *
     *
     * @return true if this IvrEvent contains any of the DTMF elements
     *         in the Set given as an argument, false otherwise.
     *
     */
    public boolean containsAny(final Set<DTMFMessage> dtmfs) {

        if (dtmfEvents != null) {
            for (DTMFMessage term : dtmfs) {
                if (dtmfEvents.contains(term)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Turns a this list of DTMFs to a String by calling
     * each DTMF found in the set <code>toString</code>
     * method. The String will have the following format<br />
     * <b>126121#</b>
     *
     * @return A String.
     */
    public String dtmfsAsString() {

        StringBuilder sb = new StringBuilder();

        for (DTMFMessage dtmf : dtmfEvents) {
            sb.append(dtmf.toString());
        }
        return sb.toString();
    }

    /**
     * Turns a this list of DTMFs to a String by calling
     * each DTMF found in the set <code>toString</code>
     * method. The String will have the following format<br />
     * <b>126121#</b>
     *
     * @param filter A Set of digits to filter out.
     *
     * @return A String.
     *
     */
    public String dtmfsAsString(Set<DTMFMessage> filter) {

        StringBuilder sb = new StringBuilder();

        for (DTMFMessage dtmf : dtmfEvents) {
            if (!filter.contains(dtmf)) {
                sb.append(dtmf.toString());
            }
        }
        return sb.toString();
    }

    /**
     * Get the size of the DTMF list hold by this class.
     *
     * @return The size in int.
     */
    public int sizeOfDtmfs() {
        return dtmfEvents.size();
    }

    /**
     *
     * @return The element at the given index.
     */
    public DTMFMessage getSingleResult() {
        return dtmfEvents.get(0);
    }

    /**
     * See if a channel event is present in this IVR event.
     *
     * @param channelEvent The event to test against.
     *
     * @return true if exists false otherwise.
     */
    public boolean contains(FSEventName channelEvent) {
        return channelEvents.contains(channelEvent);
    }

    /**
     * See if a channel event is present in this IVR event.
     *
     * @param dtmf The dtmf to test against.
     *
     * @return true if exists false otherwise.
     */
    public boolean contains(DTMFMessage dtmf) {
        return dtmfEvents.contains(dtmf);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FSEvent other = (FSEvent) obj;

        if ((this.channelEvents != other.channelEvents)
                && (!this.channelEvents.equals(other.channelEvents))) {
            return false;
        }

        if ((this.dtmfEvents != other.dtmfEvents)
                && (!this.dtmfEvents.equals(other.dtmfEvents))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.channelEvents.hashCode();
        hash = 29 * hash + this.dtmfEvents.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "FSEvent{" + "channelEvents=" + channelEvents + " dtmfEvents=" + dtmfEvents + '}';
    }

    /**
     * A class for building IvrEvents.
     *
     * @author jocke.
     */
    public static final class EventCatcher {

        private final List<DTMFMessage> dtmfs;
        /**
         * Collection with FSEventsNames to listen for
         */
        private final Set<FSEventName> events;
        private final BlockingQueue<FSEvent> eventQueue;
        private int maxNumOfDTMFChars = Integer.MAX_VALUE;
        private Set<DTMFMessage> termDtmfs = Collections.emptySet();

        ;

        /**
         * Create a new Builder with a given queue to pop events from.
         *
         * @param queue Holds events that this will consume.
         */
        public EventCatcher(BlockingQueue<FSEvent> queue) {
            this.dtmfs = new ArrayList<DTMFMessage>();
            this.events = EnumSet.noneOf(FSEventName.class);
            this.eventQueue = queue;
        }

        /**
         * Create a new IvrEvent.
         *
         * @return a new IvrEvent.
         */
        public FSEvent newFSEvent() {
            return new FSEvent(this);
        }

        /**
         * The max amount of DTMF events to collect.
         * <p>
         *  If <pre>{@code maxDigits <= 0 }</pre>
         *  maxDigits defaults to Integer.MAX_VALUE
         * </p>
         *
         * @param maxDigits A non negative number.
         * @return this.
         */
        public EventCatcher maxDigits(final int maxDigits) {
            this.maxNumOfDTMFChars = maxDigits > 0 ? maxDigits : Integer.MAX_VALUE;
            return this;
        }

        /**
         * Set digits that would stop collection of events.
         *
         * @param terms If null will default to empty Set.
         *
         * @return this.
         */
        public EventCatcher termDigits(Set<DTMFMessage> terms) {
            this.termDtmfs = terms;
            return this;
        }

        /**
         * Start the collection of IvrEvents.
         *
         * @return this.
         */
        public EventCatcher startPolling() {
            do {

                FSEvent event = null;

                try {
                    event = this.eventQueue.poll(5, TimeUnit.MINUTES);

                    if (event == null) {
                        this.events.add(FSEventName.CHANNEL_HANGUP);
                    } else {
                        dtmfs.addAll(event.dtmfEvents);
                        events.addAll(event.channelEvents);
                    }
                } catch (InterruptedException ex) {
                    LOG.error("Oops! event poll timout", ex);
                }

            } while (!buildIsFinal()
                    && !endsWithDtmf(termDtmfs)
                    && !(maxNumOfDTMFChars <= dtmfs.size()));

            return this;
        }

        /**
         *
         * Reset the builder so it can collect more events.
         *
         * @return this.
         */
        public EventCatcher reset() {
            if (events != null) {
                events.remove(FSEventName.CHANNEL_EXECUTE_COMPLETE);
            }
            LOG.trace("event to catch: size = {}", events.size());
            return this;
        }

        /**
         * Test if this builder has collected a final event.
         *
         * @return true if it has false otherwise.
         */
        private boolean buildIsFinal() {
            return (events.contains(FSEventName.CHANNEL_EXECUTE_COMPLETE)
                    || events.contains(FSEventName.TIMEOUT)
                    || events.contains(FSEventName.CHANNEL_HANGUP));
        }

        /**
         * Check if the last collected digit was a terminator.
         *
         * @param terms Terminators to test against.
         *
         * @return true if last digit is a term digit false otherwise.
         *
         */
        public boolean endsWithDtmf(Set<DTMFMessage> terms) {

            if (dtmfs.isEmpty()) {
                return false;

            } else {
                return terms.contains(dtmfs.get(dtmfs.size() - 1));
            }

        }


        /**
         * Test if this builder has an ChannelEvent.
         *
         * @param channelEvent The ChannelEvent to test.
         *
         * @return true if it has false otherwise.
         */
        public boolean contains(FSEventName channelEvent) {
            return events.contains(channelEvent);
        }
    }
}
