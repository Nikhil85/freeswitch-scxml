package org.freeswitch.scxml.lookup;

import org.osgi.framework.Bundle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Lookup;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class ActivatorTest {

    private Activator activator;
    private BundleContext bctx;
    private Bundle bundleWithServices;
    private Bundle bundleNoServices;
    private Bundle[] bundles;
    private ServiceReference reference;
    private ServiceReference[] references;
    
    
    @Before
    public void setUp() {
        bctx = createMock(BundleContext.class);
        bundleWithServices = createMock(Bundle.class);
        bundleNoServices = createMock(Bundle.class);
        reference = createMock(ServiceReference.class);
        references = new ServiceReference[1];
        references[0] = reference;
        
        bundles = createBundles();
        activator = new Activator() {
            @Override
            protected void setClassLoader(BundleContext context) {
               //No not in tests 
            }
        };
    }


    @After
    public void tearDown() {
    }

    /**
     * Test of start method, of class Activator.
     */
    @Test
    public void testStart() throws Exception {      
        bctx.addServiceListener(isA(OsgiLookup.class));
        expect(bctx.getBundles()).andReturn(bundles);
        expect(bundleNoServices.getRegisteredServices()).andReturn(null);
        expect(bundleWithServices.getRegisteredServices()).andReturn(references);
        expect(reference.getBundle()).andReturn(bundleWithServices);
        expect(bundleWithServices.getBundleContext()).andReturn(bctx);
        expect(bctx.getService(reference)).andReturn(new MockService() {});
        
        replay(bctx, bundleNoServices, bundleWithServices, reference);
        activator.start(bctx);
        verify(bctx, bundleNoServices, bundleWithServices, reference);
        
        assertNotNull("Mock should be in lookup", Lookup.getDefault().lookup(MockService.class));
        
    }

    /**
     * Test of stop method, of class Activator.
     * Should have no effect
     */
    @Test
    public void testStop() throws Exception {
      replay(bctx);
      activator.stop(bctx);
      verify(bctx);
    }
    
    private Bundle[] createBundles() {
        Bundle[] bs = new Bundle[2];
        bs[0] = bundleWithServices;
        bs[1] = bundleNoServices;
        return  bs;
    }
    
    private interface MockService {};
    
}
