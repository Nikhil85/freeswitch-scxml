package com.telmi.msc.scxml.sender;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author jocke
 */
@Singleton
public final class SenderFactoryImpl implements SenderFactory {

    private static final ConcurrentHashMap<String, Sender> CACHE =
            new ConcurrentHashMap<String, Sender>();

    /**
     * Create a new sender factory with all senders.
     *
     * @param senders Supported senders for this application.
     */
    @Inject
    public SenderFactoryImpl(Set<Sender> senders) {

        for (Sender sender : senders) {
            CACHE.put(sender.supports(), sender);
        }
    }

    @Override
    public Sender getSender(String targetType) {

        Sender sender = CACHE.get(targetType);

        if (sender == null) {
            return null;

        } else {
            return sender.newInstance();

        }
    }
}
