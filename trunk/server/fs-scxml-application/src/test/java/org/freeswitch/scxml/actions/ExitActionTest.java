package org.freeswitch.scxml.actions;


import java.util.HashMap;

import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.easymock.EasyMock;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.Session;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

public final class ExitActionTest {

    private Session session;
    private ExitAction action;
    private ActionSupport actionSupport;
    private String varNames = "test1 test2";
    private HashMap<String, Object> vars;
    
    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        actionSupport = EasyMock.createMock(ActionSupport.class);
        session = EasyMock.createMock(Session.class);
        
        action = new ExitAction();
        vars = new HashMap<String, Object>();
        action.setNamelist(varNames);
        action.setActionSupport(actionSupport);
    }

    @Test
    public void testHandleActionNamelist()throws ModelException, SCXMLExpressionException {

        vars.put("test1", "value1");
        expect(actionSupport.getNameListAsMap(varNames)).andReturn(vars);
        expect(session.hangup(vars)).andReturn(EventList.single(Event.CHANNEL_HANGUP));
       
        replay(session, actionSupport);
        action.handleAction(session);
        verify(session, actionSupport);

    }

    @Test
    public void testHandleActionNoNamelist() throws ModelException, SCXMLExpressionException {

        vars.clear();
        expect(actionSupport.getNameListAsMap(varNames)).andReturn(vars);
        expect(session.hangup()).andReturn(EventList.single(Event.CHANNEL_EXECUTE_COMPLETE));
        
        replay(actionSupport, session);
        action.handleAction(session);
        verify(actionSupport, session);
    }


}


