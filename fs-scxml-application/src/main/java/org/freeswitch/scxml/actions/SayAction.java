package org.freeswitch.scxml.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.scxml.model.ExternalContent;
import org.freeswitch.adapter.api.HangupException;
import org.w3c.dom.Text;

import org.freeswitch.adapter.api.session.Session;

/**
 * Handles the say element in the callxml 3.0 spec.
 *
 * @see <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=say.htm">
 *      Callxml
 *      </a>
 *
 * @author jocke
 */
public final class SayAction extends AbstractAction implements ExternalContent {

    private static final long serialVersionUID = -4993804632786018147L;
    
    private List<Text> externalNodes;

    /**
     * Create a new instance of SayAction.
     */
    public SayAction() {
        super();
        this.externalNodes = new ArrayList<>();
    }

    /**
     *
     * @return externalNodes
     */
    @Override
    public List<Text> getExternalNodes() {
        return externalNodes;
    }

    /**
     *
     * @param nodes Xml text nodes.
     */
    public void setExternalNodes(final List<Text> nodes) {
        this.externalNodes = nodes;
    }


    @Override
    public void handleAction(Session ivrSession, ActionSupport actionSupport) throws HangupException {

        if (externalNodes.isEmpty()) {

            log.warn("Can't speak empty words");

        } else {

            Text text = externalNodes.get(0);
            String toSay = actionSupport.eval(text.getWholeText().trim());
            ivrSession.speak(toSay);
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(", test=").append(getTest());
        builder.append("}");
        return builder.toString();
    }
}

