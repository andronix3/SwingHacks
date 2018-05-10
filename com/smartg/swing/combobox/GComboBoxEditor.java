package com.smartg.swing.combobox;

import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import com.smartg.java.util.AddToList;

public abstract class GComboBoxEditor<E> {

	private boolean finished;
	private int clickCount = 1;

	private EventListenerList listenerList = new EventListenerList();

	protected GComboBoxEditorPanel<E> component;

	public GComboBoxEditor(GComboBoxEditorPanel<E> comp) {
		this.component = comp;
		comp.addActionListener(e-> {
			switch(e.getActionCommand()) {
			case "finishEdit":
				finishEdit();
				break;
			case "fireChange":
				fireChangeEvent();
				break;
			}
		});
	}

	public abstract E getValue();

	public abstract ListCellRenderer<E> getRenderer();

	public GComboBoxEditorPanel<E> getComponent() {
		return component;
	}

	public final boolean editFinished() {
		return finished;
	}

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public void addChangeListener(ChangeListener e) {
		new AddToList(listenerList).add(ChangeListener.class, e);
	}

	public void removeChangeListener(ChangeListener e) {
		listenerList.remove(ChangeListener.class, e);
	}

	private void fireChangeEvent() {
		ChangeListener[] listeners = listenerList.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(this);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].stateChanged(e);
		}
	}

	/**
	 * Should be called by implementing classes to indicate that Editor can be
	 * closed. Moreover ChangeEvent must be fired after finishEdit() to close
	 * GComboBox popup.
	 * 
	 * @return
	 */
	protected void finishEdit() {
		finished = true;
	}

	final void startEdit() {
		finished = false;
	}
}
