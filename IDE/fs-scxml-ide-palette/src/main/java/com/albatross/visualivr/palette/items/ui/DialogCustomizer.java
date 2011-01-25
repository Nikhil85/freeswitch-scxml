package com.albatross.visualivr.palette.items.ui;
;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author joe
 */
public class DialogCustomizer<T> {

    private final Customizer<T> customizer;
    private final String title;

    public DialogCustomizer(Customizer<T> customizer, String title) {
        this.customizer = customizer;
        this.title = title;
    }


    public T getValue() {
        DialogDescriptor descriptor = new DialogDescriptor(customizer, title, true, null);
        DialogDisplayer.getDefault().createDialog(descriptor).setVisible(true);

        Object value = descriptor.getValue();

        if (value == DialogDescriptor.OK_OPTION) {
            return customizer.getValue();
        
        } else {
            return null;
        }
    }
}
