package org.freeswitch.scxml.actions;


import java.util.Set;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
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
public final class GetDigitsAction extends AbstractAction {

    private static final long serialVersionUID = -4891858110607406908L;
    private boolean cleardigits = false;
    private boolean includetermdigit = false;
    private int maxdigits = 10;
    private String maxtime = "30s";
    private String termdigits = "#";
    private String var;

   
    public boolean isCleardigits() {
        return cleardigits;
    }

    public void setCleardigits(boolean clear) {
        this.cleardigits = clear;
    }

    public boolean isIncludetermdigit() {
        return includetermdigit;
    }

    public void setIncludetermdigit(boolean include) {
        this.includetermdigit = include;
    }

    public int getMaxdigits() {
        return maxdigits;
    }

    public void setMaxdigits(int max) {
        this.maxdigits = max;
    }

    public String getMaxtime() {
        return maxtime;
    }

    public void setMaxtime(String time) {
        this.maxtime = time;
    }

    public String getTermdigits() {
        return termdigits;
    }

    public void setTermdigits(String digits) {
        this.termdigits = digits;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String varName) {
        this.var = varName;
    }

    /**
     *
     * @param ivrSession The session that will execute the actions.
     */
    @Override
    public void handleAction(Session ivrSession, ActionSupport actionSupport) throws HangupException {

        Set<DTMF> terms = DTMF.setFromString(termdigits);

        if (cleardigits) {
            ivrSession.clearDigits();
        }

        EventList evt = ivrSession.getDigits(maxdigits, terms, actionSupport.getMillisFromString(maxtime));

        if (evt.containsAny(terms)) {
            actionSupport.fireEvent(CallXmlEvent.TERMDIGIT);

        } else if (evt.sizeOfDtmfs() >= maxdigits) {
            actionSupport.fireEvent(CallXmlEvent.MAXDIGITS);

        } else if (evt.contains(Event.TIMEOUT)) {
            actionSupport.fireEvent(CallXmlEvent.MAXTIME);

        } else {
            actionSupport.fireEvent(CallXmlEvent.ERROR);
            log.warn("An unknown event happend !!!");
        }

        if (includetermdigit) {
            actionSupport.setContextVar(var, evt.dtmfsAsString());

        } else {
            actionSupport.setContextVar(var, evt.dtmfsAsString(terms));
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(", maxtime=").append(maxtime);
        builder.append(", termdigits=").append(termdigits);
        builder.append(", var=").append(var);
        builder.append("}");
        return builder.toString();
    }
}
