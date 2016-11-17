package com.smartg.swing;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.smartg.java.util.StackTraceUtil;
import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeConstraints;

public abstract class TableCellEditorWithButton extends DefaultCellEditor {

    private static final long serialVersionUID = 9197754785781921417L;

    private JPanel panel = new JPanel();
    private JTextField textField;
    private JButton button = new NullMarginButton();

    public TableCellEditorWithButton(JTextField textField) {
	super(textField);
	this.textField = textField;
	LayoutNode.HorizontalNode root = new LayoutNode.HorizontalNode("root");
	JNodeLayout layout = new JNodeLayout(panel, root);

	panel.setLayout(layout);
	panel.add(textField, new NodeConstraints("root"));
	LayoutNode.HorizontalNode buttonNode = new LayoutNode.HorizontalNode("button");
	buttonNode.setHorizontalAlignment(NodeAlignment.RIGHT);
	root.add(buttonNode);
	panel.add(button, new NodeConstraints("button"));

	//textField.setPreferredSize(new Dimension(100, 20));
	textField.setHorizontalAlignment(SwingUtilities.LEFT);

	root.setHorizontalAlignment(NodeAlignment.STRETCHED);
	layout.setHorizontalAlignment(textField, NodeAlignment.STRETCHED);
	layout.setHorizontalAlignment(button, NodeAlignment.RIGHT);

	this.editorComponent = panel;
	this.delegate = new EditorDelegate() {
	    private static final long serialVersionUID = 7028332554446975905L;

	    public void setValue(Object value) {
		textField.setText((value != null) ? value.toString() : "");
	    }

	    public Object getCellEditorValue() {
		if(textField instanceof JFormattedTextField) {
		    try {
			((JFormattedTextField) textField).commitEdit();
		    } catch (ParseException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING,e.getMessage() + " at " + StackTraceUtil.getStackTraceLine(e));
		    }
		    Object value = ((JFormattedTextField) textField).getValue();
		    if(value != null) {
			return value;
		    }
		    return "";
		}
		return textField.getText();
	    }
	};
	button.addActionListener(delegate);
	button.setRequestFocusEnabled(false);

	setClickCountToStart(1);

	textField.addFocusListener(new FocusAdapter() {
	    @Override
	    public void focusGained(final FocusEvent e) {
		SwingUtilities.invokeLater(() -> textField.selectAll());
	    }
	});
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	delegate.setValue(value);
	String string = String.valueOf(value);
	textField.setText(string);
	button.setText("...");
	button.setVisible(showButton(table, value, isSelected));
	if (isSelected) {
	    panel.setBackground(table.getSelectionBackground());
	} else {
	    panel.setBackground(table.getBackground());
	}
	return panel;
    }

    protected abstract boolean showButton(JTable table, Object value, boolean isSelected);

    public JButton getButton() {
	return button;
    }
}