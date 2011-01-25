package com.albatross.visualivr.scxml;

import org.apache.commons.scxml.model.Data;
import org.apache.commons.scxml.model.SCXML;

/**
 *
 * @author joe
 */
public interface ScxmlSerializer {
    
    String serialize(SCXML scxml);

    String serializeData(Data value);    
}
