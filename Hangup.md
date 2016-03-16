# Hangup #
Hangup the call.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |


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
        </sc:onentry>
        <sc:transition target="exit" />
    </sc:state>
    
    <sc:state id="exit" final="true">
        <sc:onentry>
            <fs:hangup />
        </sc:onentry>                
    </sc:state>
    
</sc:scxml>

```

