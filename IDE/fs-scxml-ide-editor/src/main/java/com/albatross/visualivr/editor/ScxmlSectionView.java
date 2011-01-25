package com.albatross.visualivr.editor;

import com.albatross.visualivr.editor.node.ScxmlNode;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.TransitionTarget;
import org.netbeans.modules.xml.multiview.ui.InnerPanelFactory;
import org.netbeans.modules.xml.multiview.ui.SectionContainer;
import org.netbeans.modules.xml.multiview.ui.SectionPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import org.openide.nodes.Node;

/**
 *
 * @author joe
 */
public class ScxmlSectionView extends SectionView {
    
    private final SCXML scxml;
    
    /**
     * TODO fix the scxml template to be a valid hello worl kind of document
     * 
     * @param factory
     * @param scxml 
     */
    
    public ScxmlSectionView(InnerPanelFactory factory, SCXML scxml) {
        super(factory);
        this.scxml = scxml;
        
        ScxmlNode scxmlNode = new ScxmlNode(scxml);
        
        addSection(new SectionPanel(this, scxmlNode, "SCXML" , scxml));
        
        Node[] targetNodes = scxmlNode.getChildren().getNodes();
        
        for (int i = 0; i < targetNodes.length; i++) {
              Node node = targetNodes[i];
              TransitionTarget target = node.getLookup().lookup(TransitionTarget.class);
              addSection(new SectionPanel(this, node, node.getDisplayName(), target));
        }
        
        setRoot(scxmlNode);
        
    }
    
}
