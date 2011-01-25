package com.albatross.visualivr.palette;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.Exceptions;

/**
 *
 * @author joe
 */
public class ScxmlPaletteDropDefault implements ActiveEditorDrop {
    
    private String body;

    public ScxmlPaletteDropDefault(String body) {
        this.body = body;
    }
    
    @Override
    public boolean handleTransfer(JTextComponent targetComponent) {
        
        boolean valid = true;
        
        if(targetComponent == null) {
            valid = false;
        
        } else {
            try {
                ScxmlPaletteUtilities.insert(body, targetComponent);
                
            } catch (BadLocationException ex) {
                valid = false;
                Exceptions.printStackTrace(ex);
            }
        }
        
        return valid;
    }

}
