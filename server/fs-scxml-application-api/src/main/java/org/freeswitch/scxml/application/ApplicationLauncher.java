package org.freeswitch.scxml.application;

import org.freeswitch.adapter.Session;


/**
 *  This interface is used for launching
 *  new IVR applications.
 *
 * @author jocke
 */
public interface ApplicationLauncher {

    /**
     * Launch a new IVR application.
     *
     * <p>
     * An implementation of this interface should be expected to be
     * called by multiple threads, hence it it should be thread safe.
     * </p>
     *
     * <p>
     *  When a call comes in a new IvrSession is created
     *  along with a dedicated Thread. An implementation should
     *  start execute the application logic in that thread.
     * </p>
     *
     * @param  session
     *         A session that could be used to execute Ivr applications.
     *
     */
    void launch(Session session);
}
