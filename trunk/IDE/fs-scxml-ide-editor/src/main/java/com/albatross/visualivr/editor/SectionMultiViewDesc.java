package com.albatross.visualivr.editor;

import java.awt.Image;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.modules.xml.multiview.DesignMultiViewDesc;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

public class SectionMultiViewDesc extends DesignMultiViewDesc {
    
    private static final String ID = "sectionDesignView";
    
    public SectionMultiViewDesc(ScxmlDataObject dObj) {
       super(dObj, NbBundle.getMessage(SectionMultiViewDesc.class, "design.view.name")); 
    }

    @Override
    public MultiViewElement createElement() {
        ScxmlDataObject scxmlDataObject = (ScxmlDataObject) getDataObject();
        return new ScxmlToolBarMVElement(scxmlDataObject);
    }

    @Override
    public Image getIcon() {
       return ImageUtilities.loadImage(Constants.IVR_FILE);
    }

    @Override
    public String preferredID() {
       return ID;
    }
}
