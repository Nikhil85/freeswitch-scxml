package org.freeswitch.scxml.test;

import org.freeswitch.adapter.api.constant.DTMF;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;
import org.freeswitch.adapter.api.event.Event;
import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.IBlockingConnection;
import static org.junit.Assert.*;

/**
 *
 * @author jocke
 */
public class MockConnection {

    public static final String HANGUP = "hangup";
    public static final String ANSWER = "answer";
    public static final String PLAYBACK = "playback";
    public static final String SPEAK = "speak";
    public static final String SAY = "say";
    public static final String RECORD = "record";
    public static final String BREAK = "break";
    public static final String ORIGINATE = "originate";
    public static final String BRIDGE = "uuid_bridge";
    
    private static final String DLM = "\n\n";
    private static final Pattern APP_PATTERN = Pattern.compile("^(execute-app-name:)(\\s)(\\w*)$", Pattern.MULTILINE);
    private static final Pattern ARGS_PATTERN = Pattern.compile("^(execute-app-arg:)(\\s)(.*)$?", Pattern.MULTILINE);
    private final String uid;
    private IBlockingConnection ibc;

    public MockConnection(int port) throws IOException {
        ibc = new BlockingConnection("localhost", port);
        this.uid = UUID.randomUUID().toString();
    }

    public MockConnection connect() throws IOException {
        assertTrue(ibc.readStringByDelimiter(DLM).equals("connect"));
        assertTrue(ibc.readStringByDelimiter(DLM).equals("myevents"));
        assertTrue(ibc.readStringByDelimiter(DLM).contains("filter Event-Name"));
        assertTrue(ibc.readStringByDelimiter(DLM).contains("filter Event-Name"));
        return this;
    }

    public void close() throws IOException {
        ibc.close();
    }

    public void fireEvent(String event, Map<String, String> vars) throws IOException {
        ibc.write(createEvent(event, vars));
        ibc.flush();
    }

    public void fireEvent(DTMF dtmf) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("DTMF-Digit", dtmf.toString());
        fireEvent(Event.DTMF, data);
    }

    public Reply expectApp(String application) throws IOException {
        String appCommand = ibc.readStringByDelimiter(DLM);
        assertEquals(application, getApp(appCommand));
        return new Reply(application);
    }

    public Reply expectApp(String application, String args) throws IOException {
        String appCommand = ibc.readStringByDelimiter(DLM);
        assertEquals(application, getApp(appCommand));
        assertEquals("Not same args ", args, getArgs(appCommand));
        return new Reply(application);
    }

    private String getApp(String appCommand) {
        Matcher matcher = APP_PATTERN.matcher(appCommand);

        if (matcher.find()) {
            return matcher.group(3);

        } else {
            return null;
        }
    }

    private String getArgs(String appCommand) {
        Matcher matcher = ARGS_PATTERN.matcher(appCommand);

        if (matcher.find()) {
            return matcher.group(3);

        } else {
            return null;
        }
    }

    private String createEvent(String event, Map<String, String> vars) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append("Event-Name: ").append(event).append("\n");
        vars.put("Channel-Unique-ID", uid);
        vars.put("Unique-ID", uid);
        for (Map.Entry<String, String> en : vars.entrySet()) {
            builder.append(en.getKey()).append(": ").append(URLEncoder.encode(en.getValue(), "UTF-8")).append("\n");
        }

        builder.append(DLM);
        return builder.toString();
    }

    public String getUid() {
        return uid;
    }

    public Reply expectApi(String apiCommand) throws IOException {
        assertTrue(ibc.readStringByDelimiter(DLM).contains(apiCommand));
        return new Reply() ;
    }

    public final class Reply {

        private String app;

        public Reply() {
        }
        
        public Reply(String app) {
            this.app = app;
        }

        public void andReply(String event) throws IOException {
            Map<String, String> data = new HashMap<>();
            puIfAbsent(data);
            ibc.write(createEvent(event, data));
        }
        
        public void andApiReply(String body) throws IOException {
            StringBuilder builder = new StringBuilder();
            builder.append("Content-Type: api/response").append("\n");
            builder.append("Content-Length: ").append(body.getBytes().length).append(DLM);
            builder.append(body);
            ibc.write(builder.toString());
        }

        public void andReply(String event, Map<String, String> data) throws IOException {
            puIfAbsent(data);
            ibc.write(createEvent(event, data));
        }

        private void puIfAbsent(Map<String, String> data) {
            if (!data.containsKey(app)) {
                data.put("Application", app);
            }
        }
    }
}
