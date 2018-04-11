package com.smartg.swing.table;

import java.util.Objects;

import com.smartg.java.util.StackTraceUtil;

public class GSTableModel<T> extends ObjectTableModel<T> {

	private static final long serialVersionUID = -717495482846320658L;

	@SuppressWarnings("rawtypes")
	private Getter[] getters = {};
	@SuppressWarnings("rawtypes")
	private Setter[] setters = {};

	public GSTableModel(Class<? extends T> classe) {
		super(classe);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Getter getter = getters[columnIndex];
		if(getter != null) {
			return getter.get(getRow(rowIndex));
		}
		StackTraceUtil.warning("No getter found for column " + columnIndex );
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Setter setter = setters[columnIndex];
		if(setter != null) {
			setter.set(aValue, getRow(rowIndex));
		}
		else {
			StackTraceUtil.warning("No setter found for column " + columnIndex );
		}
    }

	@SuppressWarnings("rawtypes")
	public void setGetters(Getter... getters) {
		this.getters = Objects.requireNonNull(getters);
	}

	@SuppressWarnings("rawtypes")
	public void setSetters(Setter... setters) {
		this.setters = Objects.requireNonNull(setters);
	}
}
