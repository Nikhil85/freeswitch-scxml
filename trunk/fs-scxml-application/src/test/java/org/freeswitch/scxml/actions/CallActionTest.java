package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.constant.Q850HangupCauses;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;
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
    private final String varName = "cid";
    private final String errCause = "reason_to_fail";
    
    @Before
    public void setUp() {
        callAction = new CallAction();
        as = createMock(ActionSupport.class);
        session = createMock(Session.class);
        callAction.setValue(value);
        callAction.setVar(varName);
        callAction.setReasonvar(errCause);   
    }

    /**
     * Test of handleAction method, of class CallAction.
     */
    @Test
    public void testHandleActionOkRespnse() throws Exception {
        final String uid = "e9d0qweucjsdu9c9";
        EventList evtList = EventListBuilder.single(new Event(Event.API_RESPONSE, CallAction.API_OK_RESPONSE + " " + uid));
        expectNormalFlowAndReturnList(evtList);
        as.setContextVar(varName, uid);
        as.fireEvent(CallXmlEvent.ANSWER);
        handleAction();
    }
    
    /**
     * Test of handleAction method, of class CallAction.
     */
    @Test
    public void testHandleActionErrBusyResponse() throws Exception {
        EventList evtList = EventListBuilder.single(new Event(Event.API_RESPONSE, CallAction.API_ERR_RESPONSE +" " + "USER_BUSY"));
        expectNormalFlowAndReturnList(evtList);
        as.setContextVar(errCause, Q850HangupCauses.USER_BUSY.name().toLowerCase());
        as.fireEvent(CallXmlEvent.BUSY);
        handleAction();
    }
    
    /**
     * Test of handleAction method, of class CallAction.
     */
    @Test
    public void testHandleActionErrResponse() throws Exception {
        final String NO_USER_RESPONSE = "NO_USER_RESPONSE";
        EventList evtList = EventListBuilder.single(new Event(Event.API_RESPONSE, CallAction.API_ERR_RESPONSE + " " + NO_USER_RESPONSE));
        expectNormalFlowAndReturnList(evtList);
        as.setContextVar(errCause, NO_USER_RESPONSE.toLowerCase());
        as.fireEvent(CallXmlEvent.NOANSWER);
        handleAction();
    }

    private void handleAction() throws HangupException {
        replay(session, as);
        callAction.handleAction(session, as);
        verify(session, as);
    }

    private void expectNormalFlowAndReturnList(EventList evtList) {
        expect(as.validFields(value, varName, errCause)).andReturn(Boolean.TRUE);
        expect(as.eval(value)).andReturn(value);
        expect(session.call(eq(value))).andReturn(evtList);
        expect(as.proceed(evtList)).andReturn(Boolean.TRUE);
    }
}
