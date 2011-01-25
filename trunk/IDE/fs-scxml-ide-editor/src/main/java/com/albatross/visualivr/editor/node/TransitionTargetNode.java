package com.albatross.visualivr.editor.node;

import org.apache.commons.scxml.model.TransitionTarget;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;


/**
 *
 * @author joe
 */
public class TransitionTargetNode extends AbstractNode {
    
    private TransitionTarget target;
    
    public TransitionTargetNode(TransitionTarget target) {
        super(Children.LEAF, Lookups.singleton(target));
        this.target = target;
        setDisplayName(target.getClass().getSimpleName() + " | " +target.getId());
    }
    
}
