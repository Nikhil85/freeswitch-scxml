package org.freeswitch.scxml.engine;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;

import java.net.URL;
import org.freeswitch.adapter.Event;
import org.freeswitch.adapter.EventName;
import org.freeswitch.adapter.Session;
import org.junit.Ignore;

/**
 *
 * @author jocke
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public final class ApplicationLancherTest  {

    @Mock
    private Session session;

    @Mock
    private ScxmlApplication application;

    /**
     * {SASID=-313313177, remote=1000}
     *
     * Test of launch method, of class ApplicationLancher.
     *
     * @throws Exception any.
     */
    @SuppressWarnings("unchecked")
    @Test
    @Ignore
    public void testLaunch() throws Exception {

        ScxmlApplicationLauncher launcher = new ScxmlApplicationLauncher();

        Map<String, Object> vars = new HashMap<String, Object>();

        String eventMap = "{SASID=-313313177, remote=1000}";

        vars.put("variable_sip_h_X-Eventmap", eventMap);
        vars.put("variable_sip_to_params", "scxml=file:/home/test/test.xml");

        expect(session.getVars()).andReturn(vars);
        application.createAndStartMachine(isA(URL.class), isA(Map.class));
        
        expect(session.hangup()).andReturn(Event.getInstance(
                EventName.CHANNEL_EXECUTE_COMPLETE));

        EasyMockUnitils.replay();

        launcher.launch(session);

        assertTrue("The ivr session has not been addded.",
                vars.containsKey(Session.class.getName()));

        assertTrue("The ivr session has not been addded.",
                vars.containsValue(session));

        assertTrue("Sas id has not been added. ",
                vars.containsKey("SASID"));

        assertTrue("remote part has not been added. ",
                vars.containsKey("remote"));

        assertTrue("Sas id has not been added",
                vars.containsValue("-313313177"));

        assertTrue("remote part has not been added. ",
                vars.containsValue("1000"));


    }
}
