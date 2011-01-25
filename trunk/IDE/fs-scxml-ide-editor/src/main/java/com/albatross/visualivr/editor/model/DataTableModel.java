package com.albatross.visualivr.editor.model;

import java.util.HashSet;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.apache.commons.scxml.model.Data;
import org.apache.commons.scxml.model.Datamodel;

/**
 *
 * @author joe
 */
public class DataTableModel implements TableModel {

    public static final int COLUMN_COUNT = 3;
    public static final int INDEX_EXPR = 1;
    public static final int INDEX_ID = 0;
    public static final int INDEX_SRC = 2;
    private Datamodel model;
    private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

    public DataTableModel(Datamodel model) {
        this.model = model;
    }

    @Override
    public int getRowCount() {
        return model.getData().size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public String getColumnName(int columnIndex) {

        switch (columnIndex) {
            case INDEX_ID:
                return "ID";
            case INDEX_EXPR:
                return "Expr";
            case INDEX_SRC:
                return "Src";
            default:
                throw new IllegalStateException("Unknown column index " + columnIndex);
        }

    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (invalidIndex(rowIndex)) {
            return null;
        }

        Data data = (Data) model.getData().get(rowIndex);

        switch (columnIndex) {
            case INDEX_ID:
                return data.getId();
            case INDEX_EXPR:
                return data.getExpr();
            case INDEX_SRC:
                return data.getSrc();
            default:
                throw new IllegalStateException("Unknown column index " + columnIndex);
        }

    }

    private boolean invalidIndex(int rowIndex) {

        if (rowIndex < 0 || rowIndex > model.getData().size()) {
            return true;
        }

        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        if (invalidIndex(rowIndex)) {
            return;
        }

        Data data = (Data) model.getData().get(rowIndex);

        switch (columnIndex) {
            case INDEX_ID:
                data.setId((String) aValue);
                break;
            case INDEX_EXPR:
                data.setExpr((String) aValue);
                break;
            case INDEX_SRC:
                data.setSrc((String) aValue);
                break;
            default:
                throw new IllegalStateException("Unknown column index " + columnIndex);
        }

    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    private void fireDataChangedEvent() {

        TableModelEvent evt = new TableModelEvent(this);

        for (TableModelListener listener : listeners) {
            listener.tableChanged(evt);
        }

    }

    public void addData(Data value) {
        model.addData(value);
        fireDataChangedEvent();
    }

    public void removeData(int selected) {
        model.getData().remove(selected);
        fireDataChangedEvent();
    }
}
