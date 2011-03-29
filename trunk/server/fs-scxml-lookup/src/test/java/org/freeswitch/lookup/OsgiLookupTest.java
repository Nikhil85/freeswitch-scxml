/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.freeswitch.lookup;

import java.sql.Ref;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import static org.easymock.EasyMock.*;

/**
 *
 * @author jocke
 */
public class OsgiLookupTest {
    
    private OsgiLookup lookup;
    private ServiceEvent evt;
    private ServiceReference ref;

    @org.junit.Before
    public void setUp() throws Exception {
       lookup = new OsgiLookup();
       evt = createMock(ServiceEvent.class);
       ref = createMock(ServiceReference.class);
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }


    /**
     * Test of serviceChanged method, of class OsgiLookup.
     */
    @org.junit.Test
    public void testServiceChanged() {
        System.out.println("serviceChanged");
        
        expect(evt.getServiceReference()).andReturn(ref);
        
        
        replay(evt, ref);
        lookup.serviceChanged(evt);
        verify(evt, ref);
    }

}