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

    public void setContextVar(String name, Object var) {
        ctx.set(name, var);
    }

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

    public String getPath(String expr) {
        String target = eval(expr);

        URL base = (URL) ctx.get("base");
        URL targetU = null;

        try {
            targetU = new URL(base, target);
            return targetU.getFile();

        } catch (MalformedURLException ex) {
            LOG.error(ex.getMessage());
            throw new IllegalStateException("Failed to get prompt");
        }


    }

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

    public Map<String, Object> getNameListAsMap(String vars) {

        if (vars == null || vars.isEmpty()) {
            return Collections.emptyMap();

        } else {

            String[] varNames = vars.split("\\s+");

            Map<?, ?> ctxVarMap = ctx.getVars();
            Map<String, Object> copyToMap = new HashMap<>();

            for (int i = 0; i < varNames.length; i++) {

                String var = varNames[i];

                if (ctxVarMap.containsKey(var)) {
                    copyToMap.put(var, ctxVarMap.get(var));
                }
            }

            return copyToMap;

        }
    }

    protected boolean validateFields(String... fields) {

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
