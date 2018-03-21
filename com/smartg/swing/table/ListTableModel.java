package com.smartg.swing.table;

import java.util.List;

@SuppressWarnings("rawtypes")
public class ListTableModel extends ObjectTableModel<List> {

	private static final long serialVersionUID = 5339647716413344918L;

	public ListTableModel() {
		super(List.class);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		List<?> row = getRow(rowIndex);
		if (row != null) {
			return row.get(columnIndex);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		List<Object> row = getRow(rowIndex);
		if (row != null) {
			row.set(columnIndex, aValue);
		}
	}
}
