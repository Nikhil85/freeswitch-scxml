/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.albatross.visualivr.palette;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author joe
 */
class ScxmlPaletteActions extends PaletteActions {

    public ScxmlPaletteActions() {
    }

    @Override
    public Action[] getImportActions() {
        return new Action[]{};
    }

    @Override
    public Action[] getCustomPaletteActions() {
        return new Action[]{};
    }

    @Override
    public Action[] getCustomCategoryActions(Lookup lkp) {
        return new Action[]{};
    }

    @Override
    public Action[] getCustomItemActions(Lookup lkp) {
        return new Action[]{};
    }

    @Override
    public Action getPreferredAction(Lookup lkp) {
        return new PaletteInsertAction(lkp);
    }

    private static class PaletteInsertAction extends AbstractAction {

        public static final String KEY_ERROR_NO_FOCUS_DOC = "ErrorNoFocusDoc";
        private Lookup lookup;

        public PaletteInsertAction(Lookup lookup) {
            super();
            this.lookup = lookup;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            ActiveEditorDrop drop = lookup.lookup(ActiveEditorDrop.class);

            JTextComponent target = Utilities.getFocusedComponent();


            if (target == null) {
                String msg = NbBundle.getMessage(ScxmlPaletteActions.class, KEY_ERROR_NO_FOCUS_DOC);
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));

            } else {
                
                try {
                    drop.handleTransfer(target);

                } finally {
                    Utilities.requestFocus(target);
                }

                try {
                    PaletteController pc = ScxmlPaleteFactory.getPalette();
                    pc.clearSelection();
                
                } catch (IOException ioe) {
                   Exceptions.printStackTrace(ioe);                                  
                } 
                
            }

        }
    }
}
