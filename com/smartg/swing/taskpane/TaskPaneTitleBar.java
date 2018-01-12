package com.smartg.swing.taskpane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.smartg.swing.RainLayout;
import com.smartg.swing.taskpane.DoubleArrawIcon.Direction;
import com.smartg.swing.titlebar.BasicTitleBar;

/**
 * @author Andrey Kuznetsov
 */
public class TaskPaneTitleBar extends BasicTitleBar {

	private static final long serialVersionUID = 7949533684689149849L;
	Color firstColor = new Color(0xFFFAFAFA);
	Color secondColor = new Color(0xFFC7D4F7);

	Color fg = new Color(0xFF215DC6);
	Color activeFg = new Color(0xFF4288FF);

	TaskPaneIcon arrowIcon = new TaskPaneArrowIcon(Direction.DOWN);

	GradientPaint paint;
	boolean paintValid;

	JLabel titleLabel;

	boolean _opaque;

	TaskPane taskPane;

	MutableCompoundBorder border = new MutableCompoundBorder();

	public TaskPaneTitleBar(TaskPane tp, String title) {
		super(RainLayout.X_AXIS);
		super.setOpaque(false);
		this.taskPane = tp;
		super.setBorder(border);
		setBorder(new TitleBorder());

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				paintValid = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				titleLabel.setForeground(activeFg);
				arrowIcon.setForeground(activeFg);
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				titleLabel.setForeground(fg);
				arrowIcon.setForeground(fg);
				repaint();
			}
		});

		titleLabel = new JLabel(title) {

			private static final long serialVersionUID = -7518112177247087379L;

			@Override
			public void updateUI() {
				super.updateUI();
				Font font = getFont();
				if (font != null) {
					setFont(font.deriveFont(Font.BOLD, font.getSize()));
				}
			}
		};

		titleLabel.setForeground(fg);
		add(titleLabel, "title", BasicTitleBar.STRETCH_GROW, 1);
		add(new JLabel(arrowIcon), "arrow", BasicTitleBar.STRETCH_NONE, 3);

		getComponent("title").setIgnoreMouse(true);
		getComponent("arrow").setIgnoreMouse(true);
		RainLayout layout = (RainLayout) getLayout();
		layout.setVgap(5);
	}

	protected GradientPaint getPaint() {
		if (paint == null || !paintValid) {
			paint = new GradientPaint(0, 0, firstColor, getWidth() / 2, 0, secondColor);
		}
		return paint;
	}

	@Override
	public void setOpaque(boolean isOpaque) {
		_opaque = isOpaque;
	}

	public void setExpanded(boolean expanded) {
		arrowIcon.setExpanded(expanded);
	}

	boolean iconLeft;

	public boolean isIconLeft() {
		return iconLeft;
	}

	public Color getFirstColor() {
		return firstColor;
	}

	public void setFirstColor(Color firstColor) {
		this.firstColor = firstColor;
	}

	public Color getSecondColor() {
		return secondColor;
	}

	public void setSecondColor(Color secondColor) {
		this.secondColor = secondColor;
	}

	/**
	 * icons at left or right side of title bar
	 * 
	 * @param iconLeft
	 */
	public void setIconLeft(boolean iconLeft) {
		this.iconLeft = iconLeft;
		RainLayout layout = (RainLayout) getLayout();
		layout.setReversedComponentOrder(iconLeft);
		invalidate();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		Composite composite = g2D.getComposite();
		g2D.setPaintMode();
		super.paint(g2D);
		g2D.setComposite(composite);
	}

	@Override
	public void setBorder(Border border) {
		this.border.setInsideBorder(border);
	}

	@Override
	protected void paintBorder(Graphics g) {
		if (taskPane.isBorderPainted()) {
			super.paintBorder(g);
		}
	}

	public Insets getMargin() {
		Border b = border.getOutsideBorder();
		if (b != null) {
			return b.getBorderInsets(this);
		}
		return new Insets(0, 0, 0, 0);
	}

	public void setMargin(Insets margin) {
		if (margin == null) {
			margin = new Insets(0, 0, 0, 0);
		}
		EmptyBorder outsideBorder = new EmptyBorder(margin);
		border.setOutsideBorder(outsideBorder);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (_opaque) {
			g.setColor(firstColor);
			int w = getWidth() / 2;
			if (_opaque) {
				g.drawLine(2, 1, w, 1);
				g.drawLine(1, 2, w, 2);
				g.fillRect(0, 3, w, getHeight() - 3);
			}
			Graphics2D g2d = (Graphics2D) g.create(w, 0, w, getHeight());
			Paint p = getPaint();
			g2d.setPaint(p);
			if (_opaque) {
				g2d.drawLine(0, 1, w - 3, 1);
				g2d.drawLine(0, 2, w - 2, 2);
				g2d.fillRect(0, 3, w, getHeight() - 3);
			}
			g2d.dispose();
		} else {
			if (taskPane._opaque) {
				g.setColor(getBackground());
				int w = getWidth() / 2;
				g.drawLine(2, 1, w, 1);
				g.drawLine(1, 2, w, 2);
				g.fillRect(0, 3, w, getHeight() - 3);
				Graphics2D g2d = (Graphics2D) g.create(w, 0, w, getHeight());
				g2d.drawLine(0, 1, w - 3, 1);
				g2d.drawLine(0, 2, w - 2, 2);
				g2d.fillRect(0, 3, w, getHeight() - 3);
				g2d.dispose();
			}
		}
	}

	class TitleBorder extends AbstractBorder {
		/**
			 * 
			 */
		private static final long serialVersionUID = 7025428754271121432L;

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(firstColor);
			int w = getWidth() / 2;
			int h = getHeight();
			g.drawLine(3, 0, w, 0);
			g.drawLine(0, 3, 0, h - 1);
			g.drawLine(0, 3, 3, 0);
			if (!taskPane.expanded) {
				g.drawLine(0, h - 1, w - 1, h - 1);
			}
			Graphics2D g2d = (Graphics2D) g.create(w, 0, w, getHeight());
			Paint p = getPaint();
			g2d.setPaint(p);
			g2d.drawLine(0, 0, w - 4, 0);
			g2d.drawLine(w - 1, 3, w - 1, h - 1);
			g2d.drawLine(w - 1, 3, w - 4, 0);
			if (!taskPane.expanded) {
				g2d.drawLine(0, h - 1, w - 1, h - 1);
			}
			g2d.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(1, 1, 1, 1);
		}

		@Override
		public Insets getBorderInsets(Component c, Insets insets) {
			insets.left = insets.top = insets.right = insets.bottom = 1;
			return insets;
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}
	}
}
