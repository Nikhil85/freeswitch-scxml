package com.telmi.msc.scxml.engine;

import java.util.List;
import java.util.Map;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCXMLExecutor;

import com.telmi.msc.scxml.sender.Sender;
import com.telmi.msc.scxml.sender.SenderFactory;

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

    private final SenderFactory factory;
    private final Context context;
    private SCXMLExecutor executor;
    private static final Logger LOG =
            LoggerFactory.getLogger(ScxmlEventDispatcher.class);

    /**
     * Create a new instance of ScxmlEventDispatcher.
     *
     * @param senderFactory Create senders.
     * @param ctx The context with variables.
     */
    ScxmlEventDispatcher(SenderFactory senderFactory, Context ctx) {
        this.factory = senderFactory;
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
