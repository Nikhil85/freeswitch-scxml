# Gentone #
The gentone element is not that useful at the moment, but it will generate busy or ringtone to the caller.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |
| value         | null        | String        | ring or busy                                     |
| repeat        | 0           | int           | Number of tones                                  |

## Events ##
| **Name**      | **Description** |
|:--------------|:----------------|
| hangup        | The caller hung up the phone |
| error         | An error occurred      |

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
        </sc:onentry>
        <sc:transition target="exit">
            <fs:gentone  value="ring" repeat="10"/>
            <fs:gentone value="busy" repeat="10" />
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
