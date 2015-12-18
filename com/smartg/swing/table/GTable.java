package com.smartg.swing.table;

import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * @author Andrey Kuznetsov
 */
public class GTable extends JTable {

    private static final long serialVersionUID = 6875308647587771820L;

    public GTable() {
    }

    public GTable(TableModel dm) {
	super(dm);
    }

    public GTable(TableModel dm, TableColumnModel cm) {
	super(dm, cm);
    }

    public GTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
	super(dm, cm, sm);
    }

    public GTable(int numRows, int numColumns) {
	super(numRows, numColumns);
    }

    public GTable(Vector<?> rowData, Vector<?> columnNames) {
	super(rowData, columnNames);
    }

    public GTable(final Object[][] rowData, final Object[] columnNames) {
	super(rowData, columnNames);
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
	if (orientation == SwingConstants.HORIZONTAL) {
	    int col = columnAtPoint(visibleRect.getLocation());
	    if (direction < 0) {
		if (col == 0) {
		    return 0;
		}
		int width = getCellRect(0, col - 1, false).width;
		return width;
	    }
	    int width = getCellRect(0, col, false).width;
	    return width;
	}
	int row = rowAtPoint(visibleRect.getLocation());
	if (direction < 0) {
	    return getCellRect(row - 1, 0, false).height;
	}
	return getCellRect(row, 0, false).height;
    }
}
