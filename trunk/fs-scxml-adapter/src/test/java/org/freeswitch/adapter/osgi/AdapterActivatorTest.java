package org.freeswitch.adapter.osgi;

import java.util.Dictionary;
import org.easymock.Capture;
import org.freeswitch.adapter.api.OutboundSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class AdapterActivatorTest {

    private AdapterActivator activator;
    private BundleContext context;
    private ServiceRegistration reg;

    @Before
    public void setUp() {
        context = createMock(BundleContext.class);
        reg = createMock(ServiceRegistration.class);
        activator = new AdapterActivator();
    }

    /**
     * Test of start method, of class AdapterActivator.
     */
    @Test
    public void testStart() throws Exception {

        Capture<String> nameCapture = new Capture<String>();
        Capture<OutboundSessionFactory> factoryCapture = new Capture<OutboundSessionFactory>();
        Capture<Dictionary> dictCapture = new Capture<Dictionary>();

        expect(context.registerService(
                capture(nameCapture),
                capture(factoryCapture),
                capture(dictCapture))).andReturn(reg);

        replay(context);
        activator.start(context);
        verify(context);

        assertEquals(OutboundSessionFactory.class.getName(), nameCapture.getValue());
        
    }
}
