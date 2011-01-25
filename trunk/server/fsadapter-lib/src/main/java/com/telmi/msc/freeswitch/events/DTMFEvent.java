/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telmi.msc.freeswitch.events;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://wiki.freeswitch.org/wiki/Event_List#DTMF
 *
 * DTMF-Digit: 1
 * DTMF-Duration: 2000
 * Event-Name: DTMF
 *
 * @author kristofer
 */
public class DTMFEvent extends AbstractEvent {
    private static final Pattern DIGIT_PATTERN = Pattern.compile("(DTMF-Digit:)(\\s)(.*)", Pattern.MULTILINE);
    private static final Pattern DURATION_PATTERN = Pattern.compile("(DTMF-Duration:)(\\s)(\\d*)", Pattern.MULTILINE);
    private static final Set validDigits = new HashSet();
    private final char digit;
    private final long duration;

    static {
        validDigits.add('0');
        validDigits.add('1');
        validDigits.add('2');
        validDigits.add('3');
        validDigits.add('4');
        validDigits.add('5');
        validDigits.add('6');
        validDigits.add('7');
        validDigits.add('8');
        validDigits.add('9');
        validDigits.add('#');
        validDigits.add('*');
        validDigits.add('A');
        validDigits.add('B');
        validDigits.add('C');
        validDigits.add('D');
    }

    public DTMFEvent(final String name, final String data) throws NoSuchEventException {
        super(name, data);
        Matcher matcher = null;
        String tmp = null;

        matcher = DIGIT_PATTERN.matcher(data);
        if (!matcher.find()) {
            throw new NoSuchEventException("Did not find DTMF-Digit in event\n" + data);
        }

        try {
            tmp = matcher.group(3).toUpperCase();
            if ("%23".equals(tmp)) {
                tmp = "#";
            }
        } catch (IllegalStateException ex) {
            throw new NoSuchEventException("Could not match any DTMF-Digit in event " + matcher.group(), ex);
        } catch (IndexOutOfBoundsException ex) {
            throw new NoSuchEventException("Could not match any DTMF-Digit in event " + matcher.group(), ex);
        }
        this.digit = tmp.charAt(0);

        if (!validDigits.contains(this.digit)) {
            throw new NoSuchEventException("DTMF digit is not valid " + this.digit);
        }


        matcher = DURATION_PATTERN.matcher(data);
        tmp = null;
        if (!matcher.find()) {
            throw new NoSuchEventException("Did not find DTMF-Duration in event\n" + data);
        }

        try {
            tmp = matcher.group(3);
        } catch (IllegalStateException ex) {
            throw new NoSuchEventException("Could not match any DTMF-Duration in event. " + matcher.group(), ex);
        } catch (IndexOutOfBoundsException ex) {
            throw new NoSuchEventException("Could not match any DTMF-Duration in event. " + matcher.group(), ex);
        }

        try {
            this.duration = Long.parseLong(tmp);
        }catch (NumberFormatException  ex) {
            throw new NoSuchEventException(ex);
        }
    }

    public char digit() {
        return digit;
    }

    public long duration() {
        return duration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append("{");
        sb.append(digit).append(" ").append(duration).append("}");
        return sb.toString();
    }
}
