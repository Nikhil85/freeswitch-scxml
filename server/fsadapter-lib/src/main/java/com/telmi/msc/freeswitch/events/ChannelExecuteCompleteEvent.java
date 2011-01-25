/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telmi.msc.freeswitch.events;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kristofer
 */
public class ChannelExecuteCompleteEvent extends GenericChannelEvent {

    private static final Pattern APPLICATION_PATTERN = Pattern.compile("Application:\\s(.*)", Pattern.MULTILINE);
    private static final Pattern APPLICATION_DATA_PATTERN = Pattern.compile("Application-Data:\\s(.*)", Pattern.MULTILINE);
    private static final Pattern APPLICATION_RESPONSE_PATTERN = Pattern.compile("Application-Response:\\s(.*)", Pattern.MULTILINE);
    private final String application;
    private final String applicationData;
    private final String applicationResponse;

    public ChannelExecuteCompleteEvent(final String name, final String data)  throws NoSuchEventException {
        super(name, data);
        application = value(APPLICATION_PATTERN, data);
        applicationData = value(APPLICATION_DATA_PATTERN, data);
        applicationResponse = value(APPLICATION_RESPONSE_PATTERN, data);
    }

    private String value(final Pattern pattern, final String data) throws NoSuchEventException {
        String tmp = null;
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            tmp = matcher.group(1);
        } 
        return tmp;
    }

    public String getApplication() {
        return application;
    }

    public String getApplicationData() {
        return applicationData;
    }

    public String getApplicationResponse() {
        return applicationResponse;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("[application=").append(application);
        sb.append(",application-data=").append(applicationData);
        sb.append(",application-response=").append(applicationResponse);
        sb.append("]");
        return sb.toString();
    }
}
