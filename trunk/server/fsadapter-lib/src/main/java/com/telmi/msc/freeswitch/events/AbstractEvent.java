/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telmi.msc.freeswitch.events;

/**
 *
 * @author kristofer
 */
public abstract class AbstractEvent {
//Event-Name: CHANNEL_CREATE
//Core-UUID: 689fd828-e85b-ca43-a219-39332bc55860
//Event-Date-Local: 2007-05-09%2018%3A48%3A59
//Event-Date-GMT: Wed,%2009%20May%202007%2016%3A48%3A59%20GMT
//Event-Calling-File: switch_channel.c
//Event-Calling-Function: switch_channel_set_caller_profile
//Event-Calling-Line-Number: 840

    private final String eventName;
    private final String data;

    public AbstractEvent(final String eventName, final String data) {
        this.eventName = eventName;
        this.data = data;
    }

    public final String data() {
        return data;
    }


    public final String eventName() {
        return eventName;
    }
}
