package com.smartg.swing.treetable;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FirstColumnRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 4844210345740958732L;
    private HandleIcon collapsedIcon;
    private HandleIcon expandedIcon;
    private final HandleIcon leafIcon;
    private final TreeTableModel model;

    public FirstColumnRenderer(TreeTableModel model, Image collapsedImage,
	    Image expandedImage) {
	this.model = Objects.requireNonNull(model);
	collapsedIcon = new HandleIcon(collapsedImage);
	expandedIcon = new HandleIcon(expandedImage);
	leafIcon = new HandleIcon(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));
    }

    public void setCollapsedImage(Image img) {
	this.collapsedIcon = Objects.requireNonNull(new HandleIcon(img));
    }

    public void setExpandedImage(Image img) {
	this.expandedIcon = Objects.requireNonNull(new HandleIcon(img));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	    int row, int column) {
	JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	int countToRoot = model.getCountToRoot(row);
	if (!model.isLeaf(row)) {
	    if (model.isCollapsed(row)) {
		collapsedIcon.setIndent(countToRoot * 10);
		label.setIcon(collapsedIcon);
	    } else {
		expandedIcon.setIndent(countToRoot * 10);
		label.setIcon(expandedIcon);
	    }
	} else {
	    leafIcon.setIndent(countToRoot * 10);
	    label.setIcon(leafIcon);
	}
	return label;
    }
}
