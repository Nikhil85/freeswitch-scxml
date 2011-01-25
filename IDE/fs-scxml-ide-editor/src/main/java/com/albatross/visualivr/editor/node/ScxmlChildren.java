package com.albatross.visualivr.editor.node;

import java.util.Collections;
import org.apache.commons.scxml.model.TransitionTarget;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author joe
 */
public final class ScxmlChildren extends Children.Keys<TransitionTarget> {
    
    private final java.util.Map<String, TransitionTarget> targets;

    public ScxmlChildren(java.util.Map<String, TransitionTarget> targets) {
        super(false);
        this.targets = targets;
        addNotify();
    }
    
    @Override
    protected Node[] createNodes(TransitionTarget key) {
        return new Node[]{new TransitionTargetNode(key)};
    }

    @Override
    protected void addNotify() {
      setKeys(targets.values());          
    }

    @Override
    protected void removeNotify() {
       setKeys(Collections.EMPTY_LIST);
    }
   
}
