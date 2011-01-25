package com.albatross.visualivr.palette;

import java.io.IOException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author joe
 */
public class ScxmlPaletteCustomizerAction extends CallableSystemAction{

    @Override
    public void performAction() {
        
        try {
            ScxmlPaleteFactory.getPalette().showCustomizer();
        
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public String getName() {
      return NbBundle.getMessage(ScxmlPaletteCustomizerAction.class, "ScxmlPaletteCustomizer");
    }

    @Override
    public HelpCtx getHelpCtx() {
       return HelpCtx.DEFAULT_HELP;
    }

}
