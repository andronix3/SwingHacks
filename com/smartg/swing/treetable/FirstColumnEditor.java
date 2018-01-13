package com.smartg.swing.treetable;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.smartg.swing.table.TableCellEditorWithButton;

public class FirstColumnEditor extends TableCellEditorWithButton {
	private static final long serialVersionUID = -2213519055045903729L;
	private HandleIcon collapsedIcon;
	private HandleIcon expandedIcon;
	private Icon buttonIcon;
	private final HandleIcon leafIcon;
	private final TreeTableModel model;

	public FirstColumnEditor(TreeTableModel model, int align) {
		this(model, HandleIcon.getCollapsedImage(), HandleIcon.getExpandedImage(), align);
	}

	public FirstColumnEditor(TreeTableModel model, Icon collapsedImage, Icon expandedImage, int align) {
		super(new JTextField(), align);
		this.model = ((TreeTableModel) Objects.requireNonNull(model));
		collapsedIcon = new HandleIcon(collapsedImage);
		expandedIcon = new HandleIcon(expandedImage);
		leafIcon = new HandleIcon(new ImageIcon(new BufferedImage(10, 10, 2)));
	}

	public void setCollapsedImage(Image img) {
		collapsedIcon = ((HandleIcon) Objects.requireNonNull(new HandleIcon(new ImageIcon(img))));
	}

	public void setExpandedImage(Image img) {
		expandedIcon = ((HandleIcon) Objects.requireNonNull(new HandleIcon(new ImageIcon(img))));
	}

	protected String getButtonText() {
		return null;
	}

	protected Icon getButtonIcon() {
		return buttonIcon;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		int countToRoot = model.getCountToRoot(row);
		if (!model.isLeaf(row)) {
			if (model.isCollapsed(row)) {
				collapsedIcon.setIndent(countToRoot * 10);
				buttonIcon = collapsedIcon;
			} else {
				expandedIcon.setIndent(countToRoot * 10);
				buttonIcon = expandedIcon;
			}
		} else {
			leafIcon.setIndent(countToRoot * 10);
			buttonIcon = leafIcon;
		}

		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	@Override
	protected boolean showButton(JTable table, Object value, boolean isSelected, int row, int column) {
		if (column != 0) {
			return false;
		}
		TreeTableModel model = (TreeTableModel) table.getModel();
		return !model.isLeaf(row);
	}

}
