package com.albatross.visualivr.scxml;

import org.apache.commons.scxml.io.SCXMLSerializer;
import org.apache.commons.scxml.model.Data;
import org.apache.commons.scxml.model.Datamodel;
import org.apache.commons.scxml.model.SCXML;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author joe
 */
@ServiceProvider(service = ScxmlSerializer.class)
public class ScxmlSerializerImpl implements ScxmlSerializer {

    private org.apache.commons.scxml.io.SCXMLSerializer serializer;

    @Override
    public String serialize(SCXML scxml) {
        scxml.getNamespaces().remove(ScxmlParserImpl.XSI_SCHEMA_LOCATION);
        return SCXMLSerializer.serialize(scxml);
    }

    @Override
    public String serializeData(Data value) {
        StringBuilder sb = new StringBuilder();
        sb.append("<data");
        appendAttribute(value.getId(), "id", sb);
        appendAttribute(value.getSrc(), "src", sb);
        appendAttribute(value.getExpr(), "expr", sb);
        sb.append("/>");

        return sb.toString();
    }

    private void appendAttribute(String value, String name, StringBuilder sb) {

        if (value != null && !value.trim().isEmpty()) {
            sb.append(" ").append(name).append("=").append("\"").append(value).append("\"");
        }
    }
    
}
