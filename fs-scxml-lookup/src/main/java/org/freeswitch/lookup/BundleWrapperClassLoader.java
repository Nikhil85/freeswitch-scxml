package org.freeswitch.lookup;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import org.osgi.framework.Bundle;

public class BundleWrapperClassLoader extends ClassLoader {

    private Bundle bundle;

    public BundleWrapperClassLoader(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return bundle.loadClass(name);
    }

    @Override
    public URL findResource(String name) {
        return bundle.getResource(name);
    }

    @Override
    public synchronized Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> en = bundle.getResources(name);
        if (en == null) {
            en = Collections.enumeration(Collections.EMPTY_LIST);
        }
        return en;
    }
}
