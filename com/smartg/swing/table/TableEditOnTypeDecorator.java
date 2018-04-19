package com.smartg.swing.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

public class TableEditOnTypeDecorator {

	public static void decorate(JTable table) {
		new TableEditOnTypeDecorator(table);
	}

	private final JTable table;
	private InputMap inputMap;
	private ActionMap actionMap;
	private FocusLostHandler focusLostHandler = new FocusLostHandler();

	public TableEditOnTypeDecorator(JTable table) {
		this.table = table;

		inputMap = table.getInputMap(JTable.WHEN_FOCUSED);
		actionMap = table.getActionMap();

		table.setSurrendersFocusOnKeystroke(true);

		for (int i = KeyEvent.VK_0; i <= KeyEvent.VK_9; i++) {
			registerAction(i, (char) i, 0);
		}
		for (int i = KeyEvent.VK_NUMPAD0; i <= KeyEvent.VK_NUMPAD9; i++) {
			registerAction(i, (char) (KeyEvent.VK_0 - KeyEvent.VK_NUMPAD0 + i), 0);
		}
		for (int i = KeyEvent.VK_A; i <= KeyEvent.VK_Z; i++) {
			registerAction(i, Character.toLowerCase((char) i), 0);
			registerAction(i, Character.toUpperCase((char) i), KeyEvent.SHIFT_DOWN_MASK);
		}
	}

	private void registerAction(int vk, char c, int modifiers) {
		KeyStroke ks = KeyStroke.getKeyStroke(vk, modifiers);
		String name = "LetterAction#" + c + "" + modifiers;
		inputMap.put(ks, name);
		actionMap.put(name, new StartEditAction("" + c));
	}

	private static class FocusLostHandler extends FocusAdapter {

		@Override
		public void focusLost(FocusEvent e) {
			e.getComponent().removeFocusListener(this);
			JTextComponent tc = (JTextComponent) e.getComponent();
			tc.putClientProperty("ExcludeTextFieldSelectAll", null);
		}
	}

	private class StartEditAction extends AbstractAction {

		private static final long serialVersionUID = 1198439978810227447L;
		private final String action;

		public StartEditAction(String action) {
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			if (table.isEditing()) {
				table.getCellEditor().cancelCellEditing();
			}

			// if (!table.isEditing()) {
			int selectedColumn = table.getSelectedColumn();
			int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0 && selectedColumn >= 0 && table.isCellEditable(selectedRow, selectedColumn)) {
				if (table.editCellAt(selectedRow, selectedColumn)) {
					TableCellEditor editor = table.getCellEditor();
					DefaultCellEditor cellEditor = (DefaultCellEditor) editor;
					Component editorComponent = cellEditor.getComponent();
					if (editorComponent instanceof JTextComponent) {
						JTextComponent component = (JTextComponent) editorComponent;
						component.putClientProperty("ExcludeTextFieldSelectAll", Boolean.TRUE);
						component.addFocusListener(focusLostHandler);
						SwingUtilities.invokeLater(() -> {
							component.setText(action);
						});
					}
				}
			}
			// }
		}
	}
}
