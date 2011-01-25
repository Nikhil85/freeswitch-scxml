package com.telmi.msc.scxml.actions;

import com.telmi.msc.freeswitch.FSEventName;
import com.telmi.msc.freeswitch.DTMFMessage;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.scxml.engine.CallXmlEvent;

import java.util.Set;

/**
 * Handles the menu element in the callxml 3.0 spec.
 *
 * @see <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=menu.htm">
 *        Callxml
 *      </a>
 *
 * @author jocke
 */
public final class MenuAction extends AbstractCallXmlAction {

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
     * @return timeInInt
     */
    public int getMaxtimeAsInt() {
        if (maxtime == null) {
            return DEFAULT_MAXTIME;

        } else {
            return getMillisFromString(maxtime);
        }
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
    public void handleAction(FSSession ivrSession) {

        String file;

        if (value != null) {
            file = getPath(value);

        } else if (say != null) {
            file = "say:" + eval(say);

        } else {
            log.error("Both value and say are null in menu");
            return;
        }

        int maxTime = getMaxtimeAsInt();

        Set<DTMFMessage> terms = DTMFMessage.createCollectionFromString(termdigits);

        if (cleardigits) {
            ivrSession.clearDigits();
        }

        FSEvent evt = ivrSession.read(1, file, maxTime, terms);

        if (proceed(evt)) {

            Set<DTMFMessage> dtmfChoices = getChoices();

            if (evt.containsAny(terms)) {
                fireEvent(CallXmlEvent.TERMDIGIT);

            } else if (evt.contains(FSEventName.TIMEOUT)) {
                fireEvent(CallXmlEvent.MAXTIME);

            } else if (evt.sizeOfDtmfs() > 0) {

                DTMFMessage choice = evt.getSingleResult();

                if (dtmfChoices.contains(choice)) {
                    fireChoiceEvent(choice);
                    fireMatchEvent(choice);

                } else {
                    fireEvent(CallXmlEvent.NOMATCH);

                }

            } else {
                fireEvent(CallXmlEvent.ERROR);
            }


        } else {
            log.debug("call ended in menu");
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("say=" + say);
        builder.append(", cleardigits=" + cleardigits);
        builder.append(", maxtime=" + maxtime);
        builder.append(", termdigits=" + termdigits);
        builder.append(", value=" + value);
        builder.append(", choices=" + choices);
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
    private Set<DTMFMessage> getChoices() {

        Set<DTMFMessage> dtmfChoices = null;

        if (choices != null) {
            dtmfChoices = DTMFMessage.createCollectionFromString(choices);

        } else if (choicesexpr != null) {
            dtmfChoices = DTMFMessage.createCollectionFromString(eval(choicesexpr));

        } else {

            fireErrorEvent(CallXmlEvent.ERROR);
            throw new IllegalArgumentException(
                    "A menu with no choices is not a menu");

        }

        return dtmfChoices;
    }
}
