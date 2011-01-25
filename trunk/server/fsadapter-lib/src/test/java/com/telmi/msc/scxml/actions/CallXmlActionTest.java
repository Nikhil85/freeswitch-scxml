package com.telmi.msc.scxml.actions;

import com.telmi.msc.scxml.actions.AbstractCallXmlAction;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.TriggerEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;

import com.telmi.msc.freeswitch.FSSession;

/**
 *
 * @author jocke
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public final class CallXmlActionTest {

    @Mock private Context ctx;
    @Mock private Evaluator evaluator;
    private AbstractCallXmlAction action;

    /**
     * Set up the test.
     */

    @Before
    public void setUp() {
        action = new AbstractCallXmlAction() {
            private static final long serialVersionUID = 5182026492258468210L;

            @Override
            public void handleAction(FSSession sSession) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        action.evaluator = evaluator;
        action.ctx = ctx;
        action.derivedEvents = new ArrayList<TriggerEvent>();
    }


    /**
     * Test of getNameListAsMap method, of class CallXmlAction.
     */
    @Test
    public void testGetNameListAsMap() {

        String vars = "var1 var2  var3      var4";

        Map<String, Object> ctxVarMap = new HashMap<String, Object>();

        ctxVarMap.put("var1", "value1");
        ctxVarMap.put("var2", "value2");
        ctxVarMap.put("var3", "value3");
        ctxVarMap.put("var4", "value4");

        expect(ctx.getVars()).andReturn(ctxVarMap);

        EasyMockUnitils.replay();

        Map<String, Object> nListMap = action.getNameListAsMap(vars);

        assertTrue("Should contain var1 ", nListMap.containsKey("var1"));
        assertTrue("Should contain var2 ", nListMap.containsKey("var2"));
        assertTrue("Should contain var3 ", nListMap.containsKey("var3"));
        assertTrue("Should contain var4 ", nListMap.containsKey("var4"));
        assertTrue("Should contain value1 ", nListMap.containsValue("value1"));
        assertTrue("Should contain value2 ", nListMap.containsValue("value2"));
        assertTrue("Should contain value3 ", nListMap.containsValue("value3"));
        assertTrue("Should contain value4 ", nListMap.containsValue("value4"));

    }

    /**
     * Test of getNameListAsMap method, of class CallXmlAction.
     */
    @Test
    public void testGetNameListAsMapNullOrEmpty() {

        Map<String, Object> nListMap = action.getNameListAsMap("");

        assertTrue("Empty String was passed in should be empty ",
            nListMap.isEmpty());

        Map<String, Object> emptyMap = action.getNameListAsMap(null);

        assertTrue("null was passed in should be empty ", emptyMap.isEmpty());


    }
}
