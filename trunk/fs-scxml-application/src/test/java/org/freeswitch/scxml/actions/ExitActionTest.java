package org.freeswitch.scxml.actions;


import java.util.HashMap;

import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.easymock.EasyMock;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

public final class ExitActionTest {

    private Session session;
    private ExitAction action;
    private ActionSupport actionSupport;
    private String varNames = "test1 test2";
    private HashMap<String, String> vars;
    
    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        actionSupport = EasyMock.createMock(ActionSupport.class);
        session = EasyMock.createMock(Session.class);
        
        action = new ExitAction();
        vars = new HashMap<>();
        action.setNamelist(varNames);
    }

    @Test
    public void testHandleActionNoNamelist() throws ModelException, SCXMLExpressionException, HangupException {

        vars.clear();
        expect(actionSupport.getNameListAsMap(varNames)).andReturn(vars);
        expect(session.hangup()).andReturn(EventListBuilder.single(Event.CHANNEL_EXECUTE_COMPLETE));
        
        replay(actionSupport, session);
        action.handleAction(session, actionSupport);
        verify(actionSupport, session);
    }


}


