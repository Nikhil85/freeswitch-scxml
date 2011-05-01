package org.freeswitch.adapter.api;

import java.util.Map;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class EventTest {

    /**
     * Test of getVar method, of class Event.
     */
    @Test
    public void testGetVar() {
        Event event = new Event(Event.CHANNEL_EXECUTE_COMPLETE, body());
        assertEquals("16000", event.getVar("Channel-Read-Codec-Rate"));
        assertEquals("G722", event.getVar("Channel-Write-Codec-Name"));
    }

    /**
     * Test of getVar method, of class Event.
     */
    @Test
    public void testGetVarLookupsAreCached() {
        Event event = new Event(Event.CHANNEL_EXECUTE_COMPLETE, body());
        assertEquals("16000", event.getVar("Channel-Read-Codec-Rate"));
        assertTrue(event.vars.containsKey("Channel-Read-Codec-Rate"));
    }

    /**
     * Test of getVar method, of class Event.
     */
    @Test
    public void testGetVarNotFound() {
        Event event = new Event(Event.CHANNEL_EXECUTE_COMPLETE, body());
        assertEquals(null, event.getVar("Not-Found"));
    }

    /**
     * Test of getBodyAsMap method, of class Event.
     */
    @Test
    public void testGetBodyAsMap() {
        Map<String, String> expected = new HashMap<>();
        expected.put("Answer-State", "answered");
        expected.put("Channel-Read-Codec-Name", "G722");
        expected.put("Channel-Read-Codec-Rate", "16000");
        expected.put("Channel-Write-Codec-Name", "G722");
        expected.put("Channel-Write-Codec-Rate", "16000");
        expected.put("Caller-Username", "1006");
        expected.put("Caller-Dialplan", "XML");
        assertEquals(expected, new Event(Event.CHANNEL_EXECUTE_COMPLETE, body()).getBodyAsMap());
    }

    /**
     * Test of equals method, of class Event.
     */
    @Test
    public void testEquals() {
        assertEquals(new Event(Event.CHANNEL_CREATE), new Event(Event.CHANNEL_CREATE));
        assertEquals(new Event(Event.CHANNEL_CREATE, body()), new Event(Event.CHANNEL_CREATE, body()));
    }

    /**
     * Test of toString method, of class Event.
     */
    @Test
    public void testToString() {
        assertNotNull(new Event(Event.CHANNEL_DATA));
    }

    public String body() {
        return "Answer-State: answered\n"
                + "Channel-Read-Codec-Name: G722\n"
                + "Channel-Read-Codec-Rate: 16000\n"
                + "Channel-Write-Codec-Name: G722\n"
                + "Channel-Write-Codec-Rate: 16000\n"
                + "Caller-Username: 1006\n"
                + "Caller-Dialplan: XML\n";
    }
}
