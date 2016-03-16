# inputdigits #
The inputdigits action is useful for collecting phone numbers, pins and passwords.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |
| cleardigits   | true        | boolean       | To empty the queue before collecting or not      |
| includetermdigit | false       | boolean       | To remove the last digit if it is a terminator or not|
| maxdigits     | 16          | int           | Max number of digits to collect                  |
| mindigits     | 1           | int           | The minimum amount of digits to collect          |
| maxtime       | 30s         | String        | The max time to wait for the user to enter digits|
| termdigits    | #           | String        | List of digits that will stop the collecting of digits|
| var           | null        | String        | The reference to the digits in the context       |
| value         | null        | expression    | A path to a prompt to play                       |
| say           | null        | expression    | A phrase to speak using TTS                      |

## Events ##
| **Name**      | **Description**                |
|:--------------|:-------------------------------|
| hangup        | The caller hung up the phone   |
| error         | An error occurred              |
| maxdigits     | More digits was entered then allowed |
| maxtime       | termdigits nor max digits was collected before the max time given |
| termdigit     | A term digit was pressed       |
| mindigits     | too few digits was collected   |

## Usage ##

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
            <fs:answer/>
            <fs:inputdigits var="digits" maxdigits="5" termdigits="#" maxtime="10s" mindigits="2" say="'Enter some digits'" />
        </sc:onentry>
       
        <sc:transition event="maxdigits" target="exit">
            <fs:say>'maxdigits ' + digits</fs:say>
        </sc:transition>
       
        <sc:transition event="maxtime" target="exit">
            <fs:say>'maxtime'</fs:say>
        </sc:transition>

        <sc:transition event="mindigits" target="exit">
            <fs:say>'mindigits ' + digits</fs:say>
        </sc:transition>
        
        <sc:transition event="termdigit" target="exit">
            <fs:say>'termdigit collected ' + digits</fs:say>
        </sc:transition>

        <sc:transition event="hangup" />
       
    </sc:state>
   
    <sc:state id="exit" final="true">
        <sc:onentry>
            <fs:say>Bye</fs:say>
            <fs:hangup />
        </sc:onentry>                
    </sc:state>
</sc:scxml>

```
