package com.smartg.swing;

import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

public class SelectNoneEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	public SelectNoneEditor() {
        this(new JTextField());
    }

    public SelectNoneEditor(JTextField textField) {
        super(textField);
        textField.removeActionListener(delegate);
        //fix for TextFieldSelectAll mod
        textField.putClientProperty("ExcludeTextFieldSelectAll", Boolean.TRUE);
        delegate = new EditorDelegate() {
			private static final long serialVersionUID = 1L;

			@Override
            public void setValue(Object value) {
                textField.setText((value != null) ? value.toString() : "");
            }

            @Override
            public Object getCellEditorValue() {
                return textField.getText();
            }

            @Override
            public boolean shouldSelectCell(EventObject anEvent) {
                return false;
            }
        };
        textField.addActionListener(delegate);
    }
}