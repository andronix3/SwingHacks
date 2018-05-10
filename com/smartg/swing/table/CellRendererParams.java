package com.smartg.swing.table;

import javax.swing.JTable;

public class CellRendererParams {

    private JTable table;
    private Object value;
    private boolean isSelected;
    private int row;
    private int column;
    private boolean hasFocus;

    public CellRendererParams() {
    }

    public CellRendererParams(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.value = value;
        this.isSelected = isSelected;
        this.row = row;
        this.column = column;
    }

    public CellRendererParams(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.table = table;
        this.value = value;
        this.isSelected = isSelected;
        this.row = row;
        this.column = column;
        this.hasFocus = hasFocus;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public CellRendererParams setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        return this;
    }

    public JTable getTable() {
        return table;
    }

    public CellRendererParams setTable(JTable table) {
        this.table = table;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public CellRendererParams setValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public CellRendererParams setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        return this;
    }

    public int getRow() {
        return row;
    }

    public CellRendererParams setRow(int row) {
        this.row = row;
        return this;
    }

    public int getColumn() {
        return column;
    }

    public CellRendererParams setColumn(int col) {
        this.column = col;
        return this;
    }

}
