/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freeswitch.scxml.lookup;

import org.freeswitch.scxml.lookup.OsgiLookup;
import java.sql.Ref;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.lookup.InstanceContent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import static org.junit.Assert.*;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class OsgiLookupTest {

    private static OsgiLookup lookup;
    private ServiceEvent evt;
    private ServiceReference ref;
    private Bundle bundle;
    private BundleContext bctx;
    private InstanceContent content;

    @org.junit.Before
    public void setUp() throws Exception {
        evt = createMock(ServiceEvent.class);
        ref = createMock(ServiceReference.class);
        bundle = createMock(Bundle.class);
        bctx = createMock(BundleContext.class);
        content = new InstanceContent();
        lookup = new OsgiLookup(content);
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    /**
     * Test of serviceChanged method, of class OsgiLookup.
     */
    @org.junit.Test
    public void testServiceRegistered() {
        System.out.println("serviceChanged");

        expect(evt.getServiceReference()).andReturn(ref).times(2);
        expect(ref.getBundle()).andReturn(bundle);
        expect(bundle.getBundleContext()).andReturn(bctx);
        expect(bctx.getService(ref)).andReturn(this);
        expect(evt.getType()).andReturn(ServiceEvent.REGISTERED);

        replay(evt, ref, bctx, bundle);
        lookup.serviceChanged(evt);
        verify(evt, ref, bctx, bundle);

        assertSame("Test class saved in lookup should be found ", this, lookup.lookup(OsgiLookupTest.class));
    }

    /**
     * Test of serviceChanged method, of class OsgiLookup.
     */
    @org.junit.Test
    public void testServiceUnRegistered() {
        System.out.println("serviceChanged");
        content.add(this);
        expect(evt.getServiceReference()).andReturn(ref).times(2);
        expect(ref.getBundle()).andReturn(bundle);
        expect(bundle.getBundleContext()).andReturn(bctx);
        expect(bctx.getService(ref)).andReturn(this);
        expect(evt.getType()).andReturn(ServiceEvent.UNREGISTERING);

        replay(evt, ref, bctx, bundle);
        lookup.serviceChanged(evt);
        verify(evt, ref, bctx, bundle);

        assertNull("Test class removed from lookup should not be found", lookup.lookup(OsgiLookupTest.class));
    }
    
}