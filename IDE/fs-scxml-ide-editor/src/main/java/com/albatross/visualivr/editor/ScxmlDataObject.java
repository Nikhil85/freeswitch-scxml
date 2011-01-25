package com.albatross.visualivr.editor;

import com.albatross.visualivr.scxml.ScxmlParser;
import java.io.IOException;
import org.apache.commons.scxml.model.SCXML;
import org.netbeans.modules.xml.multiview.DesignMultiViewDesc;
import org.netbeans.modules.xml.multiview.XmlMultiViewDataObject;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.netbeans.spi.xml.cookies.TransformableSupport;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.nodes.Node;
import org.openide.nodes.CookieSet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public final class ScxmlDataObject extends XmlMultiViewDataObject {

    private SCXML scxml;
    private final ScxmlMultiViewDataSynchronizer dataSynchronizer;
    private final CheckXMLSupport checkXMLSupport;

    public ScxmlDataObject(FileObject pf, ScxmlDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        this.dataSynchronizer = new ScxmlMultiViewDataSynchronizer(this);

        CookieSet cookies = getCookieSet();

        org.xml.sax.InputSource is = DataObjectAdapters.inputSource(this);
        javax.xml.transform.Source source = DataObjectAdapters.source(this);

        checkXMLSupport = new CheckXMLSupport(is);

        cookies.add(checkXMLSupport);
        cookies.add(new ValidateXMLSupport(is));
        cookies.add(new TransformableSupport(source));
        parseDocument();

    }

    /**
     * pars 
     */
    void parseDocument() {

        ScxmlParser parser = Lookup.getDefault().lookup(ScxmlParser.class);

        if (parser == null) {
            throw new IllegalStateException("No parser found for scxml");

        } else {
            try {
                scxml = parser.parseDocument(getEditorSupport().getInputStream());

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    public SCXML getScxml() {

        if (scxml == null) {
            parseDocument();
        }

        return scxml;
    }

    @Override
    protected Node createNodeDelegate() {
        return new ScxmlDataNode(this);
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    @Override
    protected DesignMultiViewDesc[] getMultiViewDesc() {
        return new DesignMultiViewDesc[]{new SectionMultiViewDesc(this)};
    }

    @Override
    protected String getPrefixMark() {
        return "";
    }

    public void modelUpdatedFromUI() {
        dataSynchronizer.requestUpdateData();
    }

    boolean checkXml() {
        return checkXMLSupport.checkXML(null);
    }
    
}
