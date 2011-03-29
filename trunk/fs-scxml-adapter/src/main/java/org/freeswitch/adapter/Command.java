package org.freeswitch.adapter;

/**
 * http://wiki.freeswitch.org/wiki/Category:Modules
 * 
 * @author jocke
 */
public final class Command {

    /**
     * Utility class should have no public constructor.
     */
    private Command() {
    }
    private static final String BASE_COMMAND = "sendmsg\n" + "call-command: execute\n" + "execute-app-name: ";

    public static String answer() {
        return "sendmsg\ncall-command: execute\nexecute-app-name: answer\n\n";
    }

    public static String hangup(Q850HangupCauses cause) {

        StringBuilder sb = new StringBuilder(BASE_COMMAND);
        sb.append("hangup");

        if (cause != null) {
            sb.append("\nexecute-app-arg: ").append(cause);
        }

        sb.append("\n\n");
        return sb.toString();

    }

    static String breakcommand() {
        return "sendmsg\ncall-command: execute\nexecute-app-name: break\n\n";
    }

    static String speak(String argstring, boolean eventlock) {

        StringBuilder sb = new StringBuilder(BASE_COMMAND);
        sb.append("speak\nexecute-app-arg: ").append(argstring);

        if (eventlock) {
            sb.append("\nevent-lock:true");
        }

        sb.append("\n\n");
        return sb.toString();
    }

    static String say(String moduleName, String type, String method, String value) {
        return String.format(
                BASE_COMMAND + " say\nexecute-app-arg: %s %s %s '%s'\n\n",
                moduleName,
                type,
                method,
                value);
    }

    static String record(String dstFileName, Integer millis, Integer silenseThresh, Integer silenceHits, boolean eventlock) {
        StringBuilder sb = new StringBuilder(BASE_COMMAND);
        sb.append("record\n").append("execute-app-arg: ").append(dstFileName);

        if (millis != null) {
            sb.append(" ").append(millis / 1000);
        }

        if (silenseThresh != null) {
            sb.append(" ").append(silenseThresh);
        }

        if (silenceHits != null) {
            sb.append(" ").append(silenceHits);
        }

        if (eventlock) {
            sb.append("\nevent-lock:true");
        }

        sb.append("\n\n");

        return sb.toString();
    }

    static String set(String apparg) {
        return String.format(BASE_COMMAND + " set\nexecute-app-arg: %s\n\n", apparg);
    }

    static String refer(String apparg) {
        return String.format(BASE_COMMAND + " deflect\nexecute-app-arg: %s\n\n", apparg);
    }

    public static String playback(String url, boolean eventlock) {

        StringBuilder sb = new StringBuilder(BASE_COMMAND);
        sb.append("playback\nexecute-app-arg: ").append(url);

        if (eventlock) {
            sb.append("\nevent-lock:true");
        }

        sb.append("\n\n");
        return sb.toString();
    }
}
