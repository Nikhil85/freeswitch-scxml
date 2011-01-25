package com.telmi.msc.scxml.engine;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 *
 * @author jocke
 */
public interface ScxmlApplication {

    /**
     * Create a new StateMachine.
     *
     * @param url
     *        A string that points to a well formed SCXML file.
     *
     * @param map
     *        The initial variables that should be put in
     *        the Context ready for evaluation.
     *
     * @throws MalformedURLException
     *         If the URL can't be resolved do a appropriate resource.
     *
     */
    void createAndStartMachine(URL url, Map<String, Object> map)
            throws MalformedURLException;

    /**
     * See if the application currently uses a cache.
     *
     * @return true when using cache false otherwise.
     *
     */
    boolean isCache();

    /**
     *
     * Set if the application should use cache or not.
     *
     * @param use To use cache or not.
     *
     */
    void setCache(boolean use);

    /**
     * Clear the cache from all StateMachines.
     *
     * <p>
     *  Note:
     *  This will have a performance impact on the
     *  application. so use wisely.
     * </p>
     */
    void clearCache();
}
