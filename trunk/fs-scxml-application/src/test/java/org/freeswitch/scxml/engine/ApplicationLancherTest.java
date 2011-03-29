package org.freeswitch.scxml.engine;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import java.net.URL;
import org.easymock.EasyMock;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.ApplicationLauncher;
import org.freeswitch.test.utils.MockLookup;
import org.junit.Before;

/**
 *
 * @author jocke
 */
public final class ApplicationLancherTest {

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
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("variable_sip_to_params", "scxml=file:/home/test/test.xml");

        expect(session.getVars()).andReturn(vars);
        application.createAndStartMachine(isA(URL.class), isA(Map.class));
        
        EasyMock.replay(session, application);
        launcher.launch(session);
        EasyMock.verify(session, application);

        assertTrue("The ivr session has not been addded.", vars.containsKey(Session.class.getName()));
        assertTrue("The ivr session has not been addded.", vars.containsValue(session));
    }
}
