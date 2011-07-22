package org.freeswitch.scxml.engine;

import java.util.List;
import java.util.Map;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCXMLExecutor;
import org.freeswitch.scxml.sender.Sender;
import org.freeswitch.scxml.sender.SenderFactory;
import org.openide.util.Lookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 *
 * Dispatches Send requests to senders.
 *
 */
final class ScxmlEventDispatcher implements EventDispatcher {

    private final Context context;
    private SCXMLExecutor executor;
    private static final Logger LOG = LoggerFactory.getLogger(ScxmlEventDispatcher.class);

    /**
     * Create a new instance of ScxmlEventDispatcher.
     *
     * @param senderFactory Create senders.
     * @param ctx The context with variables.
     */
    ScxmlEventDispatcher(Context ctx) {
        this.context = ctx;
    }

    /**
     * Set the executor.
     *
     * @param exe The executor.
     */
    void setExecutor(SCXMLExecutor exe) {
        this.executor = exe;
    }

    @Override
    public void cancel(String sendId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void send(String sendId,
            String target,
            String targetType,
            String event,
            Map params,
            Object hints,
            long delay,
            List externalNodes) {

        SenderFactory factory = Lookup.getDefault().lookup(SenderFactory.class);

        if(factory == null) {
            LOG.warn("No sender factory found unable to send ");
            return;
        } 
        
        Sender sender = factory.getSender(targetType);
       
        if (sender == null) {
            LOG.error("Target type '{}' is not supported ", targetType);

        } else {
            sender.setExecutor(executor);
            sender.setContext(context);
            sender.send(sendId, target, params);
        }
    }
}
