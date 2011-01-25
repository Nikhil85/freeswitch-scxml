package com.albatross.visualivr.palette.items;

import com.albatross.visualivr.palette.ScxmlPaletteUtilities;
import com.albatross.visualivr.palette.items.ui.StateCustomizerPanel;
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
public class State implements ActiveEditorDrop {

    private StateCustomizerPanel stateCustomizer;
    private final String title;

    public State() {
        title = NbBundle.getMessage(State.class, "state.drop.title");
    }

    @Override
    public boolean handleTransfer(JTextComponent targetComponent) {

        try {
            stateCustomizer = new StateCustomizerPanel(StateCustomizerPanel.STATE);
            
            DialogDescriptor descriptor = new DialogDescriptor(stateCustomizer, title, true, null);
            DialogDisplayer.getDefault().createDialog(descriptor).setVisible(true);

            Object value = descriptor.getValue();

            if (value == DialogDescriptor.OK_OPTION) {
                ScxmlPaletteUtilities.insert(stateCustomizer.getXml(), targetComponent);
                return true;

            } else {
                return false;

            }

        } catch (BadLocationException ex) {
            return false;
        }

    }
}

