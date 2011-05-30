package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.session.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class CallActionTest {

    private CallAction callAction;
    private Session session;
    private ActionSupport as;
    private final String value = "sip:test@test.com";
    
    @Before
    public void setUp() {
        callAction = new CallAction();
        as = createMock(ActionSupport.class);
        session = createMock(Session.class);
        callAction.setValue(value);
        
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of handleAction method, of class CallAction.
     */
    @Test
    public void testHandleAction() throws Exception {
        
        expect(as.validateFields(value)).andReturn(Boolean.TRUE);
        expect(as.eval(value)).andReturn(value);
        session.call(eq(value));
        
        replay(session, as);
        callAction.handleAction(session, as);
        verify(session, as);
    }
}
