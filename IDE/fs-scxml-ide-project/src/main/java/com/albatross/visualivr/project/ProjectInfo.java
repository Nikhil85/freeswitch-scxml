package com.albatross.visualivr.project;

import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.openide.util.ImageUtilities;

/**
 * @author joe
 */
public class ProjectInfo implements ProjectInformation {

    private final Project project;

    public ProjectInfo(Project project) {
        this.project = project;
    }
    
    @Override
    public String getName() {
        return project.getProjectDirectory().getName();
    }

    @Override
    public String getDisplayName() {
       return getName();
    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage("com/albatross/visualivr/project/ivr-project.png"));
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        //Do nothing
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        // Do nothing
    }

}
