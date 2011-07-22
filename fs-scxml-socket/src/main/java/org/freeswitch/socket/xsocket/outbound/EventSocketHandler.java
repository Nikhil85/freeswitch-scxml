package org.freeswitch.socket.xsocket.outbound;

import java.util.Map;
import org.freeswitch.socket.xsocket.EventManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xsocket.connection.ConnectionUtils;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.freeswitch.adapter.api.constant.VarName;
import org.freeswitch.adapter.api.event.DefaultEventQueue;
import org.freeswitch.adapter.api.event.Event;
import org.freeswitch.adapter.api.session.OutboundSessionFactory;
import org.freeswitch.scxml.application.api.ThreadPoolManager;
import org.freeswitch.socket.xsocket.ApplicationRunner;
import org.freeswitch.socket.xsocket.EventReader;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public final class EventSocketHandler implements IDataHandler, IDisconnectHandler, IConnectHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EventSocketHandler.class);
    private final EventReader reader;

    public EventSocketHandler(EventReader reader) {
        this.reader = reader;
    }
    
    @Override
    public boolean onData(final INonBlockingConnection connection) throws IOException {

        Event evt = reader.readEvent(connection);
        
        if(evt == null || evt.getEventName() == null) {
            return true;
        }

        EventManager manager = (EventManager) connection.getAttachment();

        if (manager != null) {
            manager.onEvent(evt);

        } else {
            initSession(evt, ConnectionUtils.synchronizedConnection(connection));
        }

        return true;
    }

    private void initSession(Event evt, final INonBlockingConnection connection) throws UnsupportedEncodingException {
        LOG.debug("New Connection so prepare the call to launch");

        OutboundSessionFactory factory = Lookup.getDefault().lookup(OutboundSessionFactory.class);

        if (factory == null) {
            LOG.warn("No factory found in lookup ");
            return;
        }
        
        Map<String, String> vars = evt.getBodyAsMap();
        DefaultEventQueue eventQueue = new DefaultEventQueue(vars.get(VarName.UNIQUE_ID));
        EventManager manger = new EventManager(connection, eventQueue);
        connection.setAttachment(manger);
        runApplication(new ApplicationRunner(factory.create(new HashMap<String, Object>(vars), manger, eventQueue)));
    }

    private void runApplication(Runnable appRunner) {
        LOG.trace("launch application in new thread");
        Lookup.getDefault().lookup(ThreadPoolManager.class).getWorkerPool().execute(appRunner);
    }

    
    @Override
    public boolean onConnect(INonBlockingConnection connection) {
        try {
            LOG.debug("onConnect:  connection[{}] {} bytes available", connection, connection.available());
            connection.write("connect\n\n");
            connection.write("myevents\n\n");
            connection.write("filter Event-Name " + Event.CHANNEL_EXECUTE_COMPLETE + "\n\n");
            connection.write("filter Event-Name " + Event.DTMF + "\n\n");
        } catch (IOException ex) {
            LOG.error("Oops! onConnect error.", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean onDisconnect(INonBlockingConnection connection) {
        try {
            LOG.debug("onDisconnect:  connection[{}] {} bytes available", connection, connection.available());

            EventManager fss = (EventManager) connection.getAttachment();

            if (fss == null) {
                LOG.warn("A connection was set up, but no session was created.");

            } else {
                fss.onClose();
            }

        } catch (Exception ex) {
            LOG.error("Oops! onDisconnect error.", ex);
            return false;
        }
        return true;
    }
}
