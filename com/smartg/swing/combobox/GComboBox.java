package com.smartg.swing.combobox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.smartg.swing.combobox.GComboBoxEditor;
import com.smartg.swing.combobox.SingleValueComboBoxModel;

public class GComboBox<E> extends JComboBox<E> {

	private static final long serialVersionUID = 4815623694250096742L;

	private GComboBoxEditor<E> comboBoxEditor;

	private JComponent cpanel;

	private SingleValueComboBoxModel<E> model = new SingleValueComboBoxModel<E>();

	private Timer t = new Timer(200, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			hidePopup();
			pmenu.show(GComboBox.this, 0, getHeight());
		}
	});

	private JPopupMenu pmenu = new JPopupMenu();

	public GComboBox(GComboBoxEditor<E> ctrl) {
		setModel(model);
		this.comboBoxEditor = ctrl;
		model.setValue(comboBoxEditor.getValue());
		cpanel = comboBoxEditor.getComponent();
		setRenderer(comboBoxEditor.getRenderer());
		comboBoxEditor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				model.setValue(comboBoxEditor.getValue());
				fireActionEvent();
				repaint();
				if (comboBoxEditor.editFinished()) {
					pmenu.setVisible(false);
				}
			}
		});

		pmenu.add(cpanel);

		addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				comboBoxEditor.startEdit();
				t.setRepeats(false);
				t.start();
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
	}

	public GComboBoxEditor<E> getComboBoxEditor() {
		return comboBoxEditor;
	}
}
