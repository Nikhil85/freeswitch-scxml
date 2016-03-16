# Exit #
The exit tag is like the hangup tag, unlike the hangup tag the exit
tag will let you pass a reason phrase that will be seen in the SIP message.
If this dialog is started by a server that can read sip headers, then there is also
a way to pass variables back to that server. This is useful if you using this FreeSwitch
as an media server and want to handle call control by an other server for instance.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |
| expr          | null        | String        | Reason phrase to use                             |
| namelist      | null        | String        | A list of variable names to pass back            |


## Usage ##

```
<sc:scxml  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
           xmlns:sc='http://www.w3.org/2005/07/scxml'
           xmlns:fs='http://www.freeswitch.org'
           xsi:schemaLocation=' http://www.w3.org/2005/07/scxml  http://freeswitch-scxml.googlecode.com/files/scxml.xsd http://www.freeswitch.org http://freeswitch-scxml.googlecode.com/files/fs-core.xsd' 
           initialstate="intro-main">
   
    <sc:datamodel>
         <sc:data name="name1" expr="'value1'" />    
         <sc:data name="name2" expr="'value2'" />    
    </sc:datamodel>
    
    <sc:state id="intro-main">
        <sc:onentry>
            <fs:answer/>
        </sc:onentry>
        <sc:transition target="exit" />
    </sc:state>
    
    <sc:state id="exit" final="true">
        <sc:onentry>
            <fs:exit expr="'Successful end'" namelist="name1 name2" />
        </sc:onentry>                
    </sc:state>
    
</sc:scxml>

```

