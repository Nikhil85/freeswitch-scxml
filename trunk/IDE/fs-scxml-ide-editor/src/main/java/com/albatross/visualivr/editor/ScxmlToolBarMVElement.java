package com.albatross.visualivr.editor;

import com.albatross.visualivr.editor.ui.TransitionTargetInnerPanel;
import com.albatross.visualivr.editor.ui.ScxmlInnerPanel;
import org.apache.commons.scxml.model.Datamodel;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.State;
import org.apache.commons.scxml.model.TransitionTarget;
import org.netbeans.modules.xml.multiview.ToolBarMultiViewElement;
import org.netbeans.modules.xml.multiview.ui.InnerPanelFactory;
import org.netbeans.modules.xml.multiview.ui.SectionInnerPanel;
import org.netbeans.modules.xml.multiview.ui.SectionView;
import org.netbeans.modules.xml.multiview.ui.ToolBarDesignEditor;
import org.openide.util.Exceptions;

/**
 *
 * @author joe
 */
public class ScxmlToolBarMVElement extends ToolBarMultiViewElement implements InnerPanelFactory {
    
    private ToolBarDesignEditor editor;
    private ScxmlDataObject sdo;
    private SectionView sectionView;
    
    
    public ScxmlToolBarMVElement(ScxmlDataObject scxmlDataObject) {
        super(scxmlDataObject);
        this.sdo = scxmlDataObject;
        editor = new ToolBarDesignEditor();
        setVisualEditor(editor);
        
    }

    @Override
    public SectionView getSectionView() {
        return sectionView;
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
        sectionView = new ScxmlSectionView(this, sdo.getScxml());
        editor.setContentView(sectionView);
        
        try {
            sectionView.openPanel(sdo);
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }
    
    
    @Override
    public SectionInnerPanel createInnerPanel(Object model) {
       
        if(model instanceof SCXML) {
           return new ScxmlInnerPanel(sectionView, (SCXML) model, sdo);
          
       } else if (model instanceof TransitionTarget) {
           return new TransitionTargetInnerPanel(sectionView, (TransitionTarget) model, sdo);
      
       } else {
           throw new IllegalStateException("Unknown model object " + model == null ? "null" : model.getClass().getName());
       }
    }
    

}
