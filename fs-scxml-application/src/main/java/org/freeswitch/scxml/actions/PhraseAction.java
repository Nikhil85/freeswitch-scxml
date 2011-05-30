package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;



/**
 *
 * @author jocke
 */
public final class PhraseAction extends AbstractAction {

    private static final long serialVersionUID = -2177874537238327910L;
    private String value;
    private String type;
    private String method;
    private String module = "en";

    public String getMethod() {
        return method;
    }

    public void setMethod(String methodToUse) {
        this.method = methodToUse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
   
    public String getValue() {
        return value;
    }

    public void setValue(String toPhrase) {
        this.value = toPhrase;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
    
    @Override
    public void handleAction(Session fsSession, ActionSupport actionSupport) throws HangupException {
        
        if(type == null || method == null || value == null) {
            actionSupport.fireErrorEvent(CallXmlEvent.ERROR);
            log.error("Not a valid phrase command {} " , this);
            return;
        }
        
        actionSupport.proceed(fsSession.say(module, type.toUpperCase(), method.toUpperCase(), actionSupport.eval(value)));
    }

    @Override
    public String toString() {
        return "PhraseAction{" + "value=" + value + ", type=" + type + ", method=" + method + ", module=" + module + '}';
    }
    
    
}
