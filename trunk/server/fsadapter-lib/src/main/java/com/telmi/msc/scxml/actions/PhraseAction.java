package com.telmi.msc.scxml.actions;

import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.freeswitch.enums.SayType;

/**
 *
 * @author jocke
 */
public final class PhraseAction extends AbstractCallXmlAction {

    private static final long serialVersionUID = -2177874537238327910L;
    private String value;
    private String format;
    private String termdigits;
    private String method;

    /**
     * Get the value of method.
     *
     * @return the value of method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set the value of method.
     *
     * @param methodToUse new value of method.
     */
    public void setMethod(String methodToUse) {
        this.method = methodToUse;
    }


    /**
     * Get the value of termdigits.
     *
     * @return the value of termdigits.
     */
    public String getTermdigits() {
        return termdigits;
    }

    /**
     * Set the value of termdigits.
     *
     * @param digits new value of termdigits.
     */
    public void setTermdigits(String digits) {
        this.termdigits = digits;
    }



    /**
     * Get the value of format.
     *
     * @return the value of format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the value of format.
     *
     * @param formatToUse new value of format
     */
    public void setFormat(String formatToUse) {
        this.format = formatToUse;
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
     * @param toPhrase new value of value
     */
    public void setValue(String toPhrase) {
        this.value = toPhrase;
    }

    @Override
    public void handleAction(FSSession fsSession) {
        String phrase = eval(value);
        FSEvent evt =  fsSession.say("sv", format, method, phrase);
        proceed(evt);
    }
}
