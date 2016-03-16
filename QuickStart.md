# Install #
This guide will help you get started with FreeSwitch SCXML.

## Prerequisite ##

### FreeSwitch ###
Download and install FreeSwitch. Make sure that you Install the flite module<br />
http://wiki.freeswitch.org/wiki/Installation_Guide <br />

### Apache Karaf ###
Download and install apache karaf. <br />
http://karaf.apache.org/manual/2.1.99-SNAPSHOT/users-guide/installation.html <br />

## Configuration ##

### Apache Karaf ###
Create file with content. <br />
$KARAF\_HOME/etc/org.freeswitch.scxml.cfg <br />
```
#Pools
scheduler.corePoolSize = 20
appPool.nThreads = 100
workPool.nThreads = 100

#paths
recording.path = /tmp

#Reparse the machines or not
scxml.use.cache = false

#port
tcp.port = 9696
```

Add repo url.<br />
$KARAF\_HOME/etc/org.ops4j.pax.url.mvn.cfg

```
org.ops4j.pax.url.mvn.repositories= \
    http://repo1.maven.org/maven2, \
    https://freeswitch-scxml.googlecode.com/svn/repo/, \
    http://repository.apache.org/content/groups/snapshots-group@snapshots@noreleases, \
    http://repository.ops4j.org/maven2, \
    http://svn.apache.org/repos/asf/servicemix/m2-repo, \
    http://repository.springsource.com/maven/bundles/release, \
    http://repository.springsource.com/maven/bundles/external
```

Create file with content. <br />
$FS\_HOME/conf/dialplan/public/888\_hello\_world.xml
```
<include>
  <extension name="public_scxml">
    <condition field="destination_number" expression="^(888)$">
        <action application="set" data="tts_engine=flite"/>
        <action application="set" data="tts_voice=kal"/>
       <action application="socket" data="127.0.0.1:9696 async full"/>
    </condition>
  </extension>
</include>
```

## Start application ##
Launch apache karf.
```
$KARAF_HOME/bin/karaf
```

Add feature url.
```
karaf@root> features:addurl https://freeswitch-scxml.googlecode.com/svn/trunk/features-fs-scxml.xml
```
Start feature
```
karaf@root> features:install fs-scxml-feature
```
List and make sure that all bundles are activated
```
karaf@root> list
```

Start FreeSwitch

## Run Hello World ##
Create file with content <br />

HelloWorld.xml

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
            <fs:say>'Hello world'</fs:say>
            <fs:wait value="3s" />            
            <fs:hangup />
        </sc:onentry>
    </sc:state>
</sc:scxml>
```

Dial the following sip number. <br />
sip:888@Your--ip--here:5080;scxml=file:/path/to/HelloWorld.xml






