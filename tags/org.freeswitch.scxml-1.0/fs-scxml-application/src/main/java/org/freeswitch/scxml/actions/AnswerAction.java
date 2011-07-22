package org.freeswitch.scxml.actions;


import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
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
public final class AnswerAction extends AbstractAction  {

    private static final long serialVersionUID = 6083371816365155944L;


    @Override
    public void handleAction(Session ivrSession, ActionSupport actionSupport) throws HangupException {

        EventList event = ivrSession.answer();

        //If no hangup or error event
        if (actionSupport.proceed(event)) {
            actionSupport.fireEvent(CallXmlEvent.ANSWER);
            actionSupport.setContextVar("isconnected", Boolean.TRUE);
        
        } else {
            actionSupport.setContextVar("isconnected", Boolean.FALSE);
            log.debug("Error or hangup in answer {} ", event );
        }

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
