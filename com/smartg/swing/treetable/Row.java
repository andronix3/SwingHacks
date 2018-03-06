package com.smartg.swing.treetable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.event.EventListenerList;

import com.smartg.java.util.AddToList;
import com.smartg.java.util.EventListenerListIterator;

public class Row implements Serializable {

	private static final long serialVersionUID = 8925551346210586101L;
	
	private int rowNumber;
	private List<Object> data;
	private final Long id = (long) new Object().hashCode();
	private Long parentId;
	private Long sieblingId;

	protected EventListenerList listenerList = new EventListenerList();

	public Row(int rowNumber, Object[] data) {
		this.rowNumber = rowNumber;
		this.data = Arrays.asList(data);
	}

	public void addRowListener(RowListener l) {
		new AddToList(listenerList).add(RowListener.class, l);
	}

	public void removeTableModelListener(RowListener l) {
		listenerList.remove(RowListener.class, l);
	}

	public void fireTableCellUpdated(int column) {
		RowEvent e = new RowEvent(this, column);
		EventListenerListIterator<RowListener> iterator = new EventListenerListIterator<>(RowListener.class,
				listenerList);
		while (iterator.hasNext()) {
			iterator.next().rowChanged(e);
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

	public Long getId() {
		return id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		if (Objects.equals(parentId, id) || Objects.equals(parentId, sieblingId)) {
			throw new IllegalArgumentException();
		}
		this.parentId = parentId;
	}

	public Long getSieblingId() {
		return sieblingId;
	}

	public void setSieblingId(Long sieblingId) {
		if (Objects.equals(sieblingId, id) || Objects.equals(sieblingId, parentId)) {
			throw new IllegalArgumentException();
		}
		this.sieblingId = sieblingId;
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
