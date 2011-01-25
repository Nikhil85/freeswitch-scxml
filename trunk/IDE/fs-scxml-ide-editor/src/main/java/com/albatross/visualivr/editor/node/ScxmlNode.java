package com.albatross.visualivr.editor.node;

import org.apache.commons.scxml.model.SCXML;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author joe
 */
public class ScxmlNode extends AbstractNode {
    
    private SCXML scxml;
    
    public ScxmlNode(SCXML scxml) {
      super(new ScxmlChildren(scxml.getTargets()));
      this.scxml = scxml;
      setDisplayName("Scxml");
    } 
}
