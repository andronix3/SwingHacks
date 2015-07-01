package com.smartg.swing.combobox;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CellRenderers {

    public static class NoSelection_ListCellRenderer<E> implements ListCellRenderer<E>{

	DefaultListCellRenderer renderer = new DefaultListCellRenderer();

	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
	    return renderer.getListCellRendererComponent(list, value, index, false, false);
	}
    }

    public static class Selection_ListCellRenderer<E> implements ListCellRenderer<E>{

	DefaultListCellRenderer renderer = new DefaultListCellRenderer();

	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
	    return renderer.getListCellRendererComponent(list, value, index, true, false);
	}
    }

    public static class NoEmptySelection_ListCellRenderer<E> implements ListCellRenderer<E> {

	DefaultListCellRenderer renderer = createRenderer();

	protected DefaultListCellRenderer createRenderer() {
	    return new DefaultListCellRenderer();
	}

	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
	    String s = (String) value;
	    if (s.isEmpty()) {
		return renderer.getListCellRendererComponent(list, value, index, false, false);
	    }
	    return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	}
    }
}
