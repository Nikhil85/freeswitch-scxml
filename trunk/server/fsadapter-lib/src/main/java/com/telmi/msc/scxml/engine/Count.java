package com.telmi.msc.scxml.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An class that keeps track on how many
 * times an event has happened in a state.
 * It is up to listener class to reset
 * this counter between states.
 *
 * @author jocke
 */
public final class Count {

    private transient Integer maxtime = 1;
    private transient Integer nomatch = 1;
    private transient Integer mindigits = 1;
    private transient Integer maxdigits = 1;
    private static final String NOMATCH = "nomatch";
    private static final String MAXTIME = "maxtime";
    private static final String MINDIGITS = "mindigits";
    private static final String MAXDIGITS = "maxdigits";
    private final Map<String, Integer> counts = new HashMap<String, Integer>();

    /**
     * Create a new instance of count.
     */
    Count() {
        counts.put(NOMATCH, nomatch);
        counts.put(MAXTIME, maxtime);
        counts.put(MINDIGITS, mindigits);
        counts.put(MAXDIGITS, maxdigits);

    }

    /**
     * Get the value of nomatch.
     *
     * @return the value of nomatch
     */
    public int getNomatch() {
        return counts.get(NOMATCH);
    }

    /**
     * Get the value of maxtime.
     *
     * @return the value of maxtime
     */
    public int getMaxtime() {
        return counts.get(MAXTIME);
    }

    /**
     * Get the value of maxdigits.
     *
     * @return The value of maxdigits
     */
    public int getMaxdigits() {
        return counts.get(MAXDIGITS);
    }

    /**
     * Get the value of mindigits.
     *
     * @return The value of mindigits
     */
    public int getMindigits() {
        return counts.get(MINDIGITS);
    }

    /**
     * Get the largest counter in
     * this count.
     *
     * @return The largest count.
     */
    public int getAny() {

        final Collection<Integer> sortedColl = counts.values();

        final List<Integer> sortedL = new ArrayList<Integer>(sortedColl);

        Collections.sort(sortedL);

        return sortedL.get(sortedL.size() - 1);
    }

    public int getSum() {


        final Collection<Integer> values = counts.values();

        int sum = -values.size() + 1;


        for (Integer integer : values) {
            sum = sum + integer;
        }

        
        return sum;
    }

    /**
     *
     * @param event The event to count up.
     */
    void countUp(final String event) {

        if (event == null || event.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cant count null or "
                    + "empty event ->" + event + "<-");

        } else if (counts.containsKey(event)) {
            final Integer currentCount = counts.get(event);
            final Integer newCount = Integer.valueOf(currentCount + 1);
            counts.put(event, newCount);
        }
    }

    /**
     * Check if a event is countable.
     *
     * @param  event The event to check.
     *
     * @return true if the event is countable false
     *         otherwise.
     */
    boolean isSupported(final String event) {
        return counts.containsKey(event);
    }

    /**
     * Reset all event counters.
     */
    void reset() {
        maxtime = 1;
        nomatch = 1;
        mindigits = 1;
        maxdigits = 1;
        counts.put(NOMATCH, nomatch);
        counts.put(MAXTIME, maxtime);
        counts.put(MINDIGITS, mindigits);
        counts.put(MAXDIGITS, maxdigits);
    }
}
