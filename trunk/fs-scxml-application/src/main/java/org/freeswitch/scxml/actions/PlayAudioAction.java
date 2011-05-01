package org.freeswitch.scxml.actions;

import java.util.Set;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 * Handles the playaudio element in the callxml 3.0 spec.
 *
 * @see
 *    <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=playaudio.htm">
 *      Callxml
 *    </a>
 *
 * @author jocke
 */
public final class PlayAudioAction extends AbstractAction {

    private static final long serialVersionUID = 6273133241974401037L;
    private String value;
    private String termdigits = "#";

    /**
     * Get digits that should stop audio.
     *
     * @return The digits.
     */
    public String getTermdigits() {
        return termdigits;
    }

    /**
     *
     * @param digits Set digits that should stop audio
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
     * @param prompt The prompt to play.
     */
    public void setValue(String prompt) {
        this.value = prompt;
    }

    @Override
    public void handleAction(Session ivrSession) throws HangupException {

        String prompt = getPath(value);

        if (prompt == null) {
            fireEvent(CallXmlEvent.ERROR);
            return;
        }

        EventList event = null;

        if (termdigits == null || termdigits.isEmpty()) {

            event = ivrSession.streamFile(prompt);
            proceed(event);

        } else {

            Set<DTMF> terms = DTMF.setFromString(termdigits);

            event = ivrSession.streamFile(prompt, terms);

            if (proceed(event)) {

                if (event.containsAny(terms)) {
                    fireEvent(CallXmlEvent.TERMDIGIT);
                }

            } else {
                log.debug("Call ended in play audio ");

            }

        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(", value=").append(value);
        builder.append("}");
        return builder.toString();
    }
}

