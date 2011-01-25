package com.albatross.visualivr.simulator.scxml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.openide.util.lookup.ServiceProvider;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author joe
 */
@ServiceProvider(service = ScxmlParser.class)
public class ScxmlParserImpl implements ScxmlParser {

    @Override
    public SCXML parseDocument(InputStream stream) {

        try {
            //TODO get custom actions from classpath
            return SCXMLParser.parse(new InputSource(stream), new SimpleErrorHandler(), Collections.EMPTY_LIST);

        } catch (IOException ex) {
            Logger.getLogger(ScxmlParserImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ScxmlParserImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ModelException ex) {
            Logger.getLogger(ScxmlParserImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
