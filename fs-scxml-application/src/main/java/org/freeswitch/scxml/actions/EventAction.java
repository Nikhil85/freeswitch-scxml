package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.session.Session;

/**
 *
 * @author jocke
 */
public class EventAction extends AbstractAction {
    
    private String name;
    private String uid;
    private String namelist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamelist() {
        return namelist;
    }

    public void setNamelist(String namelist) {
        this.namelist = namelist;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    @Override
    public void handleAction(Session session, ActionSupport actionSupport) throws HangupException {
        session.getEventQueue().fireEvent(new Event(actionSupport.eval(name), actionSupport.getNameListAsMap(namelist)), actionSupport.eval(uid));
    }
    
}
