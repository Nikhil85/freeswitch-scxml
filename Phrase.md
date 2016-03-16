# phrase #
The phrase action uses FreeSwitch say API to speak number currency and more.

## Attributes ##
| **Name**      | **Default** | **Type**      | **Description**                                  |
|:--------------|:------------|:--------------|:-------------------------------------------------|
| test          | true        | boolean       | Test if this action should execute or not        |
| value         | null        | expression    | The value to phrase                              |
| type          | null        | String        | A type, see below for valid values               |
| module        | en          | String        | The say module to use                            |
| method        | null        | String        | How to phrase, for valid values see below        |

## Types ##
  * NUMBER
  * ITEMS
  * PERSONS
  * MESSAGES
  * CURRENCY
  * TIME\_MEASUREMENT
  * CURRENT\_DATE
  * CURRENT\_TIME
  * CURRENT\_DATE\_TIME
  * TELEPHONE\_NUMBER
  * TELEPHONE\_EXTENSION
  * URL
  * IP\_ADDRESS
  * EMAIL\_ADDRESS
  * POSTAL\_ADDRESS
  * ACCOUNT\_NUMBER
  * NAME\_SPELLED
  * NAME\_PHONETIC
  * SHORT\_DATE\_TIME

## Methods ##
  * PRONOUNCED
  * ITERATED
  * COUNTED

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
            <fs:phrase type="number" module="en" method="iterated" value="1234" />
            <fs:hangup />
        </sc:onentry>
    </sc:state>    
</sc:scxml>
```

