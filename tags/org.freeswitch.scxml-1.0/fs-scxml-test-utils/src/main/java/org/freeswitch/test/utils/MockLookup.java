package org.freeswitch.test.utils;

import java.lang.reflect.Field;
import static junit.framework.Assert.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Mock implementation of system default lookup suitable for use in unit tests.
 * The initial value just contains classpath services.
 */
public class MockLookup extends ProxyLookup {

    private static MockLookup DEFAULT;
    private static boolean making = false;

    static {
        making = true;
        try {
            System.setProperty("org.openide.util.Lookup",
                    MockLookup.class.getName());
            if (Lookup.getDefault().getClass() != MockLookup.class) {
                // Someone else initialized lookup first. Try to force our way.
                Field defaultLookup = Lookup.class.getDeclaredField("defaultLookup");
                defaultLookup.setAccessible(true);
                defaultLookup.set(null, null);
            }
            assertEquals(MockLookup.class, Lookup.getDefault().getClass());
        } catch (Exception x) {
            throw new ExceptionInInitializerError(x);
        } finally {
            making = false;
        }
    }

    /** Do not call this directly! */
    public MockLookup() {
        assertTrue(making);
        assertNull(DEFAULT);
        DEFAULT = this;
        setInstances();
    }

    public static void init() {
    }

    public static void setLookup(Lookup... lookups) {
        DEFAULT.setLookups(lookups);
    }

    public static void setInstances(Object... instances) {
        ClassLoader l = MockLookup.class.getClassLoader();
        setLookup(Lookups.fixed(instances), Lookups.metaInfServices(l),
                Lookups.singleton(l));
    }
}