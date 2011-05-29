package org.freeswitch.adapter.internal.session;

import org.freeswitch.adapter.api.event.DefaultEventQueue;
import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class ControlAdapterTest {
    public static final String UID = "1111";
    
    private ControlAdapter adapter;
    private Session session;
    private SessionState state;
    private DefaultEventQueue eventQueue;
    private Command cmd;
    
    @Before
    public void setUp() {
        session = createMock(Session.class);
        state = createMock(SessionState.class);
        eventQueue = createMock(DefaultEventQueue.class);
        cmd = new Command(UID);
        adapter= new ControlAdapter(session, state, cmd);    
    }
    
    /**
     * Test of answer method, of class ControlAdapter.
     */
    @Test
    public void testAnswer() throws InterruptedException, HangupException {
      expect(session.getUuid()).andReturn(UID);
      expect(state.isNotAnswered()).andReturn(Boolean.TRUE);
      expect(session.execute(cmd.answer())).andReturn(eventQueue);
      state.setNotAnswered(Boolean.FALSE);
      expect(eventQueue.poll(anyInt(), eq(TimeUnit.MINUTES))).andReturn(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));
      
      replay(session, state, eventQueue);
      EventList answer = adapter.answer();
      verify(session, state, eventQueue);
      
      assertTrue(answer.contains(Event.CHANNEL_EXECUTE_COMPLETE));
    }
    
    /**
     * Test of answer method, of class ControlAdapter.
     */
    @Test
    public void testAnswer_already_answered() throws InterruptedException, HangupException {
      expect(session.getUuid()).andReturn(UID);
      expect(state.isNotAnswered()).andReturn(Boolean.FALSE);
      
      replay(session, state, eventQueue);
      EventList answer = adapter.answer();
      verify(session, state, eventQueue);
      
      assertTrue(answer.contains(Event.CHANNEL_EXECUTE_COMPLETE));
    }
}
