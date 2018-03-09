package com.smartg.swing.combobox;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public abstract class GComboBoxEditor<E> {

	private boolean finished;
	private int clickCount = 1;

	private EventListenerList listenerList = new EventListenerList();

	protected GComboBoxEditorPanel<E> component;

	public GComboBoxEditor(GComboBoxEditorPanel<E> comp) {
		this.component = comp;
		component.getList().addMouseListener(new ListClickHandler());
		component.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				component.list.clearSelection();
				fireChangeEvent();
				if (e.getClickCount() >= getClickCount()) {
					finishEdit();
				}
			}
		});

		component.getList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					fireChangeEvent();
				}
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

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
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

	protected class ListClickHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= getClickCount()) {
				finishEdit();
				fireChangeEvent();
			}
		}
	}
}
