package org.freeswitch.scxml.actions;


import java.util.Set;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 * Handles the getdigits element in the callxml 3.0 spec.
 *
 * @see
 * <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=getdigits.htm">
 *  Callxml
 * </a>
 *
 * @author jocke
 */
public final class GetDigitsAction extends AbstractCallXmlAction {

    private static final long serialVersionUID = -4891858110607406908L;
    private boolean cleardigits = false;
    private boolean includetermdigit = false;
    private int maxdigits = 10;
    private String maxtime = "30s";
    private String termdigits = "#";
    private String var;

    /**
     * A method that transforms a string with a qualifier like
     * 10m 10ms 10s to time in milliseconds.
     *
     * @return The time as a integer.
     */
    public int getMaxTimeAsInt() {
        return getMillisFromString(maxtime);
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
     * @param clear If to clear or not.
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
     * @param include To include terminators or not.
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
     * @param max Number of digits to collect as most.
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
     * @param time Max time to collect.
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
     * @param digits Digits that will stop the collecting.
     */
    public void setTermdigits(String digits) {
        this.termdigits = digits;
    }

    /**
     *
     * @return The name of collected digits.
     */
    public String getVar() {
        return var;
    }

    /**
     *
     * @param varName set the name of collected digits.
     */
    public void setVar(String varName) {
        this.var = varName;
    }

    /**
     *
     * @param ivrSession The session that will execute the actions.
     */
    @Override
    public void handleAction(Session ivrSession) {

        Set<DTMF> terms = DTMF.createCollectionFromString(termdigits);

        if (cleardigits) {
            ivrSession.clearDigits();
        }

        EventList evt = ivrSession.getDigits(
                maxdigits, terms, getMaxTimeAsInt());

        if (evt.containsAny(terms)) {
            fireEvent(CallXmlEvent.TERMDIGIT);

        } else if (evt.sizeOfDtmfs() >= maxdigits) {
            fireEvent(CallXmlEvent.MAXDIGITS);

        } else if (evt.contains(Event.TIMEOUT)) {
            fireEvent(CallXmlEvent.MAXTIME);

        } else {
            fireEvent(CallXmlEvent.ERROR);
            log.warn("An unknown event happend !!!");
        }

        if (includetermdigit) {
            setContextVar(var, evt.dtmfsAsString());

        } else {
            setContextVar(var, evt.dtmfsAsString(terms));
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(", maxtime=" + maxtime);
        builder.append(", termdigits=" + termdigits);
        builder.append(", var=" + var);
        builder.append("}");
        return builder.toString();
    }
}
