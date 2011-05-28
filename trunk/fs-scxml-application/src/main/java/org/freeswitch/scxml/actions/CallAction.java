package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventQueueListener;
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

    @Override
    public void handleAction(Session session) throws HangupException {

        LOG.warn("Hello I will make call");

        if (notValid()) {
            return;
        }

        session.call(eval(value), new EventListener());

    }

    private boolean notValid() {
        if (!validateFields(value)) {
            LOG.error("Invalid action {}", this);
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "CallAction{" + "value=" + value + ", url=" + url + '}';
    }

    private class EventListener implements EventQueueListener {

        @Override
        public void onAdd(Event event) {
            System.out.println(event);
        }
    }
}
