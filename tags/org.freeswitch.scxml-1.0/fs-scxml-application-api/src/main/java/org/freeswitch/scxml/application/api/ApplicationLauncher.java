package org.freeswitch.scxml.application.api;

import org.freeswitch.adapter.api.session.Session;

/**
 * This interface is used for launching new IVR applications.
 * 
 * @author jocke
 */
public interface ApplicationLauncher {

    /**
     * Launch a new IVR application.
     * <p/>
     * <p> An implementation of this interface should be expected to be
     * called by multiple threads, hence it it should be thread safe. </p>
     * <p/>
     * <p> When a call comes in a new IvrSession is created along with a
     * dedicated Thread. An implementation should start execute the
     * application logic in that thread. </p>
     * <p/>
     * @param session A session that could be used to execute Ivr
     * applications.
     * <p/>
     */
    void launch(Session session);
    
    /**
     * Test if this session can be launched by this
     * application launcher. This method should
     * not hijack the session and run it in a different Thread.
     * 
     * @param session 
     *       The session to launch.
     * 
     * @return {@code true} if launchable {@code false } otherwise.
     * 
     */
    boolean isLaunchable(Session session);
}
