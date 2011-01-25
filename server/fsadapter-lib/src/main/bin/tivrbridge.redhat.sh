#!/bin/bash
#
# tivrbridge    This starts and stops the tivrbridge
#
# chkconfig: 345 60 50
# chkconfig: - 60 50
# description: tivrbridge - startup script for tivrbridge on CentOS
# processname: /opt/tivrbridge/bin/tivrbridge
# pidfile: /tmp/tivrbridge.pid


PATH=/sbin:/bin:/usr/bin:/usr/sbin

# Source function library.
. /etc/init.d/functions

# Get config.
test -f /etc/sysconfig/network && . /etc/sysconfig/network


# Check that networking is up.
[ "${NETWORKING}" = "yes" ] || exit 101

test -f /etc/sysconfig/tivrbridge && . /etc/sysconfig/tivrbridge

tivrbridgehome=${TIVRBRIDGE_HOME:-/usr/local/tivrbridge}
user=${RUNNING_USER:-telmisrv}
pidfile=${PID_FILE:-/tmp/tivrbridge.pid}
prop=/usr/local/tivrbridge/config.properties

# Check that we are root ... so non-root users stop here
if [ `id -u` != 0 ]; then 
    if [ `whoami` != "$runninguser" ]; then 
    echo "cant start tivrbridge as ${runninguser}"
    exit 1
    fi
fi

classpath=${tivrbridgehome}
jar=${tivrbridgehome}/tivrbridge.jar

logbackconfig=${tivrbridgehome}/logback.xml


RETVAL=0

launch_daemon() {

  /bin/sh <<EOF
runuser -s /bin/bash - $user -c "java -Dpidfile=$pidfile -Dpropfile=$prop -Dlogback.configurationFile=$logbackconfig -cp $classpath -jar $jar daemon <&- &"
echo $?
EOF
}

start() {
    
    if [ -f $pidfile ]; then
        echo "service already started"
        exit 0
    fi
    
    
    echo -n $"Starting tivrbridge: "
    RETVAL=`launch_daemon`
    [ $RETVAL = 0 ] && success
    [ $RETVAL != 0 ] && failure
    echo
    return $RETVAL
}

stop() {
        echo -n $"Stopping tivrbridge: "
        killproc -p ${pidfile} -HUP
    RETVAL=$?
    echo
    return $RETVAL
}

restart(){
    stop
    sleep 5
    start
}

# See how we were called.
case "$1" in

    start)
    start
        ;;

    stop)
    stop
        ;;

    restart)
    restart
    ;;
    *)
    echo "usage: $0 { start | stop | restart }" >&2
    RETVAL=1

esac
exit $RETVAL

EOF
