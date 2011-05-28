package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;

/**
 * Handles the wait element in the callxml 3.0 spec.
 *
 * @see <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=wait.htm">
 *        Callxml
 *      </a>
 *
 * @author jocke
 */
public final class WaitAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 7677124910125336279L;
    private String value;

    /**
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param timeToWait How long to wait.
     */
    public void setValue(String timeToWait) {
        this.value = timeToWait;
    }

    /**
     *
     * @return The value as un integer of time in milliseconds.
     */
    public int getIntValue() {
        return getMillisFromString(value);
    }

    @Override
    public void handleAction(Session ivrSession) throws HangupException {
        ivrSession.sleep(getIntValue());
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(", test=").append(getTest());
        builder.append(", vaue=").append(value);
        builder.append("}");
        return builder.toString();
    }
}
