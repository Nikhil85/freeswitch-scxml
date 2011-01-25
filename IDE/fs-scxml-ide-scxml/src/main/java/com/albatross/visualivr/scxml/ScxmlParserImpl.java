package com.albatross.visualivr.scxml;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.SCXML;
import org.openide.util.lookup.ServiceProvider;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author joe
 */
@ServiceProvider(service = ScxmlParser.class)
public class ScxmlParserImpl implements ScxmlParser {
    
   private static final String scxmlRule = "scxml";
   static final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation";

    @Override
    public SCXML parseDocument(InputStream stream) {

       try {
           
            Digester parser = SCXMLParser.newInstance();
            final SchemaLocationRule schemaLocationRule = new SchemaLocationRule();
            parser.addRule(scxmlRule, schemaLocationRule);
            parser.setErrorHandler(new SimpleErrorHandler());
            SCXML scxml = (SCXML) parser.parse(new InputSource(stream));
            
            final String schemaLocation = schemaLocationRule.getSchemaLocation();
            scxml.getNamespaces().put(XSI_SCHEMA_LOCATION, schemaLocation);
            return scxml;
        
       } catch (IOException ex) {
            Logger.getLogger(ScxmlParserImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ScxmlParserImpl.class.getName()).log(Level.SEVERE, null, ex);
        }         
       
       return null;
    }
    
    private final class SchemaLocationRule extends Rule {
        
        
        private String schemaLocation;

        public String getSchemaLocation() {
            return schemaLocation;
        }
        
        @Override
        public void begin(String namespace, String name, Attributes attributes) throws Exception {
            this.schemaLocation = attributes.getValue(XSI_SCHEMA_LOCATION);
        }
    }
    
}
