package com.albatross.visualivr.project;

import com.albatross.visualivr.project.node.ProjectNode;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;

/**
 * 
 * @author joe
 * 
 */
class ProjectLogicalView implements LogicalViewProvider {
    
    private final IvrProject project;

    ProjectLogicalView(IvrProject project) {
        this.project = project;    
    }

    @Override
    public Node createLogicalView() {
       DataFolder folder = DataFolder.findFolder(project.getProjectDirectory());
       Node node = folder.getNodeDelegate();
       
       return new ProjectNode(node, project);
    }

    @Override
    public Node findPath(Node node, Object o) {
      return null; 
    }

}
