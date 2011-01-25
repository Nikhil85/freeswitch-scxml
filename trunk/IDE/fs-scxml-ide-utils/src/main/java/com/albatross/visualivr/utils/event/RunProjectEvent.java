package com.albatross.visualivr.utils.event;

import org.openide.filesystems.FileObject;

/**
 * 
 * A run project event should always run the initial.xml file. 
 *
 * @author jocke
 */
public class RunProjectEvent {
    
    private final FileObject fileObject;

    public RunProjectEvent(FileObject fileObject) {
        this.fileObject = fileObject;
        
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    @Override
    public String toString() {
       return "Event to signal that the project should be run";
    }

}
