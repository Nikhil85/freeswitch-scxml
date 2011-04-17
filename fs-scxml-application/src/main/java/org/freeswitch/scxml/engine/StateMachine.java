package org.freeswitch.scxml.engine;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.digester.Digester;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.env.URLResolver;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.semantics.SCXMLSemanticsImpl;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author jocke
 */
public final class StateMachine {

    private SCXML machine;
    private static final Logger LOG = LoggerFactory.getLogger(StateMachine.class);
    private final URL scxmlDocument;

    /**
     * The only way to create a new StateMacine.
     *
     * @param document      A hopefully well formed document.
     * @param actions       A list of custom actions.
     * @param senderFactory Creates senders.
     */
    public StateMachine(final URL document) {
        this.scxmlDocument = document;
        ErrorHandler errHandler = new SimpleErrorHandler();
        Collection<? extends CustomAction> actions = lookupActions();
        Digester digester = SCXMLParser.newInstance(null, new URLResolver(document), new ArrayList<CustomAction>(actions));
        digester.setClassLoader(new ScxmlClassLoader(getClass().getClassLoader(), actions));
        digester.setErrorHandler(errHandler);
        
        try {
            machine = (SCXML) digester.parse(document);
            SCXMLParser.updateSCXML(machine);
        } catch (IOException ioe) {
            logError(ioe);
        } catch (SAXException sae) {
            logError(sae);
        } catch (ModelException me) {
            logError(me);
        }
    }

    private Collection<CustomAction> lookupActions() {
        return (Collection<CustomAction>) Lookup.getDefault().lookupAll(CustomAction.class);
    }

    /**
     *
     * Creates a new SCXMLExecutor and starts it up.
     * It will first check if the content has been modified
     * or if it has expired, if that would be the case a new SCXML object would
     * be created. If not it will use the already parsed entity to create a
     * SCXMLExecutor.
     *
     * @param rootCtx variables to install.
     */
    public void newMachine(final Context rootCtx) {
        ScxmlEventDispatcher dispatcher = new ScxmlEventDispatcher(rootCtx);
        SCXMLExecutor engine = new SCXMLExecutor(new JexlEvaluator(),dispatcher, new ErrorReporter(), new ScxmlSemanticsImpl());
        dispatcher.setExecutor(engine);
        engine.setStateMachine(machine);
        engine.setSuperStep(true);
        Count counter = new Count(rootCtx);
        engine.setRootContext(rootCtx);
        rootCtx.set("count", counter);
        engine.addListener(machine, new ScxmlListenerImpl(counter));
        try {
            engine.go();
        } catch (ModelException me) {
            logError(me);
        }
        
      
        
    }

    /**
     * Logs the error.
     *
     * @param exception The error that has occurred.
     */
    private void logError(Exception exception) {
        LOG.error(exception.getMessage());
    }

    /**
     * builds a string with this machines URL path,
     * last access time and the content length,
     * separated with a line break.
     *
     * @return A string representation of this class.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("machine working at: ").append(scxmlDocument.getPath()).append("\n");
        return builder.toString();
    }
}
