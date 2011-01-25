package com.albatross.visualivr.project.node;

import java.awt.Image;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author joe
 */
public class ProjectNode extends FilterNode {

    private Project project;

    public ProjectNode(Node node, Project project) {
        super(node, new FilterNode.Children(node),
                new ProxyLookup(new Lookup[]{Lookups.singleton(project),
                    node.getLookup()
                }));

        this.project = project;

    }

    @Override
    public Action[] getActions(boolean arg0) {
        Action[] nodeActions = new Action[5];
        nodeActions[0] = CommonProjectActions.newFileAction();
        nodeActions[1] = CommonProjectActions.copyProjectAction();
        nodeActions[2] = CommonProjectActions.deleteProjectAction();
        nodeActions[3] = CommonProjectActions.setAsMainProjectAction();
        nodeActions[4] = CommonProjectActions.closeProjectAction();
        return nodeActions;
    }

    @Override
    public Image getIcon(int type) {
      return ImageUtilities.loadImage("com/albatross/visualivr/project/ivr-project.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getDisplayName() {
        return project.getProjectDirectory().getName();
    }
    
}
