package org.freeswitch.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://wiki.freeswitch.org/wiki/Category:Modules
 * @author jocke
 */
public final class Command {

    private static final Logger LOG = LoggerFactory.getLogger(SessionImpl.class);

    /**
     * Utility class should have no public constructor.
     */
    private Command() {
    }
    private static final String BASE_COMMAND =
            "sendmsg\n"
            + "call-command: execute\n"
            + "execute-app-name: ";

    /**
     * Answers an incoming call or session.
     * @return
     */
    public static String answer() {
        return "sendmsg\ncall-command: execute\nexecute-app-name: answer\n\n";
    }

    /**
     * Hangs up a channel, with an optional reason supplied.
     *
     * sendmsg
     * call-command: execute
     * execute-app-name: hangup
     * [execute-app-arg: {reason}]
     * 
     * @return
     */
    public static String hangup(Q850HangupCauses cause) {
        StringBuilder sb = new StringBuilder(BASE_COMMAND);
        sb.append("hangup");
        if (cause != null) {
            sb.append("\nexecute-app-arg: ").append(cause);
        }
        sb.append("\n\n");
        return sb.toString();

    }

    /**
     * 
     * 
     * Cancels currently running application on the given UUID.
     * Dialplan execution proceeds to the next application.
     * Optionally clears all unprocessed events (queued applications) on the channel.
     *
     * Note: Currently you can't break an endless_playback.
     * @see {@link http://wiki.freeswitch.org/wiki/Misc._Dialplan_Tools_break}
     */
    static String breakcommand() {
        return "sendmsg\ncall-command: execute\nexecute-app-name: break\n\n";
    }

    /**
     * Speaks a string or file of text to the channel
     * using the defined speech engine.
     *
     * {@link http://wiki.freeswitch.org/wiki/Misc._Dialplan_Tools_speak}
     * @param argstring
     * @return
     */
    static String speak(String argstring, boolean eventlock) {
        // SPEAK = BASE_COMMAND + "speak\nexecute-app-arg: %s\n\n";
        StringBuilder sb = new StringBuilder(BASE_COMMAND);
        sb.append("speak\nexecute-app-arg: ").append(argstring);
        if (eventlock) {
            sb.append("\nevent-lock:true");
        }
        sb.append("\n\n");
        return sb.toString();
    }


    /**
     * he say application will use the pre-recorded sound files to read or say
     * various things like dates, times, digits, etc.
     * The say application can read digits and numbers as well as dollar
     * amounts, date/time values and IP addresses. It can also spell out
     * alpha-numeric text, including punctuation marks.
     * 
     * @param moduleName
     * @param type
     * @param method
     * @param value
     * @return
     */
    static String say(String moduleName, String type, String method, String value) {
        return String.format(
                "sendmsg\ncall-command: execute\nexecute-app-name: say\nexecute-app-arg: %s %s %s '%s'\n\n",
                moduleName,
                type,
                method,
                value);
    }

    /**
     * <pre>
     * record,Record File,<path> [<time_limit_secs>] [<silence_thresh>] [<silence_hits>]
     * </pre>
     *
     * Record is used for recording messages, like in a voicemail system.
     * This application will record a file to <path>
     * <time_limit_secs> (optional) is the maximum duration of the recording.
     * <silence_thresh> (optional) is the energy level.
     * <silence_hits> (optional) hits is how many positive hits on being below
     * that thresh you can tolerate to stop. default hits are
     * sample rate * 3 / the number of samples per frame
     * so the default, if missing, is 3.
     *
     * Record doesn't set any variables in the dialplan
     * and sets no record status.
     *
     * @param dstFileName
     * @param millis
     * @return
     */
    static String record(String dstFileName, Integer millis, Integer silenseThresh, Integer silenceHits, boolean eventlock) {
        // RECORD = BASE_COMMAND + "record\nexecute-app-arg: %s %s\n\n";
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

//    static final String EXIT = BASE_COMMAND + "set\nexecute-app-arg: %s\n\n";
//    static String exit(String apparg) {
//
//        return String.format("sendmsg\ncall-command: execute\nexecute-app-name: say\nexecute-app-arg: %s %s %s '%s'\n\n",
//                FSCommand.EXIT, EVENT_MAP + nameList.toString()));
//
//
//
//    }
//    static final String SET = BASE_COMMAND + "set\nexecute-app-arg: %s\n\n";
    /**
     * Set a channel variable for the channel calling the application.
     * http://wiki.freeswitch.org/wiki/Misc._Dialplan_Tools_set
     * 
     * @param apparg
     * @return
     */
    static String set(String apparg) {
        return String.format(
                "sendmsg\ncall-command: execute\nexecute-app-name: set\nexecute-app-arg: %s\n\n",
                apparg);
    }

    static String refer(String apparg) {
        return String.format(
                "sendmsg\ncall-command: execute\nexecute-app-name: deflect\nexecute-app-arg: %s\n\n",
                apparg);

    }

    /**
     * silence_stream://<ms>[,<noise level>]
     * <pre>
     * silence_stream://10000
     * will generate 10 sec of absolute zeros
     *
     * silence_stream://10000,2000
     * will generate comfort noise for 10 seconds.
     * </pre>
     *
     * the noise level actually gets louder the closer it gets to zero so 2000 is softer than 1000 etc
     *
     * tone_stream://%(A, B, C [, D] [, E] [, F] [, G], [, H])
     * A = on (ms)
     * B = off (ms)
     * C = frequency 1 (Hz)
     * optional:
     * D = frequency 2 (Hz)
     * E = frequency 3 (Hz)
     * F = frequency 4 (Hz)
     * G = frequency 5 (Hz)
     * H = frequency 6 (Hz)
     * @param url
     * @return
     */
    public static String playback(String url, boolean eventlock) {
        StringBuilder sb = new StringBuilder(BASE_COMMAND);
        sb.append("playback\nexecute-app-arg: ").append(url);
        if (eventlock) {
            sb.append("\nevent-lock:true");
        }
        sb.append("\n\n");
        return sb.toString();
        //String.format(BASE_PLAYBACK, url);
    }
//    private static final String BASE_PLAYBACK = BASE_COMMAND + "playback\nexecute-app-arg: %s\n\n";
}
