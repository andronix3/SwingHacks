package com.smartg.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/**
 * List/ComboBox Model which adds support for changing visibility of items.
 * 
 * @author andro
 *
 * @param <T>
 */
public class XListModel<T> extends AbstractListModel<T> implements MutableComboBoxModel<T> {
    static class XItem<T> {
	boolean visible = true;
	int index;

	T obj;

	public XItem(T obj) {
	    this.obj = obj;
	}

	@Override
	public String toString() {
	    return String.valueOf(obj) + " " + visible;
	}
    }

    private static final long serialVersionUID = -5804334694323093338L;

    private ArrayList<XListModel.XItem<T>> elements = new ArrayList<>();
    private ArrayList<Integer> indexes = new ArrayList<>();
    private HashMap<Object, XListModel.XItem<T>> map = new HashMap<>();
    private Object selectedItem;

    private boolean valid;

    public XListModel() {
    }

    public XListModel(List<T> elements) {
	add(elements);
    }

    @Override
    public int getSize() {
	if (!valid) {
	    reindex();
	}
	return indexes.size();
    }

    private void reindex() {
	indexes.clear();
	int index = 0;
	for (XListModel.XItem<T> t : elements) {
	    if (t.visible) {
		indexes.add(index);
		t.index = index;
	    }
	    index++;
	}
	valid = true;
    }

    public int indexOf(T t) {
	if (!valid) {
	    reindex();
	}
	return map.get(t).index;
    }

    @Override
    public T getElementAt(int index) {
	if (!valid) {
	    reindex();
	}
	if (index < 0 || index >= indexes.size()) {
	    return null;
	}
	return elements.get(indexes.get(index)).obj;
    }

    private void add(List<T> list) {
	list.stream().map(t -> new XListModel.XItem<T>(t)).forEach(t -> {
	    map.put(t.obj, t);
	    elements.add(t);
	});
	setVisible(true, list);
    }

    public void setVisible(boolean visible, List<T> items) {
	ArrayList<XListModel.XItem<T>> list = new ArrayList<>();
	for (T item : items) {
	    XListModel.XItem<T> t = map.get(item);
	    if (t.visible != visible) {
		t.visible = visible;
		list.add(t);
	    }
	}
	if (list.isEmpty()) {
	    return;
	}
	valid = false;
	if (visible) {
	    // indexes are invalid, so reindex before
	    reindex();
	    list.stream().forEach(t -> fireIntervalAdded(this, t.index, t.index));
	} else {
	    // we have to collect indexes, before we reindex and fire events
	    ArrayList<Integer> indexes = new ArrayList<>();
	    list.stream().forEach(t -> indexes.add(t.index));
	    reindex();
	    indexes.stream().forEach(t -> fireIntervalRemoved(this, t, t));
	}
    }

    public void setVisible(boolean visible, T item) {
	XListModel.XItem<T> t = map.get(item);
	if (visible != t.visible) {
	    valid = false;
	    t.visible = visible;
	    if (visible) {
		reindex();
		fireIntervalAdded(this, t.index, t.index);
	    } else {
		int index = t.index;
		reindex();
		fireIntervalRemoved(this, index, index);
	    }
	}
    }

    @Override
    public void setSelectedItem(Object anItem) {
	if (!Objects.equals(selectedItem, anItem)) {
	    this.selectedItem = anItem;
	    fireContentsChanged(this, -1, -1);
	}
    }

    @Override
    public Object getSelectedItem() {
	return selectedItem;
    }

    @Override
    public void addElement(T item) {
	XListModel.XItem<T> t = new XListModel.XItem<T>(item);
	elements.add(t);
	map.put(item, t);
	reindex();
	fireIntervalAdded(this, t.index, t.index);
    }

    @Override
    public void removeElement(Object obj) {
	XListModel.XItem<T> t = map.remove(obj);
	if (t != null) {
	    elements.remove(t);
	    int index = t.index;
	    reindex();
	    fireIntervalRemoved(this, index, index);
	}
    }

    @Override
    public void insertElementAt(T t, int index) {
	if (index < 0 || index >= indexes.size()) {
	    return;
	}
	XListModel.XItem<T> item = new XListModel.XItem<>(t);
	int n = indexes.get(index);
	map.put(t, item);
	elements.add(n, item);
	reindex();
	fireIntervalAdded(this, index, index);
    }

    @Override
    public void removeElementAt(int index) {
	if (index < 0 || index >= indexes.size()) {
	    return;
	}
	int n = indexes.get(index);
	XListModel.XItem<T> t = elements.get(n);
	elements.remove(n);
	map.remove(t.obj);
	reindex();
	fireIntervalRemoved(this, index, index);
    }
}