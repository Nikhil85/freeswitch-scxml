package com.albatross.visualivr.project.action;

import com.albatross.visualivr.utils.EventLookup;
import com.albatross.visualivr.utils.event.RunProjectEvent;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/** 
 * @author joe
 */
public class ActionProviderImpl implements ActionProvider {

    private static final Logger LOG = Logger.getLogger(ActionProviderImpl.class.getName());
    
    private static final String INITAIL_FILE = "initial.xml";
    
    private Project project;
    private ProjectOperationsImpl operations;
    
    private String[] supported = new String[]{
        ActionProvider.COMMAND_DELETE,
        ActionProvider.COMMAND_COPY,
        ActionProvider.COMMAND_RUN,
    };

    public ActionProviderImpl(Project project) {
        this.project = project;
        this.operations = new ProjectOperationsImpl();    
    }

    void setOperations(ProjectOperationsImpl operations) {
        this.operations = operations;
    }
    
    
    @Override
    public String[] getSupportedActions() {
        return supported;
    }

    @Override
    public void invokeAction(String string, Lookup lkp) throws IllegalArgumentException {

        if (string.equalsIgnoreCase(ActionProvider.COMMAND_DELETE)) {
           operations.performDefaultDeleteOperation(project);

        } else if (string.equalsIgnoreCase(ActionProvider.COMMAND_COPY)) {
            operations.performDefaultCopyOperation(project);

        } else if (string.equalsIgnoreCase(ActionProvider.COMMAND_RUN)) {
            
            LOG.info("Will run main project by adding event");
            
            FileObject fileObject = project.getProjectDirectory().getFileObject(INITAIL_FILE);
            RunProjectEvent event = new RunProjectEvent(fileObject);
            EventLookup.getDefault().add(event);
            
        } else {
           throw new IllegalArgumentException(string); 
        }

    }

    @Override
    public boolean isActionEnabled(String command, Lookup lkp) throws IllegalArgumentException {

        if ((command.equals(ActionProvider.COMMAND_DELETE))) {
            return true;

        } else if ((command.equals(ActionProvider.COMMAND_COPY))) {
            return true;

        } else if ((command.equals(ActionProvider.COMMAND_RUN))) {
            return true;

        } else {
            throw new IllegalArgumentException(command);
        }

    }
   
   /**
     * A class to simplify testing 
     */
   class ProjectOperationsImpl  {

        void performDefaultCopyOperation(Project p) {
            DefaultProjectOperations.performDefaultCopyOperation(project);
        }
        
        void performDefaultDeleteOperation(Project p) {
            DefaultProjectOperations.performDefaultDeleteOperation(project);
        }
    }
}
