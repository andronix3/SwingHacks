package com.smartg.swing.table;

public class GSTableModel<T> extends ObjectTableModel<T> {

	private static final long serialVersionUID = -717495482846320658L;

	@SuppressWarnings("rawtypes")
	private Getter[] getters;
	@SuppressWarnings("rawtypes")
	private Setter[] setters;

	public GSTableModel(Class<? extends T> classe) {
		super(classe);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getters[columnIndex].get(getRow(rowIndex));
	}
	
	@SuppressWarnings("unchecked")
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		setters[columnIndex].set(aValue, getRow(rowIndex));
    }

	@SuppressWarnings("rawtypes")
	public void setGetters(Getter... getters) {
		this.getters = getters;
	}

	@SuppressWarnings("rawtypes")
	public void setSetters(Setter... setters) {
		this.setters = setters;
	}
}
