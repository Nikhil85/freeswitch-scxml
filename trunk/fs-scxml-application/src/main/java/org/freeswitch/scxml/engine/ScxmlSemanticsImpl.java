package org.freeswitch.scxml.engine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.SCXMLHelper;
import org.apache.commons.scxml.Step;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.Finalize;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.State;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.commons.scxml.semantics.ErrorConstants;
import org.apache.commons.scxml.semantics.SCXMLSemanticsImpl;

/**
 *
 * @author jocke
 */
public class ScxmlSemanticsImpl extends SCXMLSemanticsImpl {
    public static final String CURRENT_EVENT_EVALUATED = "__Current_Event_Evaluated";

    private static final String NAMESPACES_KEY = "_ALL_NAMESPACES";
    private Log appLog = LogFactory.getLog(ScxmlSemanticsImpl.class);

    @Override
    public void filterTransitionsSet(Step step, EventDispatcher evtDispatcher, ErrorReporter errRep, SCInstance scInstance) throws ModelException {
        /*
         * - filter transition set by applying events
         * (step/beforeStatus/events + step/externalEvents) (local check)
         * - evaluating guard conditions for
         * each transition (local check) - transition precedence (bottom-up)
         * as defined by SCXML specs
         */
        Set allEvents = new HashSet(step.getBeforeStatus().getEvents().size()
                + step.getExternalEvents().size());
        allEvents.addAll(step.getBeforeStatus().getEvents());
        allEvents.addAll(step.getExternalEvents());
        // Finalize invokes, if applicable
        for (Iterator iter = scInstance.getInvokers().keySet().iterator();
                iter.hasNext();) {
            State s = (State) iter.next();
            if (finalizeMatch(s.getId(), allEvents)) {
                Finalize fn = s.getInvoke().getFinalize();
                if (fn != null) {
                    try {
                        for (Iterator fnIter = fn.getActions().iterator();
                                fnIter.hasNext();) {
                            ((Action) fnIter.next()).execute(evtDispatcher,
                                    errRep, scInstance, appLog,
                                    step.getAfterStatus().getEvents());
                        }
                    } catch (SCXMLExpressionException e) {
                        errRep.onError(ErrorConstants.EXPRESSION_ERROR,
                                e.getMessage(), fn);
                    }
                }
            }
        }
        //remove list (filtered-out list)
        List removeList = new LinkedList();
        //iterate over non-filtered transition set
        for (Iterator iter = step.getTransitList().iterator();
                iter.hasNext();) {
            Transition t = (Transition) iter.next();
            // event check
            String event = t.getEvent();
            if (!eventMatch(event, allEvents)) {
                // t has a non-empty event which is not triggered
                removeList.add(t);
                continue; //makes no sense to eval guard cond.
            }
            // guard condition check
            Boolean rslt;
            String expr = t.getCond();
            if (SCXMLHelper.isStringEmpty(expr)) {
                rslt = Boolean.TRUE;
            } else {
                try {
                    Context ctx = scInstance.getContext(t.getParent());
                    Context rctx = scInstance.getRootContext();
                    
                    ctx.setLocal(NAMESPACES_KEY, t.getNamespaces());
                    customzieContext(rctx, event); 
                    
                    rslt = scInstance.getEvaluator().evalCond(ctx, t.getCond());
                   
                    ctx.setLocal(NAMESPACES_KEY, null);
                    resetCustomziedContext(rctx);
                } catch (SCXMLExpressionException e) {
                    rslt = Boolean.FALSE;
                    errRep.onError(ErrorConstants.EXPRESSION_ERROR, e.getMessage(), t);
                }
            }
            if (!rslt.booleanValue()) {
                // guard condition has not passed
                removeList.add(t);
            }
        }
        // apply event + guard condition filter
        step.getTransitList().removeAll(removeList);
        // cleanup temporary structures
        allEvents.clear();
        removeList.clear();
        // optimization - global precedence potentially applies
        // only if there are multiple enabled transitions
        if (step.getTransitList().size() > 1) {
            // global transition precedence check
            Object[] trans = step.getTransitList().toArray();
            // non-determinism candidates
            Set nonDeterm = new LinkedHashSet();
            for (int i = 0; i < trans.length; i++) {
                Transition t = (Transition) trans[i];
                TransitionTarget tsrc = t.getParent();
                for (int j = i + 1; j < trans.length; j++) {
                    Transition t2 = (Transition) trans[j];
                    TransitionTarget t2src = t2.getParent();
                    if (SCXMLHelper.isDescendant(t2src, tsrc)) {
                        //t2 takes precedence over t
                        removeList.add(t);
                        break; //it makes no sense to waste cycles with t
                    } else if (SCXMLHelper.isDescendant(tsrc, t2src)) {
                        //t takes precendence over t2
                        removeList.add(t2);
                    } else {
                        //add both to the non-determinism candidates
                        nonDeterm.add(t);
                        nonDeterm.add(t2);
                    }
                }
            }
            // check if all non-deterministic situations have been resolved
            nonDeterm.removeAll(removeList);
            if (nonDeterm.size() > 0) {
                // if not, first one in each state / region (which is also
                // first in document order) wins
                Set regions = new HashSet();
                Iterator iter = nonDeterm.iterator();
                while (iter.hasNext()) {
                    Transition t = (Transition) iter.next();
                    TransitionTarget parent = t.getParent();
                    if (regions.contains(parent)) {
                        removeList.add(t);
                    } else {
                        regions.add(parent);
                    }
                }
            }
            // apply global and document order transition filter
            step.getTransitList().removeAll(removeList);
        }
    }
    
    //This method was added so we can keep track of the event under evaluation
    private void customzieContext(Context ctx, String event) {
        ctx.set(CURRENT_EVENT_EVALUATED, event);
    }

    private void resetCustomziedContext(Context ctx) {
        ctx.set(CURRENT_EVENT_EVALUATED, null);
    }
}
