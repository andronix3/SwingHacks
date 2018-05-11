package com.smartg.swing.combobox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import com.smartg.java.util.AddToList;
import com.smartg.java.util.EventListenerListIterator;

public class GComboBoxEditorPanel<E> extends JPanel {

	private static final long serialVersionUID = 4754243150369611690L;

	public GComboBoxEditorPanel() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				finishEdit();
				fireChange();
			}
		});
	}

	public void addActionListener(ActionListener e) {
		new AddToList(listenerList).add(ActionListener.class, e);
	}

	void fireActionEvent(String command) {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
		EventListenerListIterator<ActionListener> iterator = new EventListenerListIterator<>(ActionListener.class,
				listenerList);
		while (iterator.hasNext()) {
			iterator.next().actionPerformed(e);
		}
	}

	protected void finishEdit() {
		fireActionEvent("finishEdit");
	}

	protected void fireChange() {
		fireActionEvent("fireChange");

	}
}
