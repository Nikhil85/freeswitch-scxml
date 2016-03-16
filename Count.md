# The count variable #

One common requirement is that you have to take different
actions based on how many times an event has happened in a
state. That is what the implicit count variable is there for.
The count variable when evaluated on a transition having an event
will evaluate to the number of times that event has happened ex.

```

    <sc:scxml  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
           xmlns:sc="http://www.w3.org/2005/07/scxml"
           xmlns:fs="http://www.freeswitch.org"
           xsi:schemaLocation="http://www.w3.org/2005/07/scxml 
           scxml.xsd
           http://www.freeswitch.org 
           fs-core.xsd"
           initialstate="intro-main">

    <sc:state id="intro-main">
        <sc:onentry>
            <sc:log label="count-test" expr="count" />
            <fs:menu fs:choices="12" fs:termdigits="#" maxtime="10s" fs:say="'enter one or two'" />
        </sc:onentry>

        <sc:transition event="choice:1" target="exit" >
            <fs:say>'Choice one'</fs:say>
            <sc:log label="menu-test" expr="'Choice one'" />
        </sc:transition>

        <sc:transition event="choice:2" target="exit" >
            <fs:say>'Choice two'</fs:say>
        </sc:transition>

        <sc:transition event="maxtime" target="intro-main" cond="count lt 3" >
            <fs:say>'Max time'</fs:say>
        </sc:transition>

        <sc:transition event="maxtime" target="exit" cond="count eq 3" >
            <fs:say>'Max time last try bye'</fs:say>
        </sc:transition>

        <sc:transition event="termdigit" target="intro-main" cond="count lt 3">
            <fs:say>'Termdigit count lt 3'</fs:say>
        </sc:transition>

        <sc:transition event="termdigit" cond="count eq 3" target="exit">
            <fs:say>'Termdigit count eq 3'</fs:say>
        </sc:transition>

        <sc:transition event="nomatch" target="intro-main" cond="count lt 3">
            <fs:say>'No match'</fs:say>
        </sc:transition>     
        <sc:transition event="nomatch" target="exit" cond="count eq 3">
            <fs:say>'No match last try bye'</fs:say>
        </sc:transition>     

    </sc:state>

    <sc:state id="exit" final="true">
        <sc:onentry>
            <fs:hangup />
        </sc:onentry>                
    </sc:state>

    </sc:scxml>

```

The above document will start in the intro-main state and
when entering that state it will say 'enter one or two'.
If one or two is pressed it will transition to the exit state.
If termdigit, maxtime or nomatch happens and count evaluates to less then 3, it will go back to the intro-main state.
If termdigit, maxtime or nomatch happens and count evaluates to 3, then will it go to the exit state.

The count variable supports all events even your own, remember that all event counts
are reseted when leaving a state.