package org.freeswitch.scxml.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.scxml.model.ExternalContent;
import org.w3c.dom.Text;

import org.freeswitch.adapter.api.Session;

/**
 * Handles the say element in the callxml 3.0 spec.
 *
 * @see <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=say.htm">
 *      Callxml
 *      </a>
 *
 * @author jocke
 */
public final class SayAction
        extends AbstractCallXmlAction
        implements ExternalContent {

    private static final long serialVersionUID = -4993804632786018147L;
    private String voice;
    private List<Text> externalNodes;

    /**
     * Create a new instance of SayAction.
     */
    public SayAction() {
        super();
        this.externalNodes = new ArrayList<Text>();
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

    /**
     *
     * @return voice
     */
    public String getVoice() {
        return voice;
    }

    /**
     *
     * @param toUse The voice to use.
     */
    public void setVoice(String toUse) {
        this.voice = toUse;
    }

    @Override
    public void handleAction(Session ivrSession) {

        if (externalNodes.isEmpty()) {

            log.warn("Can't speak empty words");

        } else {

            Text text = externalNodes.get(0);
            String toSay = eval(text.getWholeText().trim());
            ivrSession.speak(toSay);
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(", voice=" + voice);
        builder.append(", test=" + getTest());
        builder.append("}");
        return builder.toString();
    }
}

