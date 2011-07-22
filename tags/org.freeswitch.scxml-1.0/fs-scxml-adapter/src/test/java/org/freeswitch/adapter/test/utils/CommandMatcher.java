package org.freeswitch.adapter.test.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public final class CommandMatcher extends BaseMatcher<String> {

    private final Pattern appPattern;
    private String[] args;
    private static final Pattern ARGS_PATTERN = Pattern.compile("^(execute-app-arg:)(\\s)(.*)$", Pattern.MULTILINE);
    private Pattern uidPattern = Pattern.compile("^(sendmsg)(\\s)()$", Pattern.MULTILINE);
    private final String appName;
    private final String uid;

    private CommandMatcher(String appName, String uid) {
        this.appName = appName;
        this.uid = uid;
        this.appPattern = Pattern.compile("^(execute-app-name:)(\\s)(" + appName + ")$", Pattern.MULTILINE);
        this.uidPattern = Pattern.compile("^(sendmsg)(\\s)(" + uid + ")$", Pattern.MULTILINE);
    }

    @Override
    public boolean matches(Object item) {
        if (args == null) {
            return hasApp(item) && hasUid(item);
        } else {
            return hasApp(item) && hasArgs(item) && hasUid(item);
        }
    }

    private boolean hasApp(Object item) {
        return appPattern.matcher((CharSequence) item).find();
    }

    private boolean hasArgs(Object item) {
        Matcher matcher = ARGS_PATTERN.matcher((CharSequence) item);
        if (matcher.find()) {
            if (hasArgInLine(matcher.group(3))) {
                return false;
            }
            return true;
        } else {
            System.out.println("Args not found in " +  item);
            return false;
        }
    }

    private boolean hasUid(Object item) {
        return uidPattern.matcher((CharSequence) item).find();
    }

    private boolean hasArgInLine(String argLine) {
        for (int i = 0; i < args.length; i++) {
            if (!argLine.contains(args[i])) {
                return true;
            }
        }
   
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("uid %s execute-app-name %s execute-app-arg %s",uid ,appName,  Arrays.toString(args)));
    }

    public static CommandMatcher appName(String appName, String uid) {
        return new CommandMatcher(appName, uid);
    }

    public CommandMatcher args(String... args) {
        this.args = args;
        return this;
    }
}
