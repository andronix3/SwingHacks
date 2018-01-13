package com.smartg.swing.table;


import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.smartg.java.util.StackTraceUtil;

/**
 * Each row in this table model represents an Object of specified type.
 * To make this usable we have to implement setValueAt and getValueAt methods.
 * @author User
 * @param <T>
 */
public abstract class ObjectTableModel<T> extends AbstractTableModel {

	private static final long serialVersionUID = 2194853209085099103L;
	
	private List<T> data;
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
        this.data = data;
        fireTableDataChanged();
    }

    public T getRow(int index) {
        return data.get(index);
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
     * Sometimes Objects need specific initialization.
     * This method will be called just after createRow();
     * @param row
     * @return 
     */
    protected T initRow(T row) {
        return row;
    }

    public void addRow(T row) {
        data.add(row);
        fireTableDataChanged();
    }
    
    public void addRow(int index, T row) {
        data.add(index, row);
        fireTableDataChanged();
    }
    
    /**
     * Replace row at given index with new one
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
        fireTableDataChanged();
        return remove;
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
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (editable == null || editable.length <= columnIndex) {
            return false;
        }
        return editable[columnIndex];
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void clear() {
        data.clear();
    }
}
