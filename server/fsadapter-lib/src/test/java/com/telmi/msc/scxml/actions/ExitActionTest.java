package com.telmi.msc.scxml.actions;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.ModelException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;

import com.telmi.msc.freeswitch.FSEventName;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.scxml.actions.ExitAction;

/**
 *
 * @author jocke
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public final class ExitActionTest {

    @Mock private FSSession session;
    @Mock private Context ctx;
    @Mock private Evaluator evaluator;
    private ExitAction action;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        action = new ExitAction();
        action.evaluator = evaluator;
        action.ctx = ctx;
    }

    /**
     * Test of handleAction method, of class ExitAction.
     *
     * @throws ModelException If it happens.
     * @throws SCXMLExpressionException If it happens.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testHandleActionNamelist()
            throws ModelException, SCXMLExpressionException {

        Map<String,Object> vars = new HashMap<String, Object>();
        vars.put("test1","value1");
        vars.put("test2","value2");

        String nameList = "test1 test2";

        action.setNamelist(nameList);

        expect(ctx.getVars()).andReturn(vars);

        FSEvent event = FSEvent.getInstance(
                FSEventName.CHANNEL_EXECUTE_COMPLETE);

        expect(session.hangup(isA(Map.class))).andReturn(event);

        EasyMockUnitils.replay();

        action.handleAction(session);

    }

    /**
     * Test of handleAction method, of class ExitAction.
     *
     * @throws ModelException           If it happens.
     * @throws SCXMLExpressionException If it happens.
     */
    @Test
    public void testHandleActionNoNamelist()
            throws ModelException, SCXMLExpressionException {

        Map<String,Object> vars = new HashMap<String, Object>();

        String nameList = "test1 test2";

        action.setNamelist(nameList);

        expect(ctx.getVars()).andReturn(vars);

        FSEvent event = FSEvent.getInstance(
                FSEventName.CHANNEL_EXECUTE_COMPLETE);

        expect(session.hangup()).andReturn(event);

        EasyMockUnitils.replay();

        action.handleAction(session);

    }


}


