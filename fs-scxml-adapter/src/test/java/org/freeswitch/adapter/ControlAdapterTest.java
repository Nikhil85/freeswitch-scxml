package org.freeswitch.adapter;

import java.util.concurrent.TimeUnit;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.adapter.api.EventQueue;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class ControlAdapterTest {
    public static final String uid = "1111";
    
    private ControlAdapter adapter;
    private Session session;
    private SessionState state;
    private EventQueue eventQueue;
    
    @Before
    public void setUp() {
        session = createMock(Session.class);
        state = createMock(SessionState.class);
        eventQueue = createMock(EventQueue.class);
        adapter= new ControlAdapter(session, state);    
    }
    
    /**
     * Test of answer method, of class ControlAdapter.
     */
    @Test
    public void testAnswer() throws InterruptedException, HangupException {
      expect(session.getUuid()).andReturn(uid);
      expect(state.isNotAnswered()).andReturn(Boolean.TRUE);
      expect(session.execute(Command.answer())).andReturn(eventQueue);
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
      expect(session.getUuid()).andReturn(uid);
      expect(state.isNotAnswered()).andReturn(Boolean.FALSE);
      
      replay(session, state, eventQueue);
      EventList answer = adapter.answer();
      verify(session, state, eventQueue);
      
      assertTrue(answer.contains(Event.CHANNEL_EXECUTE_COMPLETE));
    }

    /**
     * Test of sleep method, of class ControlAdapter.
     */
    @Test
    public void testSleep() {
    }

    /**
     * Test of hangup method, of class ControlAdapter.
     */
    @Test
    public void testHangup_Map() {
    }

    /**
     * Test of hangup method, of class ControlAdapter.
     */
    @Test
    public void testHangup_0args() {

    }

    /**
     * Test of breakAction method, of class ControlAdapter.
     */
    @Test
    public void testBreakAction() {
    }
}
