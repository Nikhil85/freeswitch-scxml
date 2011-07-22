package org.freeswitch.scxml.engine;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.scxml.Context;

/**
 * @author jocke
 */
public final class Count extends Number {

    private static final long serialVersionUID = 1L;
    private final Map<String, Integer> counts = new HashMap<>();
    private Context context;

    public Count(Context context) {
        this.context = context;
    }

    void countUp(final String event) {
        validateEvent(event);
        if (counts.containsKey(event)) {
            increment(event);
        } else {
            add(event);
        }
    }

    private void add(final String event) {
        counts.put(event, Integer.valueOf(1));
    }

    private void increment(final String event) {
        counts.put(event, Integer.valueOf(counts.get(event) + 1));
    }

    private void validateEvent(final String event) throws IllegalStateException, IllegalArgumentException {

        if (event == null || event.isEmpty()) {
            throw new IllegalArgumentException("Can't count null or empty event ->" + event + "<-");
        }
    }

    void reset() {
        counts.clear();
    }

    @Override
    public int intValue() {
        return getValue();
    }

    @Override
    public long longValue() {
        return getValue();
    }

    @Override
    public float floatValue() {
        return getValue();
    }

    @Override
    public double doubleValue() {
        return getValue();
    }

    public int getValue() {
        String evt = (String) context.get(ScxmlSemanticsImpl.CURRENT_EVENT_EVALUATED);

        if (counts.containsKey(evt)) {
            return counts.get(evt);

        } else if (evt != null) {
            counts.put(evt, 1);
            return counts.get(evt);
        }

        return 1;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;

        } else if (obj instanceof Number) {

            return ((Number) obj).intValue() == getValue();

        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return getValue();
    }

    @Override
    public String toString() {
        return "Count{" + "counts=" + counts + '}';
    }
}
