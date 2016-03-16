# playaudio #
Play an audio file.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |
| value         | null        | String        | Path to a sound file, could be relative to the document |
| termdigits    | #           | String        | Digits when pressed should make the prompt stop playing|

## Events ##
| **Name**      | **Description** |
|:--------------|:----------------|
| hangup        | The caller hung up the phone |
| error         | An error occurred      |
| termdigit     | A term digit was pressed |

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
            <fs:playaudio value="'welcome.wav'" termdigits="#9" />
        </sc:onentry>
        
        <sc:transition target="exit" />
        
     </sc:state>    
        
    <sc:state id="exit" final="true">
        <sc:onentry>
            <fs:playaudio value="'goodbye.wav'"/>
            <fs:hangup />
        </sc:onentry>                
    </sc:state>
    
</sc:scxml>
```