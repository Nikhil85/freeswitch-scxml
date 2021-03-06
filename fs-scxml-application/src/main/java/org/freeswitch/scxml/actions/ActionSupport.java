package org.freeswitch.scxml.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.TriggerEvent;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public class ActionSupport {

    private Collection<TriggerEvent> derivedEvents;
    private Context ctx;
    private Evaluator evaluator;
    private static final Logger LOG = LoggerFactory.getLogger(ActionSupport.class);

    public ActionSupport() {
    }

    public ActionSupport(Collection<TriggerEvent> derivedEvents, Context ctx, Evaluator evaluator) {
        this.derivedEvents = derivedEvents;
        this.ctx = ctx;
        this.evaluator = evaluator;
    }

    /**
     * Add a variable to the root context.
     *
     * @param name The name of the variable.
     * @param var  The value of the variable.
     */
    public void setContextVar(String name, Object var) {
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

    public void fireEvent(CallXmlEvent event) {
        derivedEvents.add(new TriggerEvent(event.toString(), TriggerEvent.SIGNAL_EVENT));
    }

    public void fireChoiceEvent(DTMF dtmf) {
        derivedEvents.add(new TriggerEvent("choice:" + dtmf.toString(), TriggerEvent.SIGNAL_EVENT));
    }

    public void fireMatchEvent(DTMF dTMF) {
        derivedEvents.add(new TriggerEvent(CallXmlEvent.MATCH.toString(), TriggerEvent.SIGNAL_EVENT, dTMF.toString()));
    }

    public void fireErrorEvent(CallXmlEvent event) {
        derivedEvents.add(new TriggerEvent(event.toString(), TriggerEvent.ERROR_EVENT));
    }

    public void fireEvent(String event) {
        derivedEvents.add(new TriggerEvent(event, TriggerEvent.SIGNAL_EVENT));
    }

    public boolean actionShouldExecute(String test) {

        boolean result = true;

        if (test == null) {
            LOG.trace("actionShouldExecute: test == null, so return true");
            return true;
        }

        try {
            result = evaluator.evalCond(ctx, test);
        } catch (SCXMLExpressionException ex) {
            LOG.error(ex.getMessage());
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
    public String getPath(String expr) {
        return getUrl(eval(expr)).getFile();
    }

    public URL getUrl(String target) throws IllegalStateException {
        try {
            return new URL((URL) ctx.get("base"), target);
        } catch (MalformedURLException ex) {
            LOG.error(ex.getMessage());
            throw new IllegalStateException("Failed to get prompt");
        }
    }

    /**
     * Evaluate a variable.
     *
     * @param toEval an variable name to evaluate.
     *
     * @return The evaluated value or the value that
     *         was given as an argument, if evaluation fails.
     */
    public String eval(final String toEval) {
        Object eval = null;

        if (toEval == null || toEval.isEmpty()) {
            LOG.error("Can't eval empty field !!!!");
            return "";

        } else {
            try {

                eval = evaluator.eval(ctx, toEval);
            } catch (SCXMLExpressionException ex) {
                LOG.error(ex.getMessage());
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
     * An method to check if the last call to the ivrSession
     * came back with hangup or error.
     *
     * @param event The event to validate.
     *
     * @return true If the action should proceed false otherwise.
     */
    public boolean proceed(EventList evtl) {

        boolean proceed = false;

        if (evtl == null) {
            fireEvent(CallXmlEvent.HANGUP);
            LOG.warn("Event is null should not happen");
            ctx.set("isconnected", Boolean.FALSE);

        } else if (evtl.contains(Event.CHANNEL_HANGUP)) {
            fireEvent(CallXmlEvent.HANGUP);
            LOG.debug("Procced is false call hangup ");
            ctx.set("isconnected", Boolean.FALSE);

        } else if (evtl.contains(Event.ERROR)) {
            fireEvent(CallXmlEvent.ERROR);
            LOG.debug("Procced is false call error ");

        } else {
            proceed = true;
        }

        return proceed;
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
            throw new NumberFormatException("Time value string can not be null");
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
    public Map<String, String> getNameListAsMap(String vars) {

        if (vars == null || vars.isEmpty()) {
            return Collections.emptyMap();

        } else {

            String[] varNames = vars.split("\\s+");

            Map<?, ?> ctxVarMap = ctx.getVars();
            Map<String, String> copyToMap = new HashMap<>();

            for (int i = 0; i < varNames.length; i++) {

                String var = varNames[i];

                if (ctxVarMap.containsKey(var)) {
                    copyToMap.put(var, (String) ctxVarMap.get(var));
                }
            }

            return copyToMap;

        }
    }

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
    public boolean validFields(String... fields) {

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
}
