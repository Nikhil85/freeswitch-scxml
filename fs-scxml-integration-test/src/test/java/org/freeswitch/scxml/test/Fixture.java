package org.freeswitch.scxml.test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jocke
 */
public class Fixture {

    private Fixture() {
    }

    public static Map<String, String> createDataEventMap(String url) {
        Map<String, String> data = new HashMap<>();
        data.put("variable_scxml", Fixture.class.getClassLoader().getResource(url).toString());
        return data;
    }
}
