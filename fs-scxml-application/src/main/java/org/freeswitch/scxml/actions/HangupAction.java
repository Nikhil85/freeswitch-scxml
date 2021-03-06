package org.freeswitch.scxml.actions;

/**
 *
 * @author jocke
 */
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;

/**
 * Handles the hangup element in the callxml 3.0 spec.
 *
 * @see <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=hangup.htm">
 *         Callxml
 *      </a>
 *
 * @author jocke
 */
public final class HangupAction extends AbstractAction {

    private static final long serialVersionUID = -6083018410619271886L;

    @Override
    public void handleAction(Session ivrSession, ActionSupport actionSupport) throws HangupException {
        ivrSession.hangup();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("test=").append(getTest());
        builder.append("}");
        return builder.toString();
    }
}
