package com.smartg.swing.treetable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.smartg.java.util.AddToList;
import com.smartg.java.util.EventListenerListIterator;

public class Row implements Serializable {

	private static final long serialVersionUID = 8925551346210586101L;

	private int rowNumber;
	private List<Object> data;
	private final Integer id = new Object().hashCode();
	private Integer parentId;

	protected EventListenerList listenerList = new EventListenerList();

	public Row(int rowNumber, Object[] data) {
		this.rowNumber = rowNumber;
		this.data = Arrays.asList(data);
	}

	public void addTableModelListener(TableModelListener l) {
		new AddToList(listenerList).add(TableModelListener.class, l);
	}

	public void removeTableModelListener(TableModelListener l) {
		listenerList.remove(TableModelListener.class, l);
	}

	public void fireTableCellUpdated(int column) {
		TableModelEvent e = new TableModelEvent(null, rowNumber, rowNumber, column);
		EventListenerListIterator<TableModelListener> iterator = new EventListenerListIterator<>(
				TableModelListener.class, listenerList);
		while (iterator.hasNext()) {
			iterator.next().tableChanged(e);
		}
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public void setValueAt(Object value, int column) {
		data.set(column, value);
		fireTableCellUpdated(column);
	}

	public Object getValueAt(int column) {
		return data.get(column);
	}

	public Integer getId() {
		return id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		if (parentId == id) {
			throw new IllegalArgumentException();
		}
		this.parentId = parentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + rowNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Row other = (Row) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (rowNumber != other.rowNumber)
			return false;
		return true;
	}

}
