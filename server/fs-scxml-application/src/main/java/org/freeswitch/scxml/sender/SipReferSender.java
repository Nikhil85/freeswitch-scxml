package org.freeswitch.scxml.sender;

import java.util.Map;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventName;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 *
 * @author jocke
 */
public final class SipReferSender implements Sender {

    private static final Logger LOG
            = LoggerFactory.getLogger(SipReferSender.class);

    private static final String SUPPORT = "sip-refer";

    private SCXMLExecutor executor;

    private Context context;

    @Override
    public String supports() {
        return SUPPORT;
    }

    @Override
    public Sender newInstance() {
        return new SipReferSender();
    }


    @Override
    public void send(String sendId, String target, Map<String, Object> params) {

        Session session =
            (Session) context.get(Session.class.getName());

        Event evt = session.deflect(target);

        if (evt.contains(EventName.CHANNEL_HANGUP)) {

            try {
                executor.triggerEvent(
                          new TriggerEvent(CallXmlEvent.HANGUP.toString()
                        , TriggerEvent.SIGNAL_EVENT));

            } catch (ModelException ex) {
                LOG.error(ex.getMessage());
            }
        }

    }

    @Override
    public void setExecutor(SCXMLExecutor exe) {
        this.executor = exe;
    }

    @Override
    public void setContext(Context ctx) {
        this.context = ctx;
    }

}
