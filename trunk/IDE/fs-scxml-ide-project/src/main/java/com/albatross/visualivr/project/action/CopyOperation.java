/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.albatross.visualivr.project.action;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.openide.filesystems.FileObject;

/**
 * 
 * @author joe
 */
public class CopyOperation implements CopyOperationImplementation {
    
    private static final Logger LOG = Logger.getLogger(CopyOperation.class.getName());
    
    @Override
    public void notifyCopying() throws IOException {
       LOG.info("Copy project");
    }
    
    @Override
    public void notifyCopied(Project prjct, File file, String string) throws IOException {     
        LOG.info("Project copied");
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
