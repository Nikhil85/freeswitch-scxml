package org.freeswitch.scxml.actions;
import java.util.Set;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 *
 * @author jocke
 */
public final class InputDigitsAction extends AbstractAction {

    private static final long serialVersionUID = 6771517398770261304L;
    private boolean cleardigits = false;
    private boolean includetermdigit = false;
    private int maxdigits = 16;
    private String maxtime = "30s";
    private String termdigits = "#";
    private String var;
    private int mindigits = 1;
    private String value;
    private String say;

    /**
     * Get the value of say.
     *
     * @return the value of say
     */
    public String getSay() {
        return say;
    }

    /**
     * Set the value of say.
     *
     * @param toSay new value of say
     */
    public void setSay(String toSay) {
        this.say = toSay;
    }

    /**
     * Get the value of value.
     *
     * @return the value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of value.
     *
     * @param playValue new value of value
     */
    public void setValue(String playValue) {
        this.value = playValue;
    }

    /**
     * Get the value of mindigits.
     *
     * @return the value of mindigits
     */
    public int getMindigits() {
        return mindigits;
    }

    /**
     * Set the value of mindigits.
     *
     * @param digits new value of mindigits.
     */
    public void setMindigits(int digits) {
        this.mindigits = digits;
    }


    /**
     * A get method for the cleardigits attribute.
     *
     * @return cleardigits
     */
    public boolean isCleardigits() {
        return cleardigits;
    }

    /**
     *  A set method for the cleardigits attribute.
     *
     * @param clear to clear digit queue.
     */
    public void setCleardigits(boolean clear) {
        this.cleardigits = clear;
    }

    /**
     * A get method for the includetermdigit attribute.
     *
     * @return includetermdigit
     */
    public boolean isIncludetermdigit() {
        return includetermdigit;
    }

    /**
     *  A set method for the includetermdigit attribute.
     *
     * @param include To include the terminator or not.
     */
    public void setIncludetermdigit(boolean include) {
        this.includetermdigit = include;
    }

    /**
     *
     * @return maxdigits
     */
    public int getMaxdigits() {
        return maxdigits;
    }

    /**
     *
     * @param max The number of digits to collect at most.
     */
    public void setMaxdigits(int max) {
        this.maxdigits = max;
    }

    /**
     *
     * @return maxtime
     */
    public String getMaxtime() {
        return maxtime;
    }

    /**
     *
     * @param time The time to collect digits at most.
     */
    public void setMaxtime(String time) {
        this.maxtime = time;
    }

    /**
     *
     * @return termdigits
     */
    public String getTermdigits() {
        return termdigits;
    }

    /**
     *
     * @param digits Digits that should halt the collecting.
     */
    public void setTermdigits(String digits) {
        this.termdigits = digits;
    }

    /**
     *
     * @return The name of the variable that holds the digits.
     */
    public String getVar() {
        return var;
    }

    /**
     *
     * @param varName Set the name of the variable that holds the digits.
     */
    public void setVar(String varName) {
        this.var = varName;
    }

    @Override
    public void handleAction(Session ivrSession, ActionSupport actionSupport) throws HangupException {

        Set<DTMF> terms = DTMF.setFromString(termdigits);

        String file;

        if (value != null) {
            file = actionSupport.getPath(value);

        } else if (say != null) {
            file = "say:" + actionSupport.eval(say);

        } else {
            log.error("Both value and say are null in inputdigits");
            return;
        }

        if (cleardigits) {
            ivrSession.clearDigits();
        }

        EventList evt = ivrSession.read(maxdigits, file, actionSupport.getMillisFromString(maxtime), terms);

        if (evt != null) {

            if (includetermdigit) {
                actionSupport.setContextVar(var, evt.dtmfsAsString());

            } else {
                actionSupport.setContextVar(var, evt.dtmfsAsString(terms));
            }

        }

        if (!actionSupport.proceed(evt)) {
            return;
        }

        if (evt.containsAny(terms)) {

            if (evt.sizeOfDtmfs() <= mindigits) {
                actionSupport.fireEvent(CallXmlEvent.MINDIGITS);

            } else {
                actionSupport.fireEvent(CallXmlEvent.TERMDIGIT);
            }

        } else if (evt.sizeOfDtmfs() >= maxdigits) {
            actionSupport.fireEvent(CallXmlEvent.MAXDIGITS);

        } else if (evt.contains(Event.TIMEOUT)) {
            actionSupport.fireEvent(CallXmlEvent.MAXTIME);

        } else {
            actionSupport.fireEvent(CallXmlEvent.ERROR);
            log.warn("An unknown event happend !!! " + evt);
        }
    }
}
