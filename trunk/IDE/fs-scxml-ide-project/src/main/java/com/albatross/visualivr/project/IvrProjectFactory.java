package com.albatross.visualivr.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author joe
 */
@ServiceProvider(service = ProjectFactory.class)
public class IvrProjectFactory implements ProjectFactory {

    public static final String ENTRY = "initial.xml";

    @Override
    public boolean isProject(FileObject fo) {
        return fo.getFileObject(ENTRY) != null;
    }

    @Override
    public Project loadProject(FileObject dir, ProjectState ps) throws IOException {
        return isProject(dir) ? new IvrProject(dir, ps) : null;
    }

    @Override
    public void saveProject(Project project) throws IOException, ClassCastException {

        IvrProject ivp = (IvrProject) project;

        FileObject projectRoot = project.getProjectDirectory();

        if (projectRoot.getFileObject(ENTRY) == null) {
            throw new IOException(ENTRY + " does not exist in this project folder" + projectRoot.getPath());
 
        } else {
           //Force creation:
            ivp.create(project);
        }
    }
}
