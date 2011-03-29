package org.freeswitch.scxml.actions;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author jocke
 */
public final class ActionSupportTest {

    private Context ctx;
    private Evaluator evaluator;
    private ActionSupport actionSupport;
    
    
    /**
     * Set up the test.
     */

    @Before
    public void setUp() {
      evaluator = EasyMock.createMock(Evaluator.class);
      ctx = EasyMock.createMock(Context.class);
      actionSupport = new ActionSupport(null, ctx, evaluator);
    }


    /**
     * Test of getNameListAsMap method, of class CallXmlAction.
     */
    @Test
    public void testGetNameListAsMap() {

        String vars = "var1 var2  var3      var4";
        Map<String, Object> ctxVarMap = createVarMap();

        expect(ctx.getVars()).andReturn(ctxVarMap);
        
        EasyMock.replay(ctx);
        Map<String, Object> nListMap = actionSupport.getNameListAsMap(vars);
        EasyMock.verify(ctx);
        
        assertTrue("Should contain var1 ", nListMap.containsKey("var1"));
        assertTrue("Should contain var2 ", nListMap.containsKey("var2"));
        assertTrue("Should contain var3 ", nListMap.containsKey("var3"));
        assertTrue("Should contain var4 ", nListMap.containsKey("var4"));
        assertTrue("Should contain value1 ", nListMap.containsValue("value1"));
        assertTrue("Should contain value2 ", nListMap.containsValue("value2"));
        assertTrue("Should contain value3 ", nListMap.containsValue("value3"));
        assertTrue("Should contain value4 ", nListMap.containsValue("value4"));

    }

    private Map<String, Object> createVarMap() {
        Map<String, Object> ctxVarMap = new HashMap<String, Object>();
        ctxVarMap.put("var1", "value1");
        ctxVarMap.put("var2", "value2");
        ctxVarMap.put("var3", "value3");
        ctxVarMap.put("var4", "value4");
        return ctxVarMap;
    }

    /**
     * Test of getNameListAsMap method, of class CallXmlAction.
     */
    @Test
    public void testGetNameListAsMapNullOrEmpty() {

        Map<String, Object> nListMap = actionSupport.getNameListAsMap("");
        assertTrue("Empty String was passed in should be empty ", nListMap.isEmpty());

        Map<String, Object> emptyMap = actionSupport.getNameListAsMap(null);
        assertTrue("null was passed in should be empty ", emptyMap.isEmpty());
    }
}
