package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.event.Event;
import java.util.EnumSet;
import org.freeswitch.adapter.api.constant.DTMF;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
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
    public void testHandleActionMatch() throws HangupException {
        EventList evtl = EventListBuilder.single(DTMF.ONE);
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
    public void testHandleActionNoMatch() throws HangupException {
        EventList evtl = EventListBuilder.single(DTMF.FOUR);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        actionSupport.fireEvent(CallXmlEvent.NOMATCH);
        handleAction();
    }
    
    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleActionTimeout() throws HangupException {
        EventList evtl = EventListBuilder.single(Event.TIMEOUT);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        actionSupport.fireEvent(CallXmlEvent.MAXTIME);
        handleAction();
    }
    
    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleActionTermdigit() throws HangupException {
        EventList evtl = EventListBuilder.single(DTMF.POUND);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        actionSupport.fireEvent(CallXmlEvent.TERMDIGIT);
        handleAction();
    }
    
    /**
     * Test of handleAction method, of class MenuAction.
     */
    @Test
    public void testHandleActionHangup() throws NumberFormatException, HangupException {
        EventList evtl = EventListBuilder.single(Event.CHANNEL_HANGUP);
        readAndReturn(evtl);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.FALSE);
        handleAction();
    }
 
    private void readAndReturn(EventList evtl) throws NumberFormatException, HangupException {
        expect(actionSupport.getPath(PROMPT)).andReturn(PROMPT);
        expect(actionSupport.getMillisFromString("30s")).andReturn(30000);
        expect(ivrSession.clearDigits()).andReturn(Boolean.TRUE);
        expect(ivrSession.read(1, PROMPT, 30000 , EnumSet.of(DTMF.POUND))).andReturn(evtl);
    }
    
    private void handleAction() throws HangupException {
        replay(ivrSession, actionSupport);
        action.handleAction(ivrSession);
        verify(ivrSession, actionSupport);
    }
}