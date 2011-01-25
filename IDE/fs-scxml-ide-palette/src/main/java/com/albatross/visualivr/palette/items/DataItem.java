package com.albatross.visualivr.palette.items;

import com.albatross.visualivr.palette.ScxmlPaletteUtilities;
import com.albatross.visualivr.palette.items.ui.DataCustomizerPanel;
import com.albatross.visualivr.palette.items.ui.DialogCustomizer;
import com.albatross.visualivr.scxml.ScxmlSerializer;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.apache.commons.scxml.model.Data;
import org.openide.text.ActiveEditorDrop;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author joe
 */
public class DataItem implements ActiveEditorDrop {

    private final String title;

    public DataItem() {
        title = NbBundle.getMessage(DataItem.class, "data.drop.title");
    }

    @Override
    public boolean handleTransfer(JTextComponent targetComponent) {
        
        Data value = new DialogCustomizer<Data>(new DataCustomizerPanel(), title).getValue();
      
        if (value != null) {
            ScxmlSerializer serializer = Lookup.getDefault().lookup(ScxmlSerializer.class);
            
            try {
                ScxmlPaletteUtilities.insert(serializer.serializeData(value), targetComponent);
            
            } catch (BadLocationException ex) {
                return false;
            }
            
            return true;
        
        } else {
            return false;
        }
    }
}
