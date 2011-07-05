package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 *
 * @author jocke
 */
public class BridgeAction extends AbstractAction {
    
    private static final int BRIDGE_COMPLETE_TIME = 2000;

    private String id1;
    private String id2;

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    @Override
    public void handleAction(Session session, ActionSupport actionSupport) throws HangupException {

        if (!actionSupport.validFields(id1, id2)) {
            return;
        }

        EventList el = session.bridge(id1, id2);
        String body = el.get(Event.API_RESPONSE).getBody();
        session.sleep(BRIDGE_COMPLETE_TIME);
        
        if (body.startsWith("+OK")) {
            actionSupport.fireEvent(CallXmlEvent.BRIDGED);
        } else {
            actionSupport.fireEvent(CallXmlEvent.ERROR);
        }
    }
}
