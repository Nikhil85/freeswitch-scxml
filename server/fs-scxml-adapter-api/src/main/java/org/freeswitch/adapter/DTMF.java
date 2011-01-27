package org.freeswitch.adapter;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jocke
 */
public enum DTMF {

    /** The digit Zero in the telephone numpad. */
    ZERO('0'),

    /** The digit one in the telephone numpad. */
    ONE('1'),

    /** The digit two in the telephone numpad. */
    TWO('2'),

    /** The digit three in the telephone numpad. */
    THREE('3'),

    /** The digit four in the telephone numpad. */
    FOUR('4'),

    /** The digit five in the telephone numpad. */
    FIVE('5'),

    /** The digit six in the telephone numpad. */
    SIX('6'),

    /** The digit seven in the telephone numpad. */
    SEVEN('7'),

    /** The digit eight in the telephone numpad. */
    EIGHT('8'),

    /** The digit nine in the telephone numpad. */
    NINE('9'),

    /** The digit pound in the telephone numpad. */
    POUND('#'),

    /** The digit star in the telephone numpad. */
    STAR('*');

    /**
     * The value as an char.
     */
    private char charValue;

    /**
     * A cache for foster lookup.
     */
    private static final Map<Character, DTMF> CACHE =
            new HashMap<Character, DTMF>();

    static {

        EnumSet<DTMF> dtmfs = EnumSet.allOf(DTMF.class);

        for (DTMF d : dtmfs) {
            CACHE.put(d.charValue, d);
        }
    }

    /**
     * Create a new DTMF with a char ant int value.
     *
     * @param charV  The char value.
     *
     **/
    private DTMF(final char charV) {
        this.charValue = charV;
    }

    /**
     * Turn a String to a Set of DTMF enums.
     * Chars that are not a Enum will be ignored.
     * This is useful if you do not wish to have duplicates
     * in the Collection.
     *
     * @param toDtmf The String to transform.
     *
     * @return A Set with DTMFs that was found in the String.
     *
     */
    public static Set<DTMF> createCollectionFromString(String toDtmf) {

        if (toDtmf == null) {
            throw new IllegalArgumentException(
                    "Will not turn null "
                  + "String to DTMF Set");
        }

        Set<DTMF> dtmfs = EnumSet.noneOf(DTMF.class);
        fillCollection(toDtmf, dtmfs);
        return dtmfs;
    }

//    /**
//     * Turn a String to a List of DTMF enums.
//     * Chars that are not a Enum will be ignored.
//     * This is useful if you accept duplicates in the Collection
//     * like phone numbers.
//     *
//     * @param toDtmf The String to transform.
//     *
//     * @return A Set with DTMFs that was found in the String.
//     *
//     */
//    public static List<DTMFMessage> listFromString(String toDtmf) {
//
//        if (toDtmf == null) {
//            throw new IllegalArgumentException(
//                    "Will not turn null "
//                  + "String to DTMF List");
//        }
//
//        List<DTMFMessage> dtmfs = new ArrayList<DTMFMessage>();
//
//        fillCollection(toDtmf, dtmfs);
//
//        return dtmfs;
//    }

    /**
     *
     * @return A String representation of this DTMF.
     */
    @Override
    public String toString() {
        return String.valueOf(charValue);
    }

    /**
     * Get an DTMF based on the DTMF Char
     * representation.
     *
     * @param  charValue The char to turn to DTMF.
     *
     * @return The DTMF that the Char matches or null
     *         if no match was found.
     */
    public static final DTMF valueOfChar(final char charValue) {
        return CACHE.get(charValue);
    }

    /**
     *
     * Fill a collection with DTMF digits from a String.
     *
     * @param toDtmf The String with DTMF chars.
     * @param dtmfs  The collection to fill.
     */
    private static void fillCollection(String toDtmf, Collection<DTMF> dtmfs) {
        for (int i = 0; i < toDtmf.length(); i++) {
            char toFind = toDtmf.charAt(i);
            if (CACHE.containsKey(toFind)) {
                dtmfs.add(valueOfChar(toFind));
            }
        }
    }
}
