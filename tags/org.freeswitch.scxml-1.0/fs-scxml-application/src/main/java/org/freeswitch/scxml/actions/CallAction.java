package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.constant.Q850HangupCauses;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class CallAction extends AbstractAction {

    public static final String API_ERR_RESPONSE = "-ERR";
    public static final String API_OK_RESPONSE = "+OK";
    private static final Logger LOG = LoggerFactory.getLogger(CallAction.class);
    private String value;
    private String var;
    private String reasonvar;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getReasonvar() {
        return reasonvar;
    }

    public void setReasonvar(String reasonvar) {
        this.reasonvar = reasonvar;
    }

    @Override
    public void handleAction(Session session, ActionSupport actionSupport) throws HangupException {
        if (notValid(actionSupport)) {
            return;
        }

        EventList call = session.call(actionSupport.eval(value));

        if (!actionSupport.proceed(call)) {
            return;
        } else {
            translateEvent(call.get(Event.API_RESPONSE), actionSupport);
        }

    }

    private void translateEvent(Event evt, ActionSupport actionSupport) {
        
        System.out.println(evt.getBody());
        
        if (evt.getBody().startsWith(API_OK_RESPONSE)) {
            callAnswer(actionSupport, evt);

        } else if (evt.getBody().startsWith(API_ERR_RESPONSE)) {
            callError(evt, actionSupport);

        } else {
            unspecifiedError(actionSupport);
        }
    }

    private void unspecifiedError(ActionSupport actionSupport) {
        actionSupport.setContextVar(reasonvar, Q850HangupCauses.UNSPECIFIED.name().toUpperCase());
        actionSupport.fireErrorEvent(CallXmlEvent.NOANSWER);
    }

    private void callError(Event evt, ActionSupport actionSupport) {
        String cause = evt.getBody().replace(API_ERR_RESPONSE, "").trim();
        actionSupport.setContextVar(reasonvar, cause.toLowerCase());
        if (Q850HangupCauses.USER_BUSY.name().equalsIgnoreCase(cause)) {
            actionSupport.fireEvent(CallXmlEvent.BUSY);
        } else {
            actionSupport.fireEvent(CallXmlEvent.NOANSWER);
        }
    }

    private void callAnswer(ActionSupport actionSupport, Event evt) {
        actionSupport.setContextVar(var, evt.getBody().replace(API_OK_RESPONSE, "").trim());
        actionSupport.fireEvent(CallXmlEvent.ANSWER);
    }

    private boolean notValid(ActionSupport actionSupport) {
        if (!actionSupport.validFields(value, var, reasonvar)) {
            LOG.error("Invalid action {}", this);
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "CallAction{" + "value=" + value + ", var=" + var + '}';
    }
}
