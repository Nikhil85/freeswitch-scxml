package com.albatross.visualivr.palette.items;

import com.albatross.visualivr.palette.ScxmlPaletteUtilities;
import com.albatross.visualivr.palette.items.ui.IfCustomizerPanel;
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
public class If implements ActiveEditorDrop {

    private IfCustomizerPanel ifCustomizer;
    private final String title;

    public If() {
        title = NbBundle.getMessage(If.class, "if.drop.title");
    }

    @Override
    public boolean handleTransfer(JTextComponent targetComponent) {

        try {
            ifCustomizer.reset();
            ifCustomizer = new IfCustomizerPanel(IfCustomizerPanel.IF_MODE);

            DialogDescriptor descriptor = new DialogDescriptor(ifCustomizer, title, true, null);
            DialogDisplayer.getDefault().createDialog(descriptor).setVisible(true);

            Object value = descriptor.getValue();

            if (value == DialogDescriptor.OK_OPTION) {
                ScxmlPaletteUtilities.insert(ifCustomizer.getXml(), targetComponent);
                return true;

            } else {
                return false;

            }

        } catch (BadLocationException ex) {
            return false;
        }

    }
}
