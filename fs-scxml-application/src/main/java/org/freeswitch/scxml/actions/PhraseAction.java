package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.Session;



/**
 *
 * @author jocke
 */
public final class PhraseAction extends AbstractAction {

    private static final long serialVersionUID = -2177874537238327910L;
    private String value;
    private String format;
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
    public void handleAction(Session fsSession) {
        proceed(fsSession.say("sv", format, method, eval(value)));
    }
}
