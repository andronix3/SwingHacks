package com.smartg.swing.combobox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class GComboBoxEditorPanel<E> extends JPanel {

	private static final long serialVersionUID = -2852265896971447462L;

	protected boolean drawGrid = true;

	protected int horizontalAlignment = SwingConstants.CENTER;
	protected final Point hover = new Point();

	protected Color hoverBG;

	final protected JList<E> list = new JList<E>();

	private boolean drawSelectionBorder = true;

	private Color selectedBorderColor = Color.MAGENTA;

	public GComboBoxEditorPanel() {
		list.addMouseMotionListener(new HoverHandler());
		list.setCellRenderer(new CellBorderRenderer<E>());
	}

	public JList<E> getList() {
		return list;
	}

	public Color getSelectedBorderColor() {
		return selectedBorderColor;
	}

	public boolean isDrawSelectionBorder() {
		return drawSelectionBorder;
	}

	public void setCellSize(int size) {
		list.setFixedCellHeight(size);
		list.setFixedCellWidth(size);
	}

	public void setDrawSelectionBorder(boolean drawSelectionBorder) {
		this.drawSelectionBorder = drawSelectionBorder;
	}

	public void setSelectedBorderColor(Color selectedBorderColor) {
		this.selectedBorderColor = selectedBorderColor;
	}

	protected Color createHoverColor(Color c) {
		int r = (c.getRed() + 255) / 2;
		int g = (c.getGreen() + 255) / 2;
		int b = (c.getBlue() + 255) / 2;
		return new Color(r, g, b);
	}

	protected void drawCellBorder(Graphics g, Dimension d, int x, int y) {
		Color c = getCellBorderColor(x, y);
		if (c != null) {
			g.setColor(c);
		} else {
			g.setColor(Color.DARK_GRAY);
		}
		g.drawLine(d.width - 1, 0, d.width - 1, d.height);
		g.drawLine(0, d.height - 1, d.width, d.height - 1);
	}

	protected Color getCellBackground(int x, int y, boolean selected) {
		if (!selected && isHover(x, y)) {
			if (hoverBG == null) {
				hoverBG = createHoverColor(getList().getSelectionBackground());
			}
			return hoverBG;
		}
		return null;
	}

	/**
	 * @param x
	 *            horizontal index of list cell
	 * @param y
	 *            vertical coordinate of list cell
	 * 
	 */
	protected Color getCellBorderColor(int x, int y) {
		return null;
	}

	/**
	 * @param x
	 *            horizontal index of list cell
	 * @param y
	 *            vertical coordinate of list cell
	 * @param selected
	 *            true if cell is currently selected, false otherwise
	 * 
	 */
	protected Color getCellForeground(int x, int y, boolean selected) {
		return null;
	}

	protected int getHorizontalCellCount() {
		return list.getModel().getSize() / list.getVisibleRowCount();
	}

	protected boolean isHover(int x, int y) {
		return hover.x == x && hover.y == y;
	}

	protected class CellBorderRenderer<T> extends CellRenderers.NoEmptySelection_ListCellRenderer<T> {

		private int cx, cy;

		@Override
		public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			c.setHorizontalAlignment(horizontalAlignment);
			int w = getHorizontalCellCount();
			cy = index / w;
			cx = index - w * cy;
			Color c0 = getCellBackground(cx, cy, isSelected);
			if (c0 != null) {
				c.setBackground(c0);
			} else {
				c.setBackground(list.getBackground());
			}
			c0 = getCellForeground(cx, cy, isSelected);
			if (c0 != null) {
				c.setForeground(c0);
			} else {
				c.setForeground(list.getForeground());
			}
			c.setBorder(null);

			if (isSelected && drawSelectionBorder) {
				Color borderColor = getSelectedBorderColor();
				if (borderColor != null) {
					c.setBorder(new LineBorder(borderColor, 2));
				}
			}

			return c;
		}

		protected DefaultListCellRenderer createRenderer() {
			return new DefaultListCellRenderer() {

				private static final long serialVersionUID = -3830548117177299644L;

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					if (drawGrid) {
						drawCellBorder(g, getSize(), cx, cy);
					}
				}
			};
		}
	}

	protected class HoverHandler extends MouseAdapter {
		private int lastIndex;
		private final Point p = new Point();

		@Override
		public void mouseMoved(MouseEvent e) {
			p.x = e.getX();
			p.y = e.getY();

			int index = getList().locationToIndex(p);
			int w = getHorizontalCellCount();
			int cy = index / w;
			int cx = index - w * cy;
			hover.x = cx;
			hover.y = cy;
			if (index != lastIndex) {
				getList().repaint();
			}
			lastIndex = index;
		}
	}
}
