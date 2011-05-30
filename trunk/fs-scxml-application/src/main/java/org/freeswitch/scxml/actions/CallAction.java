package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class CallAction extends AbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(CallAction.class);
    private String value;
    private String url;
    private String uid;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    
    @Override
    public void handleAction(Session session, ActionSupport actionSupport) throws HangupException {

        LOG.warn("Hello I will make call");

        if (notValid(actionSupport)) {
            return;
        }
        
        String call = session.call(actionSupport.eval(value));
        actionSupport.setContextVar(uid, call);
    }

    private boolean notValid(ActionSupport actionSupport) {
        if (!actionSupport.validateFields(value)) {
            LOG.error("Invalid action {}", this);
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "CallAction{" + "value=" + value + ", url=" + url + '}';
    }

}
