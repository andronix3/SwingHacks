package com.smartg.swing.table;

import java.awt.Color;

import javax.swing.JTable;

public interface TableCellRendererColorSupplier {
	Color getColor(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);
}
