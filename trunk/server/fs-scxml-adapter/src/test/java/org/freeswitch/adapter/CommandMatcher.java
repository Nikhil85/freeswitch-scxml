package org.freeswitch.adapter;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public final class CommandMatcher extends BaseMatcher<String> {

    private final Pattern appPattern;
    private String[] args;
    private Pattern argsPattern = Pattern.compile("^(execute-app-arg:)(\\s)(.*)$", Pattern.MULTILINE);
    private final String appName;

    private CommandMatcher(String appName) {
        this.appName = appName;
        appPattern = Pattern.compile("^(execute-app-name:)(\\s)(" + appName + ")$", Pattern.MULTILINE);
    }

    @Override
    public boolean matches(Object item) {
        if (args == null) {
            return hasApp(item);
        } else {
            return hasApp(item) && hasArgs(item);
        }
    }

    private boolean hasApp(Object item) {
        return appPattern.matcher((CharSequence) item).find();
    }

    private boolean hasArgs(Object item) {
        Matcher matcher = argsPattern.matcher((CharSequence) item);
        if (matcher.find()) {
            if (hasArgInLine(matcher.group(3))) {
                return false;
            }
            return true;
        } else {
            return false;
        }
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
        description.appendText(String.format("execute-app-name %s execute-app-args %s", appName, Arrays.toString(args)));
    }

    public static CommandMatcher appName(String appName) {
        return new CommandMatcher(appName);
    }

    public CommandMatcher args(String... args) {
        this.args = args;
        return this;
    }
}
