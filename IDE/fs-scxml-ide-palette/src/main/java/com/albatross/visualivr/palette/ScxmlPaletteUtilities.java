package com.albatross.visualivr.palette;

import java.util.logging.Level;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.util.Exceptions;
import java.util.logging.Logger;

/**
 *
 * @author joe
 */
public class ScxmlPaletteUtilities {

    private static final Logger LOG = Logger.getLogger(ScxmlPaletteUtilities.class.getName());
    
    public static void insert(String s, JTextComponent target) throws BadLocationException {
        insert(s, target, true);
    }

    public static void insert(String s, JTextComponent target, boolean reformat) throws BadLocationException {

        if (s == null) {
            s = "";
        }

        Document doc = target.getDocument();
        
        if (doc == null) {
            return;
        }
        
        if(!(doc  instanceof BaseDocument)) {
             LOG.log(Level.SEVERE, "The document is not of type BaseDocument can't proceed");                        
            return;
        }

        final int start = insert(s, target, doc);

        final Reformat ref = Reformat.get(doc);
        final int end = start + s.length();

        /**
         * Tested at the beginning 
         */
        BaseDocument document = (BaseDocument) doc;

        document.runAtomic(new Runnable()  {

            @Override
            public void run() {
                try {
                    ref.lock();
                    ref.reformat(start, end);
                    ref.unlock();
                } catch (BadLocationException ex) {
                    ref.unlock();
                    Exceptions.printStackTrace(ex);
                }

            }
        });

    }

    private static int insert(String s, JTextComponent target, Document doc) throws BadLocationException {

        int start = -1;
        try {
            //at first, find selected text range
            Caret caret = target.getCaret();
            int p0 = Math.min(caret.getDot(), caret.getMark());
            int p1 = Math.max(caret.getDot(), caret.getMark());
            doc.remove(p0, p1 - p0);

            //replace selected text by the inserted one
            start = caret.getDot();
            doc.insertString(start, s, null);
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return start;
    }
}
