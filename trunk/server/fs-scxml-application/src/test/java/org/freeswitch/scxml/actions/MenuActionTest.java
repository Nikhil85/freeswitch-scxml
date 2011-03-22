package org.freeswitch.scxml.actions;

import org.easymock.EasyMock;
import org.freeswitch.adapter.api.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author jocke
 */
public class MenuActionTest {

    private MenuAction action;
    private Session ivrSession;
    
    @Before
    public void setUp() {
      action = new MenuAction();
      action.setValue("/test/test.wav");
      ivrSession = EasyMock.createMock(Session.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleAction() {
        System.out.println("handleAction");
        action.handleAction(ivrSession);
    }
}