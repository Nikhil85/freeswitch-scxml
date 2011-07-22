package org.freeswitch.scxml.engine;

/**
 *
 * Events taken from Call xml.
 *
 * <pre>
 *  {@code
 *    <on event ="answer"/>
 *    <on event ="callfailure"/>
 *    <on event ="error"/>
 *    <on event ="externalevent:user_event_name"/>
 *    <on event ="hangup"/>
 *    <on event ="maxdigits"/>
 *    <on event ="maxtime"/>
 *    <on event ="maxsilence"/>
 *    <on event ="user_event_name"/>
 *    <on event ="choice:grammar_match"/>
 *    <on event ="choice:nomatch"/>
 *    <on event ="termdigit:#"/>
 *    <on event ="cparesult:human"/>
 * }
 * </pre>
 *
 * @author jocke
 */
public enum CallXmlEvent {

    /** A un specified error. **/
    ERROR("error"),
    /** The call has been answered. **/
    ANSWER("answer"),
    /** The call has ended. **/
    HANGUP("hangup"),
    /** Caller has entered maximum digits. **/
    MAXDIGITS("maxdigits"),
    /** An action has timed out. **/
    MAXTIME("maxtime"),
    /** The caller entered a choice. **/
    CHOICE("choice"),
    /** Action ended with a term digit. **/
    TERMDIGIT("termdigit"),
    /** The entered digit has no match. **/
    NOMATCH("nomatch"),
    /** The entered digit has match. **/
    MATCH("match"),
    /** The caller entered to few digits. **/
    MINDIGITS("mindigits"),
    /** The dialed number is busy **/
    BUSY("busy"),
    
    /** The dialed did not answer the call**/
    NOANSWER("noanswer"),
    
    /**Two calls has been bridged together **/
    BRIDGED("bridged");
    
    private String eventString;

    /**
     * Create a new CallXml event with a name.
     *
     * @param event The name of the event.
     */
    private CallXmlEvent(String event) {
        this.eventString = event;
    }

    @Override
    public String toString() {
        return eventString;
    }
}
