package org.freeswitch.scxml.engine;

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
    void createAndStartMachine(URL url, Map<String, Object> map) throws MalformedURLException;

}
