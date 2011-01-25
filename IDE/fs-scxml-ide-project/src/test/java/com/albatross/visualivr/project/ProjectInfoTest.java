package com.albatross.visualivr.project;

import org.easymock.EasyMock;
import org.netbeans.api.project.Project;
import org.junit.Before;
import org.junit.Test;
import org.openide.filesystems.FileObject;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public class ProjectInfoTest {
    public static final String PROJECT_NAME = "TestIVR";
    
    private Project project;
    private ProjectInfo info;
    private FileObject root;
    
    @Before
    public void setup() {
        project = EasyMock.createMock(Project.class);
        root = EasyMock.createMock(FileObject.class);
        info = new ProjectInfo(project);
    }
    
    
    /**
     * Test of getName method, of class ProjectInfo.
     */
    @Test
    public void testGetName() {
       EasyMock.expect(project.getProjectDirectory()).andReturn(root);
       EasyMock.expect(root.getName()).andReturn(PROJECT_NAME);
       EasyMock.replay(project, root);
       assertEquals(info.getName(), PROJECT_NAME );
       EasyMock.verify(project, root);
    }

    /**
     * Test of getDisplayName method, of class ProjectInfo.
     */
    @Test
    public void testGetDisplayName() {
       EasyMock.expect(project.getProjectDirectory()).andReturn(root);
       EasyMock.expect(root.getName()).andReturn(PROJECT_NAME);
       EasyMock.replay(project, root);
       assertEquals(info.getDisplayName(), PROJECT_NAME );
       EasyMock.verify(project, root);
    }

    /**
     * Test of getIcon method, of class ProjectInfo.
     */
    @Test
    public void testGetIcon() {
        assertNotNull(info.getIcon());
    }


}