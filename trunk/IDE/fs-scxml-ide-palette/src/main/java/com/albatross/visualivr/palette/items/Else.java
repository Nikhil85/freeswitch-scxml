package com.albatross.visualivr.palette.items;

import com.albatross.visualivr.palette.ScxmlPaletteUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.openide.text.ActiveEditorDrop;

/**
 *
 * @author joe
 */
public class Else implements ActiveEditorDrop {
    
    private static final String XML = "<else/>";
    
    @Override
    public boolean handleTransfer(JTextComponent targetComponent) {

       try {
           ScxmlPaletteUtilities.insert(XML, targetComponent);
       } catch (BadLocationException ex) {
            return false;
        }
        return true;
    }
}

