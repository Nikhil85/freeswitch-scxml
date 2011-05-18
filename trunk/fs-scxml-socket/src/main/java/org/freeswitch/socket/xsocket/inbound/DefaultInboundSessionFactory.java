package org.freeswitch.socket.xsocket.inbound;

import java.io.IOException;
import java.util.HashMap;
import org.freeswitch.adapter.api.InboundSessionFactory;
import org.freeswitch.adapter.api.OutboundSessionFactory;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.socket.xsocket.XsocketEventProducer;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public class DefaultInboundSessionFactory implements InboundSessionFactory {

    @Override
    public Session create(String dialUrl) {

        OutboundSessionFactory factory = Lookup.getDefault().lookup(OutboundSessionFactory.class);
        if (factory == null) {
            return null;
        }

        XsocketClient client = new XsocketClient(dialUrl);
        Session fss = factory.create(new HashMap<String, Object>(), client);
        XsocketEventProducer producer = new XsocketEventProducer(fss.getEventQueue(), client);
        client.setProducer(producer);

        try {
            client.connect();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        return fss;

    }
}
