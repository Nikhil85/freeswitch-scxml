package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.CallAdapter;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;

/**
 *
 * @author jocke
 */
public class CallAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    
    private String value;
    
    @Override
    public void handleAction(Session fss) throws HangupException {
        CallAdapter adapter = fss.lookup(CallAdapter.class);
        adapter.originate(value);
    }
}
