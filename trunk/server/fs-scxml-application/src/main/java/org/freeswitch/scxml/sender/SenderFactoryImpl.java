package org.freeswitch.scxml.sender;

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

        Sender sender = null ;//CACHE.get(targetType); TODO use Lookup*/

        if (sender == null) {
            return null;

        } else {
            return sender.newInstance();

        }
    }
}
