package com.smartg.swing;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeConstraints;

public abstract class TableCellRendererWithButton implements TableCellRenderer {

    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();
    private JButton button = new NullMarginButton();

    public TableCellRendererWithButton() {
	LayoutNode.HorizontalNode root = new LayoutNode.HorizontalNode("root");
	JNodeLayout layout = new JNodeLayout(panel, root);

	panel.setLayout(layout);
	panel.add(label, new NodeConstraints("root"));
	LayoutNode.HorizontalNode buttonNode = new LayoutNode.HorizontalNode("button");
	buttonNode.setHorizontalAlignment(NodeAlignment.RIGHT);
	root.add(buttonNode);
	panel.add(button, new NodeConstraints("button"));

	//label.setPreferredSize(new Dimension(100, 20));
	label.setHorizontalAlignment(SwingUtilities.RIGHT);

	root.setHorizontalAlignment(NodeAlignment.STRETCHED);
	layout.setHorizontalAlignment(label, NodeAlignment.STRETCHED);
	layout.setHorizontalAlignment(button, NodeAlignment.RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	    int row, int column) {
	String string = "";
	if(value != null) {
	    string = value.toString();
	}
	label.setText(string);
	button.setText("...");
	button.setVisible(showButton(table, string, isSelected, hasFocus, row, column));
	if (isSelected) {
	    panel.setBackground(table.getSelectionBackground());
	} else {
	    panel.setBackground(table.getBackground());
	}
	return panel;
    }

    protected abstract boolean showButton(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);

    public JButton getButton() {
	return button;
    }
}