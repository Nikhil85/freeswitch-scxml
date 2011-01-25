/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.albatross.visualivr.project;

import com.albatross.visualivr.project.action.ActionProviderImpl;
import com.albatross.visualivr.project.action.CopyOperation;
import com.albatross.visualivr.project.action.DeleteOperation;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author joe
 */
public class IvrProject implements Project {

    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lkp;

    public IvrProject(FileObject projectDir, ProjectState state) {
        this.projectDir = projectDir;
        this.state = state;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {

        if (lkp == null) {
            lkp = Lookups.fixed(createLookups());
        }

        return lkp;
    }

    private Object[] createLookups() {
        Object[] lookups = new Object[]{
            new DeleteOperation(),
            new CopyOperation(),
            new ProjectInfo(this),
            new ActionProviderImpl(this),
            state,
            new ProjectLogicalView(this)};
        return lookups;
    
    }

    void create(Project project) throws IOException {
        projectDir.createData(IvrProjectFactory.ENTRY);
    }
}
