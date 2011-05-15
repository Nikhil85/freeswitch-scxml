package org.freeswitch.adapter;

import java.util.Map;
import java.util.HashMap;
import org.freeswitch.adapter.api.CommandExecutor;
import org.freeswitch.adapter.api.EventQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class SessionImplTest {

    private SessionImpl sessionImpl;
    private EventQueue eventQueue;
    private CommandExecutor executor;

    @Before
    public void setUp() {
        eventQueue = createMock(EventQueue.class);
        executor = createMock(CommandExecutor.class);
        Map<String, Object> vars = new HashMap<>();
        sessionImpl = new SessionImpl(vars, eventQueue, executor);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of lookup method, of class SessionImpl.
     */
    @Test
    public void testLookup() {
        assertNotNull(sessionImpl.lookup(AudioAdapter.class));
        assertNotNull(sessionImpl.lookup(TestExtension.class));
    }

    /**
     * Test of call method, of class SessionImpl.
     */
    @Test
    public void testCall() {
    }

    /**
     * Test of execute method, of class SessionImpl.
     */
    @Test
    public void testExecute() {
    }
}
