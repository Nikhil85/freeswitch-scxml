package org.freeswitch.scxml.actions;

import org.freeswitch.adapter.api.event.EventList;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.session.Session;
import org.freeswitch.adapter.api.constant.DTMF;
import java.net.MalformedURLException;
import java.util.EnumSet;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.freeswitch.adapter.api.event.EventListBuilder;
import org.freeswitch.scxml.engine.CallXmlEvent;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public final class PlayAudioActionTest {

    public static final String PROMPT = "/home/test/path.wav";
    private static final String VALUE = "path.wav";
    private Session session;
    private PlayAudioAction action;
    private ActionSupport actionSupport;

    /**
     * Set up the test.
     */
    @Before
    public void setUp() {
        actionSupport = createMock(ActionSupport.class);
        session = createMock(Session.class);
        action = new PlayAudioAction();
        action.setValue(VALUE);
        action.setTermdigits("#");
    }

    @Test
    public void testHandleActionTermdigit() throws SCXMLExpressionException, MalformedURLException, HangupException {
        EventList evtl = EventListBuilder.single(DTMF.POUND);
        action.setTermdigits("#*");
        expect(actionSupport.getPath(VALUE)).andReturn(PROMPT);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        expect(session.streamFile(PROMPT, EnumSet.of(DTMF.STAR, DTMF.POUND))).andReturn(evtl);
        actionSupport.fireEvent(CallXmlEvent.TERMDIGIT);
        replay(session, actionSupport);
        action.handleAction(session, actionSupport);
        verify(session, actionSupport);
    }

    @Test
    public void testHandleActionNoTermdigit() throws Exception {

        EventList evtl = EventListBuilder.single(DTMF.POUND);
        action.setTermdigits("");
        expect(actionSupport.getPath(VALUE)).andReturn(PROMPT);
        expect(actionSupport.proceed(evtl)).andReturn(Boolean.TRUE);
        expect(session.streamFile(PROMPT)).andReturn(evtl);
        replay(session, actionSupport);
        action.handleAction(session, actionSupport);
        verify(session, actionSupport);

    }
}
