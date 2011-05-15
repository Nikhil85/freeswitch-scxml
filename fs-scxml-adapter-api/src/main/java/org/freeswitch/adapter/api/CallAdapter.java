package org.freeswitch.adapter.api;

/**
 *
 * @author jocke
 */
public interface CallAdapter {
    public void originate(String dialString);
    public void bridge(String uuid1, String uuid2);
}
