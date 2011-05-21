package org.freeswitch.adapter;

/**
 * 
 * @author jocke
 */
public final class Command {

    private String base;

    public Command(String uid) {
        StringBuilder builder = new StringBuilder();
        builder.append("sendmsg ").append(uid).append("\n");
        builder.append("call-command: execute\n");
        builder.append("execute-app-name: ");
        this.base = builder.toString();
    }

    public static String answer() {
        return "sendmsg\ncall-command: execute\nexecute-app-name: answer\n\n";
    }

    public String hangup(Q850HangupCauses cause) {

        StringBuilder sb = new StringBuilder(base);
        sb.append("hangup");

        if (cause != null) {
            sb.append("\nexecute-app-arg: ").append(cause);
        }

        sb.append("\n\n");
        return sb.toString();

    }

    public String breakcommand() {
        return base + "break\n\n";
    }

    public String speak(String argstring, boolean eventlock) {

        StringBuilder sb = new StringBuilder(base);
        sb.append("speak\n");
        sb.append("execute-app-arg: ").append(argstring);

        if (eventlock) {
            sb.append("\nevent-lock:true");
        }

        sb.append("\n\n");
        return sb.toString();
    }

    public String say(String moduleName, String type, String method, String value) {
        return String.format(
                base + "say\nexecute-app-arg: %s %s %s '%s'\n\n",
                moduleName,
                type,
                method,
                value);
    }

    public String record(String dstFileName, Integer millis, Integer silenseThresh, Integer silenceHits, boolean eventlock) {
        StringBuilder sb = new StringBuilder(base);

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

    public String set(String apparg) {
        return String.format(base + " set\nexecute-app-arg: %s\n\n", apparg);
    }

    public String refer(String apparg) {
        return String.format(base + " deflect\nexecute-app-arg: %s\n\n", apparg);
    }

    public String playback(String url, boolean eventlock) {

        StringBuilder sb = new StringBuilder(base);
        sb.append("playback\nexecute-app-arg: ").append(url);

        if (eventlock) {
            sb.append("\nevent-lock:true");
        }

        sb.append("\n\n");
        return sb.toString();
    }
}
