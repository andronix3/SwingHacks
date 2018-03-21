package com.smartg.swing.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.smartg.java.util.StackTraceUtil;
import java.util.Objects;

/**
 * Each row in this table model represents an Object of specified type.
 * Extending classes should implement setValueAt and getValueAt methods.
 * Moreover after creating ObjectTableModel three things must be done: set
 * column names, set column classes and set column editability.
 *
 * @author User
 * @param <T>
 */
public abstract class ObjectTableModel<T> extends AbstractTableModel {

    private static final long serialVersionUID = 2194853209085099103L;

    private List<T> data = new ArrayList<>();
    private String[] columnNames;
    @SuppressWarnings("rawtypes")
    private Class[] types;
    private Boolean[] editable;
    private final Class<? extends T> classe;

    public ObjectTableModel(Class<? extends T> classe) {
        this.classe = classe;
    }

    public List<T> getData() {
        return data;
    }

    public void setColumnNames(String... columnNames) {
        this.columnNames = columnNames;
    }

    public void setTypes(Class<?>... types) {
        this.types = types;
    }

    public void setEditable(Boolean... canEdit) {
        this.editable = canEdit;
    }

    public void setEditable(int column, Boolean editable) {
        this.editable[column] = editable;
    }

    public void setData(List<T> data) {
        Objects.requireNonNull(data);
        this.data = data;
        fireTableDataChanged();
    }

    public T getRow(int index) {
        if (index < data.size()) {
            return data.get(index);
        }
        return null;
    }

    public final T createRow() {
        try {
            return classe.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            StackTraceUtil.severe(ex);
        }
        return null;
    }

    /**
     * Add empty row to model
     *
     * @return
     */
    public T addRow() {
        T row = initRow(createRow());
        data.add(row);
        fireTableDataChanged();
        return row;
    }

    /**
     * Sometimes Objects need specific initialization. This method will be
     * called just after createRow();
     *
     * @param row
     * @return
     */
    protected T initRow(T row) {
        return row;
    }

    public void addRow(T row) {
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void addRow(int index, T row) {
        data.add(index, row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public int indexOf(T row) {
        return getData().indexOf(row);
    }

    /**
     * Replace row at given index with new one
     *
     * @param index index of element to replace
     * @param row replacement
     * @return replaced element
     */
    public T setRow(int index, T row) {
        T set = data.set(index, row);
        fireTableDataChanged();
        return set;
    }

    public T deleteRow(int i) {
        T remove = data.remove(i);
        fireTableRowsDeleted(i, i);
        return remove;
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    public boolean moveUp(int index) {
        try {
            Collections.swap(data, index, index - 1);
            fireTableDataChanged();
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public boolean moveRow(int source, int dest) {
        try {
            Collections.swap(data, source, dest);
            fireTableDataChanged();
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public boolean moveDown(int index) {
        try {
            Collections.swap(data, index, index + 1);
            fireTableDataChanged();
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    @Override
    public int getRowCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (types != null) {
            return types[columnIndex];
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (editable != null) {
            return editable[columnIndex];
        }
        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int col) {
        if (columnNames != null) {
            return columnNames[col];
        }
        return super.getColumnName(col);
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void clear() {
        int size = getRowCount();
        if (size > 0) {
            data.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
}
