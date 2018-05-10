package com.smartg.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.smartg.swing.NullMarginButton;
import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeConstraints;

public class TableCellRendererWithButton implements TableCellRenderer {

	private final JComponent panel = new JLabel();
	private final StealthButton button = new StealthButton();
	private TableCellRenderer renderer;
	private final JComponent rendererPanel = new Box(BoxLayout.LINE_AXIS);

	private final RendererFunctionFactory functionFactory = new RendererFunctionFactory();

	public TableCellRendererWithButton(TableCellRenderer renderer) {
		this(renderer, 4);
	}

	public TableCellRendererWithButton(TableCellRenderer renderer, int buttonAlignment) {
		this.renderer = renderer;
		LayoutNode.HorizontalNode root = new LayoutNode.HorizontalNode("root");
		JNodeLayout layout = new JNodeLayout(this.panel, root);

		this.panel.setLayout(layout);
		this.panel.setOpaque(true);
		if (buttonAlignment == 4) {
			this.panel.add(this.rendererPanel, new NodeConstraints("root"));
			LayoutNode.HorizontalNode buttonNode = new LayoutNode.HorizontalNode("button");
			buttonNode.setHorizontalAlignment(NodeAlignment.RIGHT);
			root.add(buttonNode);
			this.panel.add(this.button, new NodeConstraints("button"));
			layout.setHorizontalAlignment(this.rendererPanel, NodeAlignment.RIGHT);
			root.setHorizontalAlignment(NodeAlignment.RIGHT);
			layout.setHorizontalAlignment(this.button, NodeAlignment.RIGHT);
		} else {
			LayoutNode.HorizontalNode buttonNode = new LayoutNode.HorizontalNode("button");
			buttonNode.setHorizontalAlignment(NodeAlignment.LEFT);
			root.add(buttonNode);
			this.panel.add(this.button, new NodeConstraints("button"));

			this.panel.add(this.rendererPanel, new NodeConstraints("root"));
			layout.setHorizontalAlignment(this.rendererPanel, NodeAlignment.LEFT);
			root.setHorizontalAlignment(NodeAlignment.LEFT);
			layout.setHorizontalAlignment(this.button, NodeAlignment.LEFT);
		}
		layout.setHorizontalAlignment(this.button, NodeAlignment.RIGHT);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		CellRendererParams params = new CellRendererParams().setTable(table).setValue(value).setSelected(isSelected)
				.setHasFocus(hasFocus).setRow(row).setColumn(column);

		String string = "";
		if (value != null) {
			string = value.toString();
		}
		this.button.setContentAreaFilled(isButtonContentAreaFilled(params));
		this.button.setBorderPainted(isButtonBorderPainted(params));

		Icon buttonIcon = getButtonIcon(params);
		if (buttonIcon != null) {
			this.button.setIcon(buttonIcon);
		} else {
			String buttonText = getButtonText(params);
			if ((buttonText != null) && (!buttonText.isEmpty())) {
				this.button.setText(buttonText);
			} else {
				this.button.setText("...");
			}
		}
		this.button.setStealthMode(!showButton(params));
		JLabel comp;
		if (isUseValueForButton(params)) {
			comp = (JLabel) this.renderer.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
			this.button.setText(string);
			comp.setVisible(false);
		} else {
			comp = (JLabel) this.renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
			comp.setVisible(true);
		}
		comp.setHorizontalTextPosition(4);
		comp.setHorizontalAlignment(4);

		comp.setOpaque(true);

		Color bg = getBackgroundColor(params);
		this.panel.setBackground(bg);
		comp.setBackground(bg);
		this.rendererPanel.setBackground(bg);
		comp.setBackground(bg);
		this.rendererPanel.add(comp, "Center");

		return this.panel;
	}

	public Color getBackgroundColor(CellRendererParams t) {
		return this.functionFactory.getBackgroundcolor().apply(t);
	}

	public boolean isUseValueForButton(CellRendererParams t) {
		return this.functionFactory.getUseValueForButton().apply(t);
	}

	public String getButtonText(CellRendererParams t) {
		return this.functionFactory.getButtonText().apply(t);
	}

	public Icon getButtonIcon(CellRendererParams t) {
		return this.functionFactory.getButtonIcon().apply(t);
	}

	public boolean isButtonContentAreaFilled(CellRendererParams t) {
		return this.functionFactory.getButtonContentAreaFilled().apply(t);
	}

	public boolean isButtonBorderPainted(CellRendererParams t) {
		return this.functionFactory.getButtonBorderPainted().apply(t);
	}

	public boolean showButton(CellRendererParams t) {
		return this.functionFactory.getShowButton().apply(t);
	}

	public JButton getButton() {
		return this.button;
	}

	public RendererFunctionFactory getFunctionFactory() {
		return this.functionFactory;
	}

	static class StealthButton extends NullMarginButton {

		private static final long serialVersionUID = -9008065882485328141L;
		private boolean stealthMode;

		public void paint(Graphics g) {
			if (!this.stealthMode) {
				super.paint(g);
			}
		}

		public boolean isStealthMode() {
			return this.stealthMode;
		}

		public void setStealthMode(boolean stealthMode) {
			this.stealthMode = stealthMode;
		}
	}

}
