package com.smartg.swing.taskpane;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import com.smartg.swing.RainLayout;

/**
 * Probably not the best implementation of TaskPane, but I like it.
 * 
 * @author Andrey Kuznetsov
 */
public class TaskPane extends JComponent {

	private static final long serialVersionUID = 1447473281072102997L;
	public static final String OPAQUE = "opaque";
	public static final String EXPANDED_STATE = "expanded";

	private final TaskPaneTitleBar titleBar;
	final ContentPane contentPane;

	private Color bg = new Color(0xFFD6DFF7);
	boolean expanded = true;
	boolean collapsed;
	boolean expanding, collapsing;

	private Color firstColor = Color.white;
	private Color secondColor = new Color(0xFFC7D4F7);

	protected boolean _opaque = true;
	boolean titleBarOpaque;

	boolean borderPainted = true;

	TaskPaneContainer container;

	public boolean debug = true;

	float speed = 16.0f;

	private int xGap = 5;
	private int yGap = 5;

	public TaskPane(String title) {
		this(title, null);
	}

	public TaskPane(String title, LayoutManager layout) {
		if (layout == null) {
			layout = createLayoutForContentPane();
		}

		setLayout(new BorderLayout());
		updateUI();
		setBackground(bg);
		super.setOpaque(false);

		this.contentPane = new ContentPane(layout);
		this.contentPane.setBorder(new EmptyBorder(5, 8, 5, 8));
		this.contentPane.setOpaque(false);

		Border b = this.contentPane.getBorder();
		if (b != null) {
			this.contentPane.setBorder(new CompoundBorder(new TaskPaneBorder(), b));
		} else {
			this.contentPane.setBorder(new TaskPaneBorder());
		}
		super.addImpl(this.contentPane, null, -1);

		titleBar = new TaskPaneTitleBar(this, title);
		setOpaque(true);
		setTitleBarOpaque(true);

		titleBar.setMargin(new Insets(3, 5, 3, 5));

		titleBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setExpanded(!expanded);
			}
		});

		super.addImpl(titleBar, BorderLayout.NORTH, -1);
		titleBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				e.getComponent().setCursor(Cursor.getDefaultCursor());
			}
		});
		finishExpand();
	}

	protected LayoutManager createLayoutForContentPane() {
		RainLayout layout = new RainLayout(RainLayout.X_AXIS, xGap, yGap, RainLayout.LAYOUT_POLICY_TABLE);
		layout.setColumnCount(1);
		layout.setAcrossStretch(false);
		return layout;
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		contentPane.add(comp, constraints, index);
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		titleBar.setExpanded(expanded);
		if (!expanded) {
			container.collapse(this);
			this.expanded = expanded;
		} else {
			this.expanded = expanded;
			container.expand(this);
		}
	}

	public JComponent getContentPane() {
		return contentPane;
	}

	public TaskPaneContainer getTaskPaneContainer() {
		return container;
	}

	@Override
	public void setOpaque(boolean isOpaque) {
		if (_opaque != isOpaque) {
			_opaque = isOpaque;
			if (_opaque) {
				firePropertyChange(OPAQUE, Boolean.FALSE, Boolean.TRUE);
			} else {
				firePropertyChange(OPAQUE, Boolean.TRUE, Boolean.FALSE);
			}
		}
	}

	public boolean is_opaque() {
		return _opaque;
	}

	public boolean isTitleBarOpaque() {
		return titleBarOpaque;
	}

	public boolean isIconLeft() {
		return titleBar.isIconLeft();
	}

	public void setIconLeft(boolean b) {
		titleBar.setIconLeft(b);
	}

	public int getXGap() {
		return xGap;
	}

	public void setXGap(int xGap) {
		this.xGap = xGap;
	}

	public int getYGap() {
		return yGap;
	}

	public void setYGap(int yGap) {
		this.yGap = yGap;
	}

	public void setTitleBarOpaque(boolean titleBarOpaque) {
		this.titleBarOpaque = titleBarOpaque;
		titleBar.setOpaque(titleBarOpaque);
		getArrowIcon().setContentOpaque(titleBarOpaque);
	}

	TaskPaneIcon getArrowIcon() {
		return titleBar.arrowIcon;
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

	float alpha = 1.0f;
	boolean useAlpha;

	@Override
	protected void paintComponent(Graphics g) {
		if (!_opaque) {
			return;
		}

		g.setColor(getBackground());
		int w = getWidth();
		int h = getHeight();
		int tbh = titleBar.getHeight();

		if (useAlpha && container.fadeOut) {
			((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		}

		g.fillRect(0, tbh, w - 1, h - tbh);

		if (useAlpha && container.fadeOut) {
			g.setPaintMode();
		}
	}

	void setAnimated(boolean animated) {
		if (!animated) {
			setPreferredSize(null);
			if (!expanded) {
				contentPane.setVisible(false);
			}
		}
	}

	public boolean isBorderPainted() {
		return borderPainted;
	}

	public void setBorderPainted(boolean b) {
		this.borderPainted = b;
	}

	class TaskPaneBorder extends AbstractBorder {

		private static final long serialVersionUID = 1644545944791732238L;

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			if (debug) {
				// System.out.println(x + " " + y + " " + width + " " + height);
			}
			int w = width / 2;
			g.setColor(firstColor);
			g.drawLine(x, y, x, y + height);
			g.drawLine(x, y + height - 1, x + w, y + height - 1);

			Graphics2D g2d = (Graphics2D) g.create(w, 0, w, height);
			Paint p = new GradientPaint(0, 0, firstColor, w, 0, secondColor);
			g2d.setPaint(p);
			g2d.drawLine(0, y + height - 1, w, y + height - 1);
			g2d.drawLine(w - 1, y, w - 1, y + height - 1);
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(0, 1, 1, 1);
		}

		@Override
		public Insets getBorderInsets(Component c, Insets insets) {
			if (insets != null) {
				insets.top = 0;
				insets.left = 1;
				insets.bottom = 1;
				insets.right = 1;
				return insets;
			}
			return new Insets(0, 1, 1, 1);
		}

		@Override
		public boolean isBorderOpaque() {
			return _opaque;
		}
	}

	private CObj cObj = new CObj();
	private EObj eObj = new EObj();

	void finishCollapse() {
		useAlpha = false;
		expanded = false;
		contentPane.setVisible(false);
		collapsing = false;
		// setPreferredSize(null);
		contentPane.setSize(contentPane.getWidth(), savedHeight);
	}

	int savedHeight;

	int prepareToCollapse() {
		collapsing = true;
		expandedHeight = 0;
		savedHeight = contentPane.getHeight();
		int h = getHeight() - titleBar.getHeight();
		Dimension d = getPreferredSize();
		useAlpha = true;
		float k = (h / speed);
		float step = 1.0f / (h / k);
		int count = (int) (h / k);
		alpha = 1.0f;
		cObj.set(count, k, d, step);
		return count;
	}

	void doCollapse() {
		for (int i = 0; i < cObj.count; i++) {
			doCollapseStep();
			container.doLayout();
			doLayout();
			Dimension d0 = container.getSize();
			container.paintImmediately(0, 0, d0.width, d0.height);
		}
		collapsed = true;
	}

	void doCollapseStep() {
		cObj.d.height -= cObj.k;
		cObj.d.height = Math.max(cObj.d.height, titleBar.getHeight());
		setPreferredSize(cObj.d);
		alpha = Math.max(alpha - cObj.step, 0.0f);
		// pause();
	}

	void finishExpand() {
		useAlpha = false;
		expanded = true;
		expanding = false;
		contentPane.setVisible(true);
		// setPreferredSize(null);
	}

	int prepareToExpand() {
		expanding = true;
		collapsed = false;
		int h = titleBar.getHeight();
		setPreferredSize(null);
		contentPane.setVisible(true);
		Dimension d0 = getPreferredSize();
		d0.height = Math.max(d0.height, expandedHeight);
		Dimension d = new Dimension(d0.width, h);
		useAlpha = true;
		float amount = d0.height - h;
		int k = (int) (amount / speed);
		int count = (int) (amount / k);
		float step = 1.0f / (amount / k);
		alpha = 0.0f;
		eObj.set(h, count, k, d, d0, step);
		return count;
	}

	@Override
	public Dimension getPreferredSize() {
		if (collapsed) {
			return new Dimension(contentPane.getPreferredSize().width, titleBar.getHeight());
		}
		return super.getPreferredSize();
	}

	public int getPreferredHeight() {
		Dimension d = contentPane.getPreferredSize();
		int h = titleBar.getHeight();
		return d.height + h;
	}

	int expandedHeight;

	public int getExpandedHeight() {
		return expandedHeight;
	}

	public void setExpandedHeight(int expandedHeight) {
		this.expandedHeight = expandedHeight;
	}

	void doExpand() {
		for (int i = eObj.h; i > 0; i--) {
			doExpandStep();
			container.doLayout();
			Dimension d1 = container.getSize();
			container.paintImmediately(0, 0, d1.width, d1.height);
		}
	}

	void doExpandStep() {
		eObj.d.height += eObj.k;
		eObj.d.height = Math.min(eObj.d.height, eObj.d0.height);
		setPreferredSize(eObj.d);
		alpha = Math.min(alpha + eObj.step, 1.0f);
	}

	public TaskPaneTitleBar getTitleBar() {
		return titleBar;
	}

	private static class CObj {
		int count;
		float k;
		Dimension d;
		float step;

		public void set(int count, float k, Dimension d, float step) {
			this.count = count;
			this.k = k;
			this.d = d;
			this.step = step;
		}
	}

	private static class EObj {
		int h;
		float k;
		Dimension d;
		Dimension d0;
		float step;

		/**
		 * @param count
		 */
		public void set(int h, int count, float k, Dimension d, Dimension d0, float step) {
			this.h = h;
			this.k = k;
			this.d = d;
			this.d0 = d0;
			this.step = step;
		}
	}

	class ContentPane extends JPanel {

		private static final long serialVersionUID = 5351522553678812559L;

		public ContentPane() {
		}

		public ContentPane(boolean isDoubleBuffered) {
			super(isDoubleBuffered);
		}

		public ContentPane(LayoutManager layout) {
			super(layout);
		}

		public ContentPane(LayoutManager layout, boolean isDoubleBuffered) {
			super(layout, isDoubleBuffered);
		}

		@Override
		protected void paintBorder(Graphics g) {
			if (borderPainted) {
				Border border = getBorder();
				int h = TaskPane.this.getHeight();
				int ph = Math.max(TaskPane.this.getPreferredHeight(), h);
				if (debug) {
					// System.out.println(h + " " + ph);
				}
				int y = h - ph;

				if (!expanding || collapsing) {
					y = 0;
				}

				if (border != null) {
					int width = getWidth();
					int height = getHeight() + y;
					border.paintBorder(this, g, 0, 0, width, height);
				}
			}
		}

		@Override
		protected void paintChildren(Graphics g) {
			if (useAlpha && container.fadeOut) {
				((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			}

			int ph = Math.max(TaskPane.this.getPreferredHeight(), TaskPane.this.getHeight());
			int h = TaskPane.this.getHeight();
			int w = getWidth();

			if ((expanding || collapsing) && ph != h && container.showRollEffect) {
				int y = h - ph;
				Graphics2D g2D = (Graphics2D) g.create(0, y, w, ph);
				g2D.setClip(0, -y, w, ph);
				super.paintChildren(g2D);
				g2D.dispose();
			} else {
				super.paintChildren(g);
			}

			if (useAlpha && container.fadeOut) {
				g.setPaintMode();
			}
		}
	}
}
