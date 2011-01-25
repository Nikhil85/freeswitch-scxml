/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.albatross.visualivr.editor;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;

/**
 *
 * @author joe
 */
public class ScxmlDataLoader extends UniFileLoader {

    public static final String REQUIRED_MIME = "text/scxml+xml";

    public ScxmlDataLoader() {
        super(ScxmlDataObject.class.getName());
    }

    @Override
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }

    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new ScxmlDataObject(primaryFile, this);
    }

    @Override
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
}
