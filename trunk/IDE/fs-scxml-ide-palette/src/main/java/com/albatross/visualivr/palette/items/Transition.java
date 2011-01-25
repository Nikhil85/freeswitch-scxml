package com.albatross.visualivr.palette.items;

import com.albatross.visualivr.palette.ScxmlPaletteUtilities;
import com.albatross.visualivr.palette.items.ui.TransitionCustomizerPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.NbBundle;

/**
 *
 * @author joe
 */
public class Transition implements ActiveEditorDrop {

    private final String title;
    private final TransitionCustomizerPanel customizer;

    public Transition() {
        customizer = new TransitionCustomizerPanel();
        title = NbBundle.getMessage(Transition.class, "transition.drop.title");
    }

    @Override
    public boolean handleTransfer(JTextComponent targetComponent) {
        
        customizer.reset();
        DialogDescriptor descriptor = new DialogDescriptor(customizer, title, true, null);
        DialogDisplayer.getDefault().createDialog(descriptor).setVisible(true);
        Object value = descriptor.getValue();

        try {
            
            if (value == DialogDescriptor.OK_OPTION) {
                ScxmlPaletteUtilities.insert(customizer.getXml(), targetComponent);
                return true;

            } else {
                return false;
            }

        } catch (BadLocationException ex) {
            return false;
        }
    }
}
