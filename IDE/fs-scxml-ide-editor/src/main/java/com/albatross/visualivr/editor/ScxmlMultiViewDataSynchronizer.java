package com.albatross.visualivr.editor;

import com.albatross.visualivr.scxml.ScxmlSerializer;
import java.io.IOException;
import org.apache.commons.scxml.model.SCXML;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataSynchronizer;
import org.openide.filesystems.FileLock;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author joe
 */
public class ScxmlMultiViewDataSynchronizer extends XmlMultiViewDataSynchronizer {

    private ScxmlDataObject sdo;

    public ScxmlMultiViewDataSynchronizer(ScxmlDataObject dataObject) {
        super(dataObject, 5000);
        this.sdo = dataObject;
    }

    @Override
    protected boolean mayUpdateData(boolean bln) {
        return sdo.checkXml();
    }

    @Override
    protected void updateDataFromModel(Object o, FileLock fl, boolean bln) {

        ScxmlSerializer serializer = Lookup.getDefault().lookup(ScxmlSerializer.class);

        try {
            sdo.getDataCache().setData(fl, serializer.serialize((SCXML) o), bln);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    @Override
    protected Object getModel() {
        return sdo.getScxml();
    }

    @Override
    protected void reloadModelFromData() {
        if (mayUpdateData(true)) {
            sdo.parseDocument();
        }
    }
}
