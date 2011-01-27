package org.freeswitch.scxml.sender;

/**
 *
 * @author jocke
 */
public interface SenderFactory {

    /**
     * Create a new sender based on the target type.
     *
     * @param  targetType The type of the target like basichttp etc.
     *
     * @return A sender ready for handle the action.
     *
     */
    Sender getSender(String targetType);

}
