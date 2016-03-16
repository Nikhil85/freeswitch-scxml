# recordaudio #
Record the user.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |
| beep          | true        | boolean       | To play a short beep before starting the recording or not|
| cleardigits   | true        | boolean       | Empty the queue of digits or not                 |
| maxtime       | 30s         | String        | The maximum length of the recording              |
| var           | null        | String        | The name of the variable to store the reference of the recording in |
| timevar       | null        | String        | The name of the variable to store the reference of the duration in |
| termdigits    | #           | String        | Digits when pressed should halt the recording    |

## Events ##
| **Name**      | **Description** |
|:--------------|:----------------|
| hangup        | The caller hung up the phone |
| error         | An error occurred      |
| termdigit     | A term digit was pressed |
| maxtime       | Maximum length of the recording was reached |

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
            <fs:answer /> 
            <fs:say>'Start your recording after the beep'</fs:say>
            <fs:recordaudio var="rec" beep="true" fs:termdigits="#" maxtime="10s" timevar="dur" />
        </sc:onentry>
        
        <sc:transition event="termdigit" target="exit"  >
            <fs:say>'You said'</fs:say>
            <fs:playaudio value="rec" />
            <fs:say>'Duration of the recording is ' + dur + ' seconds'</fs:say>
        </sc:transition>
        
        <sc:transition event="maxtime"  target="exit"  >
            <fs:say>'You said'</fs:say>
            <fs:playaudio value="rec" />
            <fs:say>'Duration of the recording is ' + dur + ' seconds'</fs:say>
        </sc:transition>
            
     </sc:state>    
        
    <sc:state id="exit" final="true">
        <sc:onentry>
            <fs:say>'bye'</fs:say>
            <fs:hangup />
        </sc:onentry>                
    </sc:state>
    
</sc:scxml>

```