package org.freeswitch.scxml.actions;

import java.util.HashMap;
import java.util.EnumSet;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.Session;
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
    public void testHandleActionMaxtime() {
        HashMap<String, String> vars = new HashMap<String, String>();
        String rec = "/home/test/test.wav";
        final String time = "2000";
        
        vars.put(RecordAudioAction.RECORD_MS, time);
        vars.put(RecordAudioAction.RECORD_PATH, rec);
        
        expect(session.clearDigits()).andReturn(Boolean.TRUE);
        expect(actionSupport.getMillisFromString("60s")).andReturn(60000);
        expect(session.recordFile(TIME_LIMIT, true, EnumSet.of(DTMF.POUND), "wav"))
                .andReturn(EventList.single(new Event(Event.CHANNEL_EXECUTE_COMPLETE, vars)));
        expect(actionSupport.proceed(isA(EventList.class))).andReturn(Boolean.TRUE);
        actionSupport.setContextVar(action.getVar(), rec);
        actionSupport.setContextVar(action.getTimevar(), time);
        actionSupport.fireEvent(CallXmlEvent.MAXTIME);
        
        replay(session, actionSupport);
        action.handleAction(session);
        verify(session, actionSupport);

    }
}
