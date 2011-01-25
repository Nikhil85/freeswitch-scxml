package com.telmi.msc.scxml.sender;

import java.util.Map;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telmi.msc.freeswitch.FSEventName;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.scxml.engine.CallXmlEvent;

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

        FSSession session =
            (FSSession) context.get(FSSession.class.getName());

        FSEvent evt = session.deflect(target);

        if (evt.contains(FSEventName.CHANNEL_HANGUP)) {

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
