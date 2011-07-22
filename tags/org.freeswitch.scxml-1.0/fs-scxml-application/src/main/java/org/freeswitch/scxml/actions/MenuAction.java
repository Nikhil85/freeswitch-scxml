package org.freeswitch.scxml.actions;

import java.util.Set;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 * Handles the menu element in the callxml 3.0 spec.
 *
 * @see <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=menu.htm">
 *        Callxml
 *      </a>
 *
 * @author jocke
 */
public final class MenuAction extends AbstractAction {

    private static final int DEFAULT_MAXTIME = 30000;
    private static final long serialVersionUID = 357676526855898765L;
    private boolean cleardigits = true;
    private String maxtime = "30s";
    private String say = null;
    private String termdigits = "#";
    private String value;
    private String choices;
    private String choicesexpr;

    /**
     *
     * @param theChoices expected digits to be pressed.
     */
    public void setChoices(String theChoices) {
        this.choices = theChoices;

    }

    /**
     *
     * @return cleardigits
     */
    public boolean isCleardigits() {
        return cleardigits;
    }

    /**
     *
     * @param clear To empty the digit queue or not.
     */
    public void setCleardigits(boolean clear) {
        this.cleardigits = clear;
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
     * @param time Max time to wait.
     */
    public void setMaxtime(String time) {
        this.maxtime = time;
    }


    /**
     *
     * @return say
     */
    public String getSay() {
        return say;
    }

    /**
     *
     * @param toSay To say.
     */
    public void setSay(String toSay) {
        this.say = toSay;
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
     * @param digits Digits that should stop the collecting of digits.
     */
    public void setTermdigits(String digits) {
        this.termdigits = digits;
    }

    /**
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param prompt A prompt to play.
     */
    public void setValue(String prompt) {
        this.value = prompt;
    }

    /**
     * Get the value of choicesexpr.
     *
     * @return the value of choicesexpr
     */
    public String getChoicesexpr() {
        return choicesexpr;
    }

    /**
     * Set the value of choicesexpr.
     *
     * @param expr new value of choicesexpr
     */
    public void setChoicesexpr(String expr) {
        this.choicesexpr = expr;
    }

    @Override
    public void handleAction(Session ivrSession, ActionSupport actionSupport) throws HangupException {

        String file;

        if (value != null) {
            file = actionSupport.getPath(value);

        } else if (say != null) {
            file = "say:" + actionSupport.eval(say);

        } else {
            log.error("Both value and say are null in menu");
            return;
        }

        int maxTime = actionSupport.getMillisFromString(maxtime);

        Set<DTMF> terms = DTMF.setFromString(termdigits);

        if (cleardigits) {
            ivrSession.clearDigits();
        }

        EventList evt = ivrSession.read(1, file, maxTime, terms);

        if (actionSupport.proceed(evt)) {

            Set<DTMF> dtmfChoices = getChoices(actionSupport);

            if (evt.containsAny(terms)) {
                actionSupport.fireEvent(CallXmlEvent.TERMDIGIT);

            } else if (evt.contains(Event.TIMEOUT)) {
                actionSupport.fireEvent(CallXmlEvent.MAXTIME);

            } else if (evt.sizeOfDtmfs() > 0) {

                DTMF choice = evt.getSingleResult();

                if (dtmfChoices.contains(choice)) {
                    actionSupport.fireChoiceEvent(choice);
                    actionSupport.fireMatchEvent(choice);

                } else {
                    actionSupport.fireEvent(CallXmlEvent.NOMATCH);

                }

            } else {
                actionSupport.fireEvent(CallXmlEvent.ERROR);
            }


        } else {
            log.debug("call ended in menu");
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("say=").append(say);
        builder.append(", cleardigits=").append(cleardigits);
        builder.append(", maxtime=").append(maxtime);
        builder.append(", termdigits=").append(termdigits);
        builder.append(", value=").append(value);
        builder.append(", choices=").append(choices);
        builder.append("}");
        return builder.toString();
    }

    /**
     * Get a Set of choices.
     *
     *  If both choices and choicesexpr is null. A IllegalArgumentException
     *  will be thrown.
     *
     * @return DTMF choices from the choices field, if null choices
     *         from the choicesexpr field.
     */
    private Set<DTMF> getChoices(ActionSupport actionSupport) {

        Set<DTMF> dtmfChoices = null;

        if (choices != null) {
            dtmfChoices = DTMF.setFromString(choices);

        } else if (choicesexpr != null) {
            dtmfChoices = DTMF.setFromString(actionSupport.eval(choicesexpr));

        } else {
            actionSupport.fireErrorEvent(CallXmlEvent.ERROR);
            throw new IllegalArgumentException("A menu with no choices is not a menu");
        }

        return dtmfChoices;
    }
}
