package com.albatross.visualivr.simulator.scxml;

import java.io.InputStream;
import org.apache.commons.scxml.model.SCXML;

/**
 *
 * @author joe
 */
public interface ScxmlParser {
    
    /**
     * Get an scxml model by parsing a document
     * 
     * @param inputStream To read the document from
     * 
     * @return a SCXML object.
     * 
     */
    SCXML parseDocument(InputStream inputStream);

}
