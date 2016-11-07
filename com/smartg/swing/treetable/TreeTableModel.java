package com.smartg.swing.treetable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class TreeTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -7352868779102415178L;
    private ArrayList<TreeRow> visibleRows = new ArrayList<>();
    private LinkedHashMap<Integer, TreeRow> rowsByNumber = new LinkedHashMap<>();
    private HashMap<Integer, TreeRow> rowsById = new HashMap<>();
    private TreeRow root;

    private List<Object> columnNames;
    private List<TreeRow> dataVector;

    public TreeTableModel(Object[][] data, Object[] columnNames) {
	this.root = new TreeRow(new Row(-1, new Object[columnNames.length]));
	this.columnNames = Arrays.asList(columnNames);
	this.dataVector = new ArrayList<>();
	int r = 0;
	for (Object[] row : data) {
	    TreeRow tr = new TreeRow(new Row(r++, row));
	    dataVector.add(tr);
	}
	dataVector.forEach(t -> root.addChild(t));
	dataVector.forEach(t -> rowsByNumber.put(t.getRow().getRowNumber(), t));
	dataVector.forEach(t -> rowsById.put(t.getRow().getId(), t));

	visibleRows.addAll(rowsByNumber.values());
    }

    public TreeTableModel(Row[] rows, Object[] columnNames) {
	this.root = new TreeRow(new Row(-1, new Object[columnNames.length]));
	this.columnNames = Arrays.asList(columnNames);
	dataVector = new ArrayList<>();
	Arrays.sort(rows, new Comparator<Row>() {
	    @Override
	    public int compare(Row o1, Row o2) {
		return o1.getRowNumber().compareTo(o2.getRowNumber());
	    }
	});
	for (Row r : rows) {
	    dataVector.add(new TreeRow(r));
	}
	dataVector.forEach(t -> rowsById.put(t.getRow().getId(), t));
	dataVector.forEach(t -> rowsByNumber.put(t.getRow().getRowNumber(), t));

	updateParents();

	visibleRows.addAll(rowsByNumber.values());
    }

    public TreeTableModel(TreeRow[] rows, Object[] columnNames) {
	this.root = new TreeRow(new Row(-1, new Object[columnNames.length]));
	this.columnNames = Arrays.asList(columnNames);
	Arrays.sort(rows, new Comparator<TreeRow>() {
	    @Override
	    public int compare(TreeRow o1, TreeRow o2) {
		return o1.getRow().getRowNumber().compareTo(o2.getRow().getRowNumber());
	    }
	});

	dataVector = new ArrayList<>(Arrays.asList(rows));
	dataVector.forEach(t -> rowsById.put(t.getRow().getId(), t));
	dataVector.forEach(t -> rowsByNumber.put(t.getRow().getRowNumber(), t));

	updateParents();

	visibleRows.addAll(rowsByNumber.values());
    }

    private void updateParents() {
	dataVector.forEach(t -> {
	    if (t.getParent() == null) {
		Integer parentId = t.getRow().getParentId();
		if (parentId == null) {
		    root.addChild(t);
		} else {
		    TreeRow parent = rowsById.get(parentId);
		    if (parent != null) {
			parent.addChild(t);
		    }
		}
	    }
	});
    }

    @Override
    public int getRowCount() {
	return visibleRows.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
	return visibleRows.get(rowIndex).getRow().getData().get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	visibleRows.get(rowIndex).getRow().getData().set(columnIndex, aValue);
    }

    @Override
    public int getColumnCount() {
	return columnNames.size();
    }

    @Override
    public String getColumnName(int column) {
	Object obj = columnNames.get(column);
	if (obj != null) {
	    return obj.toString();
	}
	return super.getColumnName(column);
    }

    public boolean indentRow(int rowIndex) {
	if (rowIndex == 0) {
	    return false;
	}
	TreeRow prev = rowsByNumber.get(rowIndex - 1);
	TreeRow row = rowsByNumber.get(rowIndex);
	if (row.getParent() != root) {
	    return false;
	}
	row.setParent(prev);
	return true;
    }

    public boolean outdentRow(int rowIndex) {
	TreeRow row = rowsByNumber.get(rowIndex);
	TreeRow parent = row.getParent();
	if (parent != root) {
	    row.setParent(parent.getParent());
	}
	return true;
    }

    public int getCountToRoot(int row) {
	TreeRow tr = visibleRows.get(row);
	return tr.getCountToRoot();
    }

    public boolean isCollapsed(int row) {
	TreeRow tr = visibleRows.get(row);
	return tr.isCollapsed();
    }

    private TreeRow getTreeRow(int rowIndex) {
	return visibleRows.get(rowIndex);
    }

    public boolean isLeaf(int row) {
	return visibleRows.get(row).isLeaf();
    }

    public void collapseRow(int rowNumber) {
	TreeRow tr = getTreeRow(rowNumber);
	if (!tr.isCollapsed()) {
	    setCollapsed(tr, true);
	}
    }

    public void expandRow(int rowNumber) {
	TreeRow tr = getTreeRow(rowNumber);
	if (tr.isCollapsed()) {
	    setCollapsed(tr, false);
	}
    }

    private void setCollapsed(TreeRow tr, boolean collapsed) {
	if (!tr.isLeaf()) {
	    tr.collapsed = collapsed;
	    ArrayList<TreeRow> children = tr.getChildren();
	    if (collapsed) {
		visibleRows.removeAll(children);
		int count = tr.getChildCount();
		Iterator<TreeRow> iter = children.iterator();
		while (iter.hasNext()) {
		    TreeRow next = iter.next();
		    if (!next.isLeaf()) {
			ArrayList<TreeRow> nextChildren = next.getChildren();
			for (TreeRow r : nextChildren) {
			    if (visibleRows.contains(r)) {
				visibleRows.remove(r);
				count++;
			    }
			}
		    }
		}
		final int indexOf = visibleRows.indexOf(this) + 1;
		fireTableRowsDeleted(indexOf, indexOf + count);
	    } else {
		final int indexOf = visibleRows.indexOf(tr) + 1;
		visibleRows.addAll(indexOf, children);
		int count = tr.getChildCount();
		for (TreeRow t : children) {
		    if (!t.isLeaf() && !t.isCollapsed()) {
			visibleRows.addAll(visibleRows.indexOf(t) + 1, t.getChildren());
			count += t.getChildCount();
		    }
		}
		fireTableRowsInserted(indexOf, indexOf + count);
	    }
	}
    }
}
