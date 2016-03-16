# wait #
Create a short pause.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |
| value         | null        | String        | The amount of time to wait                       |

## Usage ##

```
<sc:scxml  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
           xmlns:sc='http://www.w3.org/2005/07/scxml'
           xmlns:fs='http://www.freeswitch.org'
           xsi:schemaLocation='
           http://www.w3.org/2005/07/scxml  http://freeswitch-scxml.googlecode.com/files/scxml.xsd
           http://www.freeswitch.org http://freeswitch-scxml.googlecode.com/files/fs-core.xsd' initialstate="intro-main">
    <sc:state id="intro-main">
        <sc:onentry>
            <fs:answer />
            <fs:say>'Will wait 3 seconds'</fs:say>
            <fs:wait value="3s" />            
            <fs:say>'Bye'</fs:say>
            <fs:hangup />
        </sc:onentry>
    </sc:state>
</sc:scxml>
```

