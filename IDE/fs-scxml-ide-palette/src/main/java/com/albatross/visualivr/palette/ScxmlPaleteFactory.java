package com.albatross.visualivr.palette;

import java.io.IOException;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;

/**
 *
 * @author joe
 */
public class ScxmlPaleteFactory {
   
      public static final String SCXML_PALETTE_FOLDER = "ScxmlPalette";
    
    private static PaletteController palette = null;
    
    public static PaletteController getPalette() throws IOException {
        
        if (palette == null){
            palette = PaletteFactory.createPalette(SCXML_PALETTE_FOLDER, new ScxmlPaletteActions()); 
        } 
        
        return palette;
    }
    
}
