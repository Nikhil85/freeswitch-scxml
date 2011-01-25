/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telmi.msc.freeswitch.events;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author kristofer
 */
public class FSEventFactoryTest {

    String testdata = "Event-Name: %s\n"
            + "Core-UUID: 0340640046-2356025602364-46356235097-13465\n"
            + "Event-Date-Local: 2010-07-20 10:11:00\n"
            + "Event-Date-GMT: 2010-07-20 08:11:00 GMT\n"
            + "Event-Calling-File: mod_event_socket.c\n"
            + "Event-Calling-Function: parse_command\n"
            + "Event-calling-Line-Number: 0\n"
            + "%s" // to be able to add event specific data in tests. i.e DTMF
            + "\n\n";

    @Test
    public void dummy() {
        Assert.assertTrue(true);
    }

    @Test(expected = NoSuchEventException.class)
    public void noSuchEventException() throws NoSuchEventException {
        String data = String.format(testdata, "INVALID_EVENT_NAME", "");
        String body = null;
        FSEventFactory.getFSEvent(data);
    }

    @Test
    public void testDTMFEvent() throws NoSuchEventException {
        String data = String.format(testdata, "DTMF", "DTMF-Digit: 1\nDTMF-Duration: 2000\n");
        String body = null;
        AbstractEvent event = FSEventFactory.getFSEvent(data);
        Assert.assertEquals(DTMFEvent.class.getCanonicalName(), event.getClass().getCanonicalName());

        DTMFEvent dtmf = null;
        if (event instanceof DTMFEvent) {
            dtmf = (DTMFEvent) event;
        }
        Assert.assertNotNull(dtmf);
        Assert.assertEquals('1', dtmf.digit());
        Assert.assertEquals(2000L, dtmf.duration());
    }

    @Test
    public void testDTMFEventHASH() throws NoSuchEventException {
        String data = String.format(testdata, "DTMF", "DTMF-Digit: %23\nDTMF-Duration: 800\n");
        String body = null;
        AbstractEvent event = FSEventFactory.getFSEvent(data);
        Assert.assertEquals(DTMFEvent.class.getCanonicalName(), event.getClass().getCanonicalName());

        DTMFEvent dtmf = null;
        if (event instanceof DTMFEvent) {
            dtmf = (DTMFEvent) event;
        }
        Assert.assertNotNull(dtmf);
        Assert.assertEquals('#', dtmf.digit());
        Assert.assertEquals(800L, dtmf.duration());
    }

    @Test(expected = NoSuchEventException.class)
    public void testDTMFEvent_withNoValidDigit() throws NoSuchEventException {
        String data = String.format(testdata, "DTMF", "DTMF-Digit: %\nDTMF-Duration: 800\n");
        String body = null;
        AbstractEvent event = FSEventFactory.getFSEvent(data);
    }

    @Test(expected = NoSuchEventException.class)
    public void testDTMFEvent_withNoDigit() throws NoSuchEventException {
        String data = String.format(testdata, "DTMF", "DTMF-Digit: \nDTMF-Duration: 2000\n");
        String body = null;
        FSEventFactory.getFSEvent(data);
    }

    @Test(expected = NoSuchEventException.class)
    public void testDTMFEvent_withNoDuration() throws NoSuchEventException {
        String data = String.format(testdata, "DTMF", "DTMF-Digit: 1\nDTMF-Duration:\n");
        String body = null;
        FSEventFactory.getFSEvent(data);
    }

    @Test
    public void testChannelExecuteCompleteEvent() throws NoSuchEventException {
        String extra = ""
                + "Application: playback\n"
                + "Application-Data: /vmms/prompts/vm-welcome.wav\n"
                + "Application-Response: FILE%20PLAYED";

        String data = String.format(testdata, "CHANNEL_EXECUTE_COMPLETE", extra);
        AbstractEvent event = FSEventFactory.getFSEvent(data);

        Assert.assertEquals(ChannelExecuteCompleteEvent.class.getCanonicalName(), event.getClass().getCanonicalName());

        ChannelExecuteCompleteEvent cecevent = null;
        if (event instanceof ChannelExecuteCompleteEvent) {
            cecevent = (ChannelExecuteCompleteEvent) event;
        }
        Assert.assertNotNull(cecevent);
        Assert.assertEquals("playback", cecevent.getApplication());
        Assert.assertEquals("/vmms/prompts/vm-welcome.wav", cecevent.getApplicationData());
        Assert.assertEquals("FILE%20PLAYED", cecevent.getApplicationResponse());
    }


}
