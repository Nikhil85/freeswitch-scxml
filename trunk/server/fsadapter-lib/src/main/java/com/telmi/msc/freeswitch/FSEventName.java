package com.telmi.msc.freeswitch;

/**
 * http://wiki.freeswitch.org/wiki/Event_list
 * http://wiki.freeswitch.org/wiki/Event_Socket
 *
 *
 * The minimum amount of information sent for an event are:
 * <pre>
 * Event-Name: {FSChannelEvent}
 * Core-UUID: 689fd828-e85b-ca43-a219-39332bc55860
 * Event-Date-Local: 2007-05-09%2018%3A48%3A59
 * Event-Date-GMT: Wed,%2009%20May%202007%2016%3A48%3A59%20GMT
 * Event-Calling-File: switch_channel.c
 * Event-Calling-Function: switch_channel_set_caller_profile
 * Event-Calling-Line-Number: 840
 * </pre>
 *
 * @author jocke
 */
public enum FSEventName {

    //
    // Channel Events
    //

   
    CHANNEL_EXECUTE_COMPLETE,
    
    CHANNEL_CREATE,
    /**
     * CHANNEL_DESTROY.
     * Called when a channel should get destroyed.
     */
    CHANNEL_DESTROY,
    /** The channel hangup. **/
    CHANNEL_HANGUP,
    /** Some times this messages gets through the filter. **/
    CHANNEL_PARK,
    /** The action was stopped because of a term digit. **/

    //
    // System Events
    //

    TERMDIGIT,
    /** The action timed out. **/
    TIMEOUT,
    /** The channel found a DTMF. **/
    DTMF,
    /** Channel found error. **/
    ERROR,
    /** Channel data event **/
    CHANNEL_DATA
}
