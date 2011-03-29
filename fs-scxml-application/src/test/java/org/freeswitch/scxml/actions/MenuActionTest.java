package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.Event;
import java.util.EnumSet;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class MenuActionTest {
    public static final String PROMPT = "/test/test.wav";

    private MenuAction action;
    private Session ivrSession;
    private ActionSupport actionSupport;
    
    @Before
    public void setUp() {
      actionSupport = createMock(ActionSupport.class);
      ivrSession = createMock(Session.class);
      
      action = new MenuAction();
      action.setValue(PROMPT);
      action.setChoices("1");
      action.setActionSupport(actionSupport);
      
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleActionMatch() {
        EventList evtl = EventList.single(DTMF.ONE);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        actionSupport.fireChoiceEvent(DTMF.ONE);
        actionSupport.fireMatchEvent(DTMF.ONE);
        handleAction();
    }

    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleActionNoMatch() {
        EventList evtl = EventList.single(DTMF.FOUR);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        actionSupport.fireEvent(CallXmlEvent.NOMATCH);
        handleAction();
    }
    
    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleActionTimeout() {
        EventList evtl = EventList.single(Event.TIMEOUT);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        actionSupport.fireEvent(CallXmlEvent.MAXTIME);
        handleAction();
    }
    
    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleActionTermdigit() {
        EventList evtl = EventList.single(DTMF.POUND);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        actionSupport.fireEvent(CallXmlEvent.TERMDIGIT);
        handleAction();
    }
    
    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleActionHangup() {
        EventList evtl = EventList.single(Event.CHANNEL_HANGUP);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.FALSE);
        handleAction();
    }
 
    private void readAndReturn(EventList evtl) throws NumberFormatException {
        expect(actionSupport.getPath(PROMPT)).andReturn(PROMPT);
        expect(actionSupport.getMillisFromString("30s")).andReturn(30000);
        expect(ivrSession.clearDigits()).andReturn(Boolean.TRUE);
        expect(ivrSession.read(1, PROMPT, 30000 , EnumSet.of(DTMF.POUND))).andReturn(evtl);
    }
    
    private void handleAction() {
        replay(ivrSession, actionSupport);
        action.handleAction(ivrSession);
        verify(ivrSession, actionSupport);
    }
}