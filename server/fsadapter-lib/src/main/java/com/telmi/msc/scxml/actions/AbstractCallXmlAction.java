package com.telmi.msc.scxml.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.model.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telmi.msc.freeswitch.FSEventName;
import com.telmi.msc.freeswitch.DTMFMessage;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.scxml.engine.CallXmlEvent;

import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.model.Action;

/**
 *
 * @author jocke
 */
public abstract class AbstractCallXmlAction extends Action {

    private static final long serialVersionUID = -1141223857669398177L;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private String test;
//    SCInstance scInstance;

    Collection<TriggerEvent> derivedEvents;

    Context ctx;

    Evaluator evaluator;

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

        derivedEvents.add(new TriggerEvent(
                event.toString(),
                TriggerEvent.SIGNAL_EVENT));
    }

    /**
     * Fire a choice event on the StateMachine.
     *
     * @param dtmf The DTMF choice to fire
     *
     */
    public final void fireChoiceEvent(DTMFMessage dtmf) {
        derivedEvents.add(
                new TriggerEvent("choice:" + dtmf.toString(),
                TriggerEvent.SIGNAL_EVENT));
    }

    /**
     * Fire an match on the StateMachine.
     *
     * @param dTMF The matching DTMF.
     */
    public final void fireMatchEvent(DTMFMessage dTMF) {
        derivedEvents.add(new TriggerEvent(
                CallXmlEvent.MATCH.toString(),
                TriggerEvent.SIGNAL_EVENT,
                dTMF.toString()));
    }

    /**
     * Fire an error event on the StateMachine.
     *
     * @param event The event that occurred.
     */
    public final void fireErrorEvent(CallXmlEvent event) {

        derivedEvents.add(
                new TriggerEvent(event.toString(), TriggerEvent.ERROR_EVENT));
    }

    /**
     * Fire an error event on the StateMachine.
     *
     * @param event The event that occurred.
     */
    public final void fireEvent(String event) {
        derivedEvents.add(
                new TriggerEvent(event, TriggerEvent.SIGNAL_EVENT));
    }

    /**
     * An method to check if the last call to the ivrSession
     * came back with hangup or error.
     *
     * @param event The event to validate.
     *
     * @return true If the action should proceed false otherwise.
     */
    public final boolean proceed(FSEvent event) {
        boolean proceed = false;

        if (event == null) {
            fireEvent(CallXmlEvent.HANGUP);
            log.warn("Event is null should not happen");
            ctx.set("isconnected", Boolean.FALSE);

        } else if (event.contains(FSEventName.CHANNEL_HANGUP)) {
            fireEvent(CallXmlEvent.HANGUP);
            log.debug("Procced is false call hangup ");
            ctx.set("isconnected", Boolean.FALSE);

        } else if (event.contains(FSEventName.ERROR)) {
            fireEvent(CallXmlEvent.ERROR);
            log.debug("Procced is false call error ");

        } else {
            proceed = true;

        }

        return proceed;
    }

    /**
     * Handle all pre actions here like tell the ivrSession to execute some
     * events.
     *
     * @param sSession A session to use for executing IVR commands.
     *
     */
    public abstract void handleAction(FSSession sSession);

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

        boolean isValid = true;

        for (String value : fields) {

            if (value == null || value.isEmpty()) {
                fireErrorEvent(CallXmlEvent.ERROR);
                isValid = false;
                break;
            }

        }

        return isValid;
    }

    /**
     * Add a variable to the root context.
     *
     * @param name The name of the variable.
     * @param var  The value of the variable.
     */
    public final void setContextVar(String name, Object var) {
        ctx.set(name, var);
    }

    /**
     * Get a variable from the context.
     *
     * @param name of variable
     *
     * @return Object found or null.
     */
    public Object getContextVar(String name) {
        return ctx.get(name);
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
        Object eval = null;

        if (toEval == null || toEval.isEmpty()) {
            log.error("Can't eval empty field !!!!");
            return "";

        } else {
            try {

                eval = evaluator.eval(ctx, toEval);
            } catch (SCXMLExpressionException ex) {
                log.error(ex.getMessage());
                fireErrorEvent(CallXmlEvent.ERROR);
            }
        }

        if (eval != null) {
            return eval.toString();

        } else {
            return toEval;
        }

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

        if (vars == null || vars.isEmpty()) {
            return Collections.emptyMap();

        } else {

            String[] varNames = vars.split("\\s+");

            Map<?, ?> ctxVarMap = ctx.getVars();
            Map<String, Object> copyToMap = new HashMap<String, Object>();

            for (int i = 0; i < varNames.length; i++) {

                String var = varNames[i];

                if (ctxVarMap.containsKey(var)) {
                    copyToMap.put(var, ctxVarMap.get(var));
                }
            }

            return copyToMap;

        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void execute(
            EventDispatcher evtDispatcher,
            ErrorReporter errRep,
            SCInstance scInstance,
            Log appLog,
            Collection derivedEvents)
            throws ModelException, SCXMLExpressionException {

        this.derivedEvents = derivedEvents;
        this.ctx = scInstance.getRootContext();
        this.evaluator = scInstance.getEvaluator();

        if (actionShouldExecute()) {
            FSSession fss = (FSSession) ctx.get(FSSession.class.getName());

            if (fss.isAlive()) {
                handleAction(fss);

            } else {
                fireErrorEvent(CallXmlEvent.HANGUP);
            }
            
        } else {
            log.info("rejected action because test evaluated to false ");
        }
    }

    /**
     * Test if an action should execute.
     *
     * @return true if the action should false otherwise.
     */
    private boolean actionShouldExecute() {

        boolean result = true;

        if (test == null) {
            log.trace("actionShouldExecute: test == null, so return true");
            return true;
        }

        try {

            result = evaluator.evalCond(ctx, test);
        } catch (SCXMLExpressionException ex) {
            log.error(ex.getMessage());
        }


        return result;
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
        String target = eval(expr);

        URL base = (URL) ctx.get("base");
        URL targetU = null;

        try {
            targetU = new URL(base, target);
            return targetU.getFile();

        } catch (MalformedURLException ex) {
            log.error(ex.getMessage());

            FSSession ivrSession =
                    (FSSession) ctx.get(FSSession.class.getName());

            ivrSession.hangup();

            throw new IllegalStateException("Failed to get prompt");
        }


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

        if (time == null) {
            throw new IllegalArgumentException("Time value string can not be null");
//            log.error("Time is null will return default value ");
//            return 10000;
        }

        int timeInt = 0;
        StringBuilder strBuff = new StringBuilder();

        char candidate;

        for (int i = 0; i < time.length(); i++) {
            candidate = time.charAt(i);

            if (Character.isDigit(candidate)) {
                strBuff.append(candidate);
            }
        }

        int digits = Integer.parseInt(strBuff.toString());

        if (time.endsWith("ms")) {
            timeInt = digits;

        } else if (time.endsWith("s")) {
            timeInt = digits * 1000;

        } else if (time.endsWith("m")) {
            timeInt = digits * 1000 * 60;

        }

        return timeInt;

    }
}
