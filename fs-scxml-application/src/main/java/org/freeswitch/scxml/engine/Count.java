package org.freeswitch.scxml.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.scxml.Context;

/**
 * @author jocke
 */
public final class Count extends Number {

    private final Map<String, Integer> counts = new HashMap<String, Integer>();
    private Context context;

    public Count(Context context) {
        this.context = context;
    }

    public int getMax() {
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

    void countUp(final String event) {
        validateEvent(event);
        if(counts.containsKey(event)) {
           counts.put(event, Integer.valueOf(counts.get(event) + 1));
        
        } else {
           counts.put(event, Integer.valueOf(1));
        }
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

    
    @Override
    public boolean equals(Object obj) {
            
        if (obj == null) {
            return false;
        
        } else if(obj instanceof Number) {
            
            return ((Number)obj).intValue() == getValue();
        
        } else {
            return false;
        }
        
    }

    @Override
    public int hashCode() {
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
    public String toString() {
        return "Count{" + "counts=" + counts + '}';
    }
}
