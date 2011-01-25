package com.telmi.msc.scxml.actions;

import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.Send;

/**
 *
 * @author jocke
 */
public final class SendAction extends Send {

    /**
     * This is where the derived events will be stored in the context.
     */
    public static final String DERIVED_EVENTS = "derivedEvents";

    private static final long serialVersionUID = 1L;

    @Override
    public void execute(EventDispatcher evtDispatcher, ErrorReporter errRep,
            SCInstance scInstance, Log appLog, Collection derivedEvents)
            throws ModelException, SCXMLExpressionException {
        scInstance.getRootContext().set( DERIVED_EVENTS, derivedEvents);
        super.execute(evtDispatcher, errRep, scInstance, appLog, derivedEvents);
    }
}
