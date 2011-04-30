package org.freeswitch.scxml.actions;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.apache.commons.scxml.model.Action;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 *
 * @author jocke
 */
public abstract class AbstractAction extends Action {

    private static final long serialVersionUID = -1141223857669398177L;
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private String test;
    private ActionSupport actionSupport;

    void setActionSupport(ActionSupport actionSupport) {
        this.actionSupport = actionSupport;
    }
    
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
     * Fire an event on the StateMachine.
     *
     * @param event The event to fire.
     */
    public final void fireEvent(CallXmlEvent event) {
      actionSupport.fireEvent(event);
    }

    /**
     * Fire a choice event on the StateMachine.
     *
     * @param dtmf The DTMF choice to fire
     *
     */
    public final void fireChoiceEvent(DTMF dtmf) {
      actionSupport.fireChoiceEvent(dtmf);
    }

    /**
     * Fire an match on the StateMachine.
     *
     * @param dTMF The matching DTMF.
     */
    public final void fireMatchEvent(DTMF dTMF) {
      actionSupport.fireMatchEvent(dTMF);
    }

    /**
     * Fire an error event on the StateMachine.
     *
     * @param event The event that occurred.
     */
    public final void fireErrorEvent(CallXmlEvent event) {
      actionSupport.fireErrorEvent(event);
    }

    /**
     * Fire an error event on the StateMachine.
     *
     * @param event The event that occurred.
     */
    public final void fireEvent(String event) {
       actionSupport.fireEvent(event);
    }

    /**
     * An method to check if the last call to the ivrSession
     * came back with hangup or error.
     *
     * @param event The event to validate.
     *
     * @return true If the action should proceed false otherwise.
     */
    public boolean proceed(EventList evtl) {
      return actionSupport.proceed(evtl);
    }

    /**
     * Handle all pre actions here like tell the ivrSession to execute some
     * events.
     *
     * @param sSession A session to use for executing IVR commands.
     *
     */
    public abstract void handleAction(Session sSession) throws HangupException;

    /**
     * Validate fields.
     *
     * <p>
     *   A field will be seen as invalid if it
     *  is null or empty. An error event will be
     *  fired on the StateMachine if such field is
     *  found.
     * </p>
     *
     * @param fields The fields to validate.
     *
     * @return <code>true</code> if a all fields are valid
     *         <code>false</code> otherwise.
     */
    protected final boolean validateFields(String... fields) {
     return actionSupport.validateFields(fields);
    }

    /**
     * Add a variable to the root context.
     *
     * @param name The name of the variable.
     * @param var  The value of the variable.
     */
    public final void setContextVar(String name, Object var) {
        actionSupport.setContextVar(name, var);
    }

    /**
     * Get a variable from the context.
     *
     * @param name of variable
     *
     * @return Object found or null.
     */
    public Object getContextVar(String name) {
       return actionSupport.getContextVar(name);
    }

    /**
     * Evaluate a variable.
     *
     * @param toEval an variable name to evaluate.
     *
     * @return The evaluated value or the value that
     *         was given as an argument, if evaluation fails.
     */
    public final String eval(final String toEval) {
        return actionSupport.eval(toEval);
    }

    /**
     * Get variables names and values as an map.
     *
     * <p>
     *   The <code>vars</code> argument will
     *   be split between spaces. The variable
     *   name will be added to the map with it's
     *   evaluated value.
     * </P>
     *
     * @param vars Tokens separated with one space or more with variable
     *             names.
     *
     * @return All variables that was fond in the root context,
     *         or an empty map.
     */
    public final Map<String, Object> getNameListAsMap(String vars) {
      return actionSupport.getNameListAsMap(vars);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(
            EventDispatcher evtDispatcher,
            ErrorReporter errRep,
            SCInstance scInstance,
            Log appLog,
            Collection derivedEvents)
            throws ModelException, SCXMLExpressionException {

        setActionSupport(new ActionSupport(derivedEvents,  scInstance.getRootContext(), scInstance.getEvaluator()));
        
        if (actionShouldExecute()) {
            Session fss = (Session) scInstance.getRootContext().get(Session.class.getName());

            if (fss.isAlive()) {
                try {
                    handleAction(fss);
                } catch (HangupException ex) {
                   fireErrorEvent(CallXmlEvent.HANGUP);    
                }

            } else {
                fireErrorEvent(CallXmlEvent.HANGUP);
            }

        } else {
            log.debug("rejected action because test evaluated to false ");
        }
    }

    /**
     * Test if an action should execute.
     *
     * @return true if the action should false otherwise.
     */
    private boolean actionShouldExecute() {
        return actionSupport.actionShouldExecute(test);
    }

    /**
     * Get the path relative to the path
     * of where the SCXML document is executed from.
     *
     * @param expr Expression that should be evaluated to
     *             a path.
     * @return The path.
     */
    protected String getPath(String expr) {
        return actionSupport.getPath(expr);
    }

    /**
     * A method that translates a string expression to time in milliseconds.
     * The string should begin with a numeric value. The valid qualifiers are:
     *
     * m: minutes
     * s: seconds
     * ms: milliseconds
     *
     * ex.
     *
     * 10m will evaluate to 60 000 milliseconds.
     *
     * It will read all the chars in the string and if they
     * will be considered to be apart of the numeric value, otherwise they will
     * simply get ignored.
     *
     * @param  time The String that should be transformed.
     * @return Time in milliseconds.
     * @throws NumberFormatException
     *         If the string time could not be translated in to milliseconds.
     *
     **/
    public int getMillisFromString(String time) throws NumberFormatException {
        return actionSupport.getMillisFromString(time);
    }
}
