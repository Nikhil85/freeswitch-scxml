package com.albatross.visualivr.project.action;

import com.albatross.visualivr.project.action.ActionProviderImpl.ProjectOperationsImpl;
import org.netbeans.spi.project.ActionProvider;
import java.util.Arrays;
import java.util.List;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.api.project.Project;
import org.openide.util.Lookup;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public class ActionProviderImplTest {
    
    private ActionProviderImpl actionProvider;
    private Project proj;
    ProjectOperationsImpl operations;
    
    @Before
    public void setUp() {
        proj = EasyMock.createMock(Project.class); 
        operations = EasyMock.createMock(ProjectOperationsImpl.class);
        
        actionProvider = new ActionProviderImpl(proj);
        actionProvider.setOperations(operations);
        
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSupportedActions method, of class ActionProviderImpl.
     */
    @Test
    public void testGetSupportedActions() {
        String[] actions = actionProvider.getSupportedActions();
        List<String> aList = Arrays.asList(actions);
        assertTrue("Two actions no more no less ", aList.size() == 3);
        assertTrue(aList.contains(ActionProvider.COMMAND_COPY));
        assertTrue(aList.contains(ActionProvider.COMMAND_RUN));
        assertTrue(aList.contains(ActionProvider.COMMAND_DELETE));
    }

    /**
     * Test of invokeAction method, of class ActionProviderImpl.
     */
    @Test
    public void testInvokeActionDelete() {        
        operations.performDefaultDeleteOperation(proj);
        EasyMock.replay(proj, operations);       
        actionProvider.invokeAction(ActionProvider.COMMAND_DELETE, Lookup.EMPTY);
        EasyMock.verify(proj, operations);
    }
    
    /**
     * Test of invokeAction method, of class ActionProviderImpl.
     */
    @Test
    public void testInvokeActionCopy() {
        operations.performDefaultCopyOperation(proj);
        EasyMock.replay(proj, operations);       
        actionProvider.invokeAction(ActionProvider.COMMAND_COPY, Lookup.EMPTY);
        EasyMock.verify(proj, operations);
    }

    
    /**
     * Test of isActionEnabled method, of class ActionProviderImpl.
     */
    @Test
    public void testIsActionEnabled() {
        
    }

}