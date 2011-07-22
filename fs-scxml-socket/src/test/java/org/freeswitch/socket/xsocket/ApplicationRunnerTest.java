package org.freeswitch.socket.xsocket;

import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.application.api.ApplicationLauncher;
import org.freeswitch.test.utils.MockLookup;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class ApplicationRunnerTest {

    private ApplicationRunner runner;
    private Session session;
    private ApplicationLauncher launcher;

    @Before
    public void setUp() {
        session = createMock(Session.class);
        launcher = createMock(ApplicationLauncher.class);
        runner = new ApplicationRunner(session);
    }

    /**
     * Test of run method, of class ApplicationRunner.
     */
    @Test
    public void testRun() {
        MockLookup.setInstances(launcher);
        expect(launcher.isLaunchable(session)).andReturn(Boolean.TRUE);
        launcher.launch(session);
        replay(launcher);
        runner.run();
        verify(launcher);
    }
    /**
     * Test of run method, of class ApplicationRunner.
     */
    @Test
    public void testRunNoLauncher() {
        MockLookup.setInstances();
        replay(launcher);
        runner.run();
        verify(launcher);
    }
}
