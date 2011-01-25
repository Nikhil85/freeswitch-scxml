package com.telmi.msc.scxml.sender;

import java.util.Map;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;

/**
 *
 * @author jocke
 */
public interface Sender {

    /**
     * An implementation should return
     * the target type it supports.
     *
     * ex. basichttp
     *
     * @return The target type.
     */
    String supports();

    /**
     * An sender implementation should create a new instance
     *  and return that one. But if the sender is handled in a
     *  synchronised way it may return the same instance always.
     *  Note that it will then be up to the sender it self to make sure
     *  it does not run in to concurrent problems.
     *
     * @return a sender not always new.
     */
    Sender newInstance();


    /**
     *
     * Handle the send action.
     *
     * @param sendId A id only usefull if send supports cancel.
     * @param target Where to send.
     * @param params What to send.
     */
    void send(String sendId, String target, Map<String, Object> params);

    /**
     * Get access to the executor to fire events.
     *
     * @param executor The executor.
     */
    void setExecutor(SCXMLExecutor executor);

    /**
     * Get access to the context.
     *
     * @param ctx Holds variables.
     */
    void setContext(Context ctx);
}
