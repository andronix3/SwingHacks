/**
 * 
 */
package com.smartg.swing;

import javax.swing.AbstractListModel;

public class FixedListModel<E> extends AbstractListModel<E> {

    private static final long serialVersionUID = -4661854593273156025L;
    E[] values;

    public FixedListModel(E[] values) {
        this.values = values;
    }

    public E getElementAt(int index) {
        return values[index];
    }

    public int getSize() {
        return values.length;
    }
}