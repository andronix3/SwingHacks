/**
 * 
 */
package com.smartg.swing.combobox;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

public class SingleValueComboBoxModel<E> implements ComboBoxModel<E> {

    private E value;
    
    public Object getSelectedItem() {
        return value;
    }

    public void setSelectedItem(Object anItem) {

    }

    public void addListDataListener(ListDataListener l) {

    }

    public E getElementAt(int index) {
        return value;
    }

    public int getSize() {
        return 1;
    }

    public void removeListDataListener(ListDataListener l) {

    }

    public void setValue(E value) {
	this.value = value;
    }

    public Object getValue() {
	return value;
    }
}