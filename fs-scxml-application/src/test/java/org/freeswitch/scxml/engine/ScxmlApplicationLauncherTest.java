package org.freeswitch.scxml.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import java.net.URL;
import org.easymock.EasyMock;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.scxml.application.api.ApplicationLauncher;
import org.freeswitch.test.utils.MockLookup;
import org.junit.Before;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


/**
 *
 * @author jocke
 */
public final class ScxmlApplicationLauncherTest {

    private Session session;
    private ScxmlApplication application;
    private ApplicationLauncher launcher;

    @Before
    public void setUp() {
        session = EasyMock.createMock(Session.class);
        application = EasyMock.createMock(ScxmlApplication.class);
        launcher = new ScxmlApplicationLauncher();
        MockLookup.setInstances(application);
    }

    @Test
    public void testLaunch() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("variable_sip_to_params", "scxml=file:/home/test/test.xml");

        expect(session.getVars()).andReturn(vars);
        application.createAndStartMachine(isA(URL.class), isA(Map.class));
        
        EasyMock.replay(session, application);
        launcher.launch(session);
        EasyMock.verify(session, application);

        assertTrue("The ivr session has not been addded.", vars.containsKey(Session.class.getName()));
        assertTrue("The ivr session has not been addded.", vars.containsValue(session));
    }
    
    @Test
    public void testIsLaunchable() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        vars.put("variable_sip_to_params", "scxml=file:/home/test/test.xml");

        expect(session.getVars()).andReturn(vars);
        EasyMock.replay(session, application);
        assertTrue(launcher.isLaunchable(session));
        EasyMock.verify(session, application);
    }
    
    @Test
    public void testIsNotLaunchable() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        expect(session.getVars()).andReturn(vars);
        EasyMock.replay(session, application);
        assertFalse(launcher.isLaunchable(session));
        EasyMock.verify(session, application);
    }
}
