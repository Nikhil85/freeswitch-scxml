package org.freeswitch.scxml.engine;

import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public final class CountTest {

    public static final String MOCK_EVENT = "mockevent";
    private Context ctx;
    private JexlEvaluator evaluator;
    private Count count;

    @Before
    public void setUp() {
        ctx = new org.apache.commons.scxml.env.jexl.JexlContext();
        count = new Count(ctx);
        ctx.set("count", count);
        evaluator = new JexlEvaluator();
    }

    /**
     * Test of countUp method, of class Count.
     */
    @Test
    public void testCount() throws SCXMLExpressionException {
        assertTrue(evaluator.evalCond(ctx, "count eq 1"));
        ctx.setLocal(ScxmlSemanticsImpl.CURRENT_EVENT_EVALUATED, MOCK_EVENT);
        assertTrue(evaluator.evalCond(ctx, "count eq 1"));
        assertTrue(evaluator.evalCond(ctx, "count == 1"));
        count.countUp(MOCK_EVENT);
        assertTrue(evaluator.evalCond(ctx, "count eq 2"));
        assertTrue(evaluator.evalCond(ctx, "2 eq count"));
        assertFalse(evaluator.evalCond(ctx, "count eq 1"));
        assertTrue(evaluator.evalCond(ctx, "count == 2"));
        count.countUp(MOCK_EVENT);
        assertTrue(evaluator.evalCond(ctx, "count eq 3"));
        assertTrue(evaluator.evalCond(ctx, "count le 3"));
        assertTrue(evaluator.evalCond(ctx, "count gt 2"));
        ctx.setLocal(ScxmlSemanticsImpl.CURRENT_EVENT_EVALUATED, "event");
        assertTrue(evaluator.evalCond(ctx, "count eq 1"));
        assertEquals(1 , evaluator.eval(ctx, "count.value"));
    }
}
