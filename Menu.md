# Menu #

The menu tag is a great tag to use when you want your users
to be able to navigate from one state to the other, based
on the choices the user makes. It's a good convention to have
a choice that will let the user get back to the initial state.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |
| cleardigits   | true        | boolean       | To empty the digits queue before executing or not|
| maxtime       | 30s         | String        | How long to wait for input in ms s or m          |
| say           | null        | expression    | A sentence To speak using TTS                    |
| termdigits    | #           | String        | Sequence of digits thats should halt the execution|
| value         | null        | expression    | Path to a prompt that should be played           |
| choices       | null        | String        | Sequence of digits representing choices that the user can take|
| choicesexpr   | null        | expression    | Same as choices                                  |

## Events ##
| **Name**      | **Description** |
|:--------------|:----------------|
| hangup        | The caller hung up the phone |
| error         | An error occurred|
| maxtime       | Exceeded the max time |
| termdigit     | A term digit was pressed |
| choice:?      | Choice was made, ex choice:1 |
| nomatch       | No match was found |

## Usage ##

```
<sc:scxml  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
           xmlns:sc='http://www.w3.org/2005/07/scxml'
           xmlns:fs='http://www.freeswitch.org'
           xsi:schemaLocation=' http://www.w3.org/2005/07/scxml  http://freeswitch-scxml.googlecode.com/files/scxml.xsd http://www.freeswitch.org http://freeswitch-scxml.googlecode.com/files/fs-core.xsd' 
           initialstate="intro-main">
    
    <sc:state id="intro-main">
        <sc:onentry>
            <fs:answer/>
            <fs:menu fs:choices="12" fs:termdigits="#" maxtime="10s" fs:say="'enter one or two'" />
        </sc:onentry>
        
        <sc:transition event="choice:1" target="exit">
            <fs:say>'Choice one'</fs:say>
            <sc:log label="menu-test" expr="'Choice one'" />
        </sc:transition>
        
        <sc:transition event="choice:2" target="exit" >
            <fs:say>'Choice two'</fs:say>
            <sc:log label="menu-test" expr="'Choice two'" />
        </sc:transition>
        
        <sc:transition event="maxtime" target="intro-main" >
            <fs:say>'Max time'</fs:say>
        </sc:transition>

        <sc:transition event="termdigit" target="intro-main">
            <fs:say>'Termdigit'</fs:say>
        </sc:transition>

        <sc:transition event="nomatch" target="intro-main">
            <fs:say>'No match'</fs:say>
        </sc:transition>
       
    </sc:state>
    
    <sc:state id="exit" final="true">
        <sc:onentry>
            <fs:say>Bye</fs:say>
            <fs:hangup />
        </sc:onentry>                
    </sc:state>
    
</sc:scxml>

```
