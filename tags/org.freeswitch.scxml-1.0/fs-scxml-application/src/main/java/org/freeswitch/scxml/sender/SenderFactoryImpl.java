package org.freeswitch.scxml.sender;

import java.util.Collection;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public final class SenderFactoryImpl implements SenderFactory {

    /**
     * Create a new sender factory with all senders.
     *
     * @param senders Supported senders for this application.
     */
    public SenderFactoryImpl() {
    }

    @Override
    public Sender getSender(String targetType) {

        Collection<? extends Sender> allSenders = Lookup.getDefault().lookupAll(Sender.class);

        for (Sender sender : allSenders) {
            
            if (sender.supports().equals(targetType)) {
                return sender.newInstance();
            }
            
        }

        return null;

    }
}
