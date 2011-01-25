package com.albatross.visualivr.project.action;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.openide.filesystems.FileObject;

/**
 * 
 * @author joe
 */
public class DeleteOperation implements DeleteOperationImplementation {
    
    private static final Logger LOG = Logger.getLogger(DeleteOperation.class.getName());
    
    @Override
    public void notifyDeleting() throws IOException {
      LOG.finest("The project is being deleted");
    }

    @Override
    public void notifyDeleted() throws IOException {
      LOG.finest("The project has been deleted");   
    }

    @Override
    public List<FileObject> getMetadataFiles() {
        return Collections.emptyList();
    }

    @Override
    public List<FileObject> getDataFiles() {
        return Collections.emptyList();
    }
    
}
