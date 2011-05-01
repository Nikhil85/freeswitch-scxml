package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.Event;
import java.util.Set;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventListBuilder;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public final class GetDigitsActionTest {

    public static final String MAX_TIME_STRING = "40s";
    public static final String NUMBER = "number";
    private static final int MAX_DIGITS = 7;
    private static final int MAX_TIME = 40000;
    private Session session;
    private GetDigitsAction action;
    private ActionSupport actionSupport;

    @Before
    public void setUp() {
        actionSupport = createMock(ActionSupport.class);
        session = createMock(Session.class);
        action = new GetDigitsAction();
        action.setVar(NUMBER);
        action.setTermdigits("#*");
        action.setMaxdigits(MAX_DIGITS);
        action.setMaxtime(MAX_TIME_STRING);
        action.setIncludetermdigit(false);
        action.setActionSupport(actionSupport);
    }

    @Test
    public void testHandleActionMaxtime() throws HangupException {
        String dtmfs = "123467";
        EventList list = EventListBuilder.list(dtmfs, Event.TIMEOUT);
        expect(actionSupport.getMillisFromString(MAX_TIME_STRING)).andReturn(MAX_TIME);
        expect(session.getDigits(MAX_DIGITS, getTermDigits(), MAX_TIME)).andReturn(list);
        actionSupport.fireEvent(CallXmlEvent.MAXTIME);
        actionSupport.setContextVar(NUMBER, dtmfs);
        replay(actionSupport, session);
        action.handleAction(session);
        verify(actionSupport, session);
    }

    @Test
    public void testHandleActionTermdigit() throws HangupException {
        expect(actionSupport.getMillisFromString(MAX_TIME_STRING)).andReturn(MAX_TIME);
        expect(session.getDigits(MAX_DIGITS, getTermDigits(), MAX_TIME)).andReturn(EventListBuilder.list("123467#"));
        actionSupport.fireEvent(CallXmlEvent.TERMDIGIT);
        actionSupport.setContextVar(NUMBER, "123467");
        replay(actionSupport, session);
        action.handleAction(session);
        verify(actionSupport, session);
    }
  
    @Test
    public void testHandleActionMaxDigits() throws HangupException {
        expect(actionSupport.getMillisFromString(MAX_TIME_STRING)).andReturn(MAX_TIME);
        String digits = "12345678";
        expect(session.getDigits(MAX_DIGITS, getTermDigits(), MAX_TIME)).andReturn(EventListBuilder.list(digits));
        actionSupport.fireEvent(CallXmlEvent.MAXDIGITS);
        actionSupport.setContextVar(NUMBER, digits);
        replay(actionSupport, session);
        action.handleAction(session);
        verify(actionSupport, session);
    }
    
    private Set<DTMF> getTermDigits() {
        return DTMF.setFromString(action.getTermdigits());
    }
}
