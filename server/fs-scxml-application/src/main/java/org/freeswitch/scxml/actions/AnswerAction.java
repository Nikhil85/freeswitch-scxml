package org.freeswitch.scxml.actions;


import org.freeswitch.adapter.Event;
import org.freeswitch.adapter.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 * Handles the answer element in the callxml 3.0 spec.
 *
 * @see <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=answer.htm">
 *         Callxml
 *      </a>
 *
 * @author jocke
 */
public final class AnswerAction extends AbstractCallXmlAction  {

    private static final long serialVersionUID = 6083371816365155944L;


    @Override
    public void handleAction(Session ivrSession) {

        Event event = ivrSession.answer();

        //If no hangup or error event
        if (proceed(event)) {
            fireEvent(CallXmlEvent.ANSWER);
            setContextVar("isconnected", Boolean.TRUE);
        } else {
            log.debug("Error or hangup in answer {} ", event );

        }

    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("test=" + getTest());
        builder.append("}");
        return builder.toString();
    }

}
