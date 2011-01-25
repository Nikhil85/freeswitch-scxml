package com.albatross.visualivr.palette.items;

import com.albatross.visualivr.palette.ScxmlPaletteUtilities;
import com.albatross.visualivr.palette.items.ui.AssignCustomizerPanel;
import com.albatross.visualivr.palette.items.ui.DataCustomizerPanel;
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
public class Assign implements ActiveEditorDrop {

    private final AssignCustomizerPanel customizer;
    private final String title;

    public Assign() {
        title = NbBundle.getMessage(Assign.class, "assign.drop.title");
        customizer = new AssignCustomizerPanel();
    }

    @Override
    public boolean handleTransfer(JTextComponent targetComponent) {

        try {
            customizer.reset();
            DialogDescriptor descriptor = new DialogDescriptor(customizer, title, true, null);
            DialogDisplayer.getDefault().createDialog(descriptor).setVisible(true);

            Object value = descriptor.getValue();

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

