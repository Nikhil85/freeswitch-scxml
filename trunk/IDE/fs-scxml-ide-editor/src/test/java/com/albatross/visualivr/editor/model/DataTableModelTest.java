package com.albatross.visualivr.editor.model;

import org.apache.commons.scxml.model.Data;
import org.apache.commons.scxml.model.Datamodel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author joe
 */
public class DataTableModelTest {

    private DataTableModel dataTableModel;

    @Before
    public void setUp() {

        Datamodel datamodel = new Datamodel();

        Data d1 = new Data();
        d1.setId("d1");
        d1.setExpr("'d1'");
        d1.setSrc("path");

        Data d2 = new Data();
        d2.setId("d2");
        d2.setExpr("'d2'");
        d2.setSrc("path");

        Data d3 = new Data();
        d3.setId("d3");
        d3.setExpr("'d3'");
        d3.setSrc("path");

        Data d4 = new Data();
        d4.setId("d4");
        d4.setExpr("'d4'");
        d4.setSrc("path");

        datamodel.addData(d1);
        datamodel.addData(d2);
        datamodel.addData(d3);
        datamodel.addData(d4);

        dataTableModel = new DataTableModel(datamodel);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getRowCount method, of class DataTableModel.
     */
    @Test
    public void testGetRowCount() {
        assertEquals("Data rows should be four", 4, dataTableModel.getRowCount());
    }

    /**
     * Test of getColumnCount method, of class DataTableModel.
     */
    @Test
    public void testGetColumnCount() {
        assertEquals("Data column count should be three", 3, dataTableModel.getColumnCount());
    }

    /**
     * Test of getColumnName method, of class DataTableModel.
     */
    @Test
    public void testGetColumnName() {
        assertEquals("Data column 0 name should be ID", "ID", dataTableModel.getColumnName(0));
        assertEquals("Data column 1 name should be ID", "Expr", dataTableModel.getColumnName(1));
        assertEquals("Data column 2 name should be ID", "Src", dataTableModel.getColumnName(2));
    }

    /**
     * Test of getColumnName method, of class DataTableModel.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetColumnNameIndexToLarge() {
        dataTableModel.getColumnName(3);
    }

    /**
     * Test of getColumnClass method, of class DataTableModel.
     */
    @Test
    public void testGetColumnClass() {
        assertEquals("All columns should be String type", String.class, dataTableModel.getColumnClass(1));
        assertEquals("All columns should be String type", String.class, dataTableModel.getColumnClass(2));
        assertEquals("All columns should be String type", String.class, dataTableModel.getColumnClass(3));
    }

    /**
     * Test of isCellEditable method, of class DataTableModel.
     */
    @Test
    public void testIsCellEditable() {
        assertTrue("All columns should be editable", dataTableModel.isCellEditable(1, 0));
        assertTrue("All columns should be editable", dataTableModel.isCellEditable(1, 1));
        assertTrue("All columns should be editable", dataTableModel.isCellEditable(1, 2));

    }

    /**
     * Test of getValueAt method, of class DataTableModel.
     */
    @Test
    public void testGetValueAt() {
        assertEquals("Data d1 should be at row 0 and id should be the column", "d1", dataTableModel.getValueAt(0, 0));
        assertEquals("Data d2 should be at row 1 and id should be the column", "d2", dataTableModel.getValueAt(1, 0));
        assertEquals("Data d1 should be at row 2 and id should be the column", "d3", dataTableModel.getValueAt(2, 0));

        assertEquals("Data d1 should be at row 0 and id should be the column", "'d1'", dataTableModel.getValueAt(0, 1));
        assertEquals("Data d2 should be at row 1 and id should be the column", "'d2'", dataTableModel.getValueAt(1, 1));
        assertEquals("Data d1 should be at row 2 and id should be the column", "'d3'", dataTableModel.getValueAt(2, 1));
       
    }
    /**
     * Test of getValueAt method, of class DataTableModel.
     */
    @Test
    public void testGetValueAtIndexToLarge() {
        assertNull(dataTableModel.getValueAt(5, 2));  
    }

    /**
     * Test of setValueAt method, of class DataTableModel.
     */
    @Test
    public void testSetValueAt() {
        
        final String newId = "new-id";
        final String newExp = "new-exp";
        
        dataTableModel.setValueAt(newId, 0, 0);
        dataTableModel.setValueAt(newId, 1, 0);
        dataTableModel.setValueAt(newId, 2, 0);
        
        dataTableModel.setValueAt(newExp, 0, 1);
        dataTableModel.setValueAt(newExp, 1, 1);
        dataTableModel.setValueAt(newExp, 2, 1);
        
        assertEquals(newId, dataTableModel.getValueAt(0, 0));
        assertEquals(newId, dataTableModel.getValueAt(1, 0));
        assertEquals(newId, dataTableModel.getValueAt(2, 0));

        assertEquals(newExp, dataTableModel.getValueAt(0, 1));
        assertEquals(newExp, dataTableModel.getValueAt(1, 1));
        assertEquals(newExp, dataTableModel.getValueAt(2, 1));
        
    }
}