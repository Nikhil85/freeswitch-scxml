package org.freeswitch.scxml.actions;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.scxml.model.Action;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 *
 * @author jocke
 */
public abstract class AbstractAction extends Action {

    private static final long serialVersionUID = -1141223857669398177L;
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private String test;

    
    /**
     * Get the value of test.
     *
     * @return the value of test
     */
    public final String getTest() {
        return test;
    }

    /**
     * Set the value of test.
     *
     * @param toTest new value of test
     */
    public final void setTest(String toTest) {
        this.test = toTest;
    }

 

    /**
     * Handle all pre actions here like tell the ivrSession to execute some
     * events.
     *
     * @param sSession A session to use for executing IVR commands.
     *
     */
    public abstract void handleAction(Session session, ActionSupport actionSupport) throws HangupException;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(
            EventDispatcher evtDispatcher,
            ErrorReporter errRep,
            SCInstance scInstance,
            Log appLog,
            Collection derivedEvents)
            throws ModelException, SCXMLExpressionException {
        
        ActionSupport ac = new ActionSupport(derivedEvents,  scInstance.getRootContext(), scInstance.getEvaluator());
 
        if (ac.actionShouldExecute(test)) {
            
            Session fss = (Session) scInstance.getRootContext().get(Session.class.getName());

            if (fss.isAlive()) {
                try {
                    handleAction(fss, ac);
                } catch (HangupException ex) {
                   ac.fireErrorEvent(CallXmlEvent.HANGUP);    
                }

            } else {
                ac.fireErrorEvent(CallXmlEvent.HANGUP);
            }

        } else {
            log.debug("rejected action because test evaluated to false ");
        }
    }

}
