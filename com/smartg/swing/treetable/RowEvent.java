package com.smartg.swing.treetable;

import java.util.EventObject;

public class RowEvent extends EventObject {
	private static final long serialVersionUID = 9170559735218170348L;

	private int column;
	
	public RowEvent(Row source, int column) {
		super(source);
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return ((Row)source).getRowNumber();
	}
}
