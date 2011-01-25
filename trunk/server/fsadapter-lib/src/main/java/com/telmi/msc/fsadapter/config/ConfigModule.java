package com.telmi.msc.fsadapter.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author joe
 */
public final class ConfigModule extends AbstractModule {

     private static final Logger LOG =
            LoggerFactory.getLogger(ConfigModule.class);
     
    private static final String CONFIG_PROP_FILE = "config.properties";

    /**
     * Configure the monitor module.
     */
    @Override
    protected void configure() {
        try {
            Properties properties = loadProperties();
            Names.bindProperties(binder(), properties);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

    }

    /**
     * Load properties.
     *
     * @return The properties loaded from the class path.
     *
     * @throws IOException If it fails to load properties.
     */
    private static Properties loadProperties() throws IOException {

        Properties properties = new Properties();

        //
        // 1) load default properties
        //
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(CONFIG_PROP_FILE);

        if (url == null) {
            url = loader.getResource("/" + CONFIG_PROP_FILE);
        }

        properties.load(url.openStream());

        Set<Object> keys = properties.keySet();


        //
        // 2) override with properties from propfile
        //
        String confFileName = System.getProperty("propfile");

        if (confFileName != null) {

            FileInputStream fis = null;

            try {
                fis = new FileInputStream(confFileName);
                properties.load(fis);
                fis.close();

            } catch (FileNotFoundException e) {
                LOG.warn("Oops! {}", e.getMessage());

            } finally {

                if (fis != null) {
                    fis.close();
                }

            }
        }

        //
        // 3) override with argument properties
        //
        for (Object key : keys) {
            String value = System.getProperty((String) key);
            if (value != null) {
                properties.setProperty((String) key, value);
            }
        }

        return properties;
    }
}
