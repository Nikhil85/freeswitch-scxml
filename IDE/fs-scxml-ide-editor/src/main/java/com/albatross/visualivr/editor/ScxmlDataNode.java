/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.albatross.visualivr.editor;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;

/**
 *
 * @author joe
 */
public class ScxmlDataNode extends DataNode {

    public ScxmlDataNode(ScxmlDataObject sdo) {
      super(sdo, Children.LEAF);
      setIconBaseWithExtension(Constants.IVR_FILE);
    }
}
