package org.freeswitch.adapter.internal.session;

import org.freeswitch.adapter.api.constant.Q850HangupCauses;

/**
 * 
 * @author jocke
 */
public final class Command {

    private String base;

    public Command(String uid) {
        StringBuilder builder = new StringBuilder();
        builder.append("sendmsg ").append(uid == null ? "" : uid).append("\n");
        builder.append("call-command: execute\n");
        builder.append("execute-app-name: ");
        this.base = builder.toString();
    }

    public String answer() {
        return noArgCommand("answer");
    }

    public String hangup(Q850HangupCauses cause) {
        if (cause == null) {
            return noArgCommand("hangup");
        } else {
            return argCommand("hangup", Integer.toString(cause.ituQ850Code()));
        }
    }

    public String breakcommand() {
        return noArgCommand("break");
    }

    public String speak(String argstring) {
        return argCommand("speak", argstring);
    }

    public String say(String moduleName, String type, String method, String value) {
        return argCommand("say", moduleName, type, method, "'" + value + "'");
    }

    public String record(String dstFileName, Integer millis, Integer silenseThresh, Integer silenceHits) {
        return argCommand("record", dstFileName, millis == null ? null : millis / 1000, silenseThresh, silenceHits);
    }

    public String set(String apparg) {
        return argCommand("set", apparg);
    }

    public String refer(String apparg) {
        return argCommand("deflect", apparg);
    }

    public String playback(String url) {
        return argCommand("playback", url);
    }

    public String noArgCommand(String command) {
        return new StringBuilder(base).append(command).append("\n\n").toString();
    }

    public String argCommand(String command, Object... args) {
        StringBuilder builder = new StringBuilder(base).append(command).append("\n").append("execute-app-arg: ");
        int index = 0;
        for (Object arg : args) {
            index++;
            if (arg == null) {
                continue;
            }
            builder.append(arg);
            if (index < args.length) {
                builder.append(" ");
            }
        }
        return builder.append("\n\n").toString();
    }
}
