package org.freeswitch.scxml.actions;

import java.util.HashMap;
import java.util.EnumSet;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventListBuilder;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.adapter.api.VarName;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public final class RecordAudioActionTest {

    private static final int TIME_LIMIT = 60000;
    private static final String RECORD = "/tmp/ndskdac454dcc15ac44.wav";
    private Session session;
    private RecordAudioAction action;
    private ActionSupport actionSupport;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        actionSupport = createMock(ActionSupport.class);
        session = createMock(Session.class);
        action = new RecordAudioAction();
        action.setBeep(true);
        action.setVar("recording");
        action.setMaxtime("60s");
        action.setFormat("wav");
        action.setTermdigits("#");
        action.setActionSupport(actionSupport);
    }

    /**
     * Test of handleAction method, of class RecordAudioAction.
     */
    @Test
    public void testHandleActionMaxtime() throws HangupException {
        HashMap<String, String> vars = new HashMap<>();
        String data = "/home/test/test.wav 10";
        
        vars.put(VarName.APPLICATION_DATA, data);
        
        expect(session.clearDigits()).andReturn(Boolean.TRUE);
        expect(actionSupport.getMillisFromString("60s")).andReturn(60000);
        expect(session.recordFile(TIME_LIMIT, true, EnumSet.of(DTMF.POUND), "wav"))
                .andReturn(EventListBuilder.single(new Event(Event.CHANNEL_EXECUTE_COMPLETE, vars)));
        expect(actionSupport.proceed(isA(EventList.class))).andReturn(Boolean.TRUE);
        actionSupport.setContextVar(action.getVar(), "/home/test/test.wav");
        actionSupport.setContextVar(action.getTimevar(), "10" );
        actionSupport.fireEvent(CallXmlEvent.MAXTIME);
        
        replay(session, actionSupport);
        action.handleAction(session);
        verify(session, actionSupport);

    }
}
