/*
 * Copyright (c) imagero Andrey Kuznetsov. All Rights Reserved.
 * http://jgui.imagero.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.smartg.swing.splitpane;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public abstract class Divider {
    Node node;
    final DividerDragHandler dividerDragHandler;
    Component dividerComponent;
    boolean dragging;

    static boolean highlightDivider;

    public static boolean isHighlightDivider() {
	return highlightDivider;
    }

    public static void setHighlightDivider(boolean highlightDivider) {
	Divider.highlightDivider = highlightDivider;
    }

    Divider(Node n) {
	this.node = n;
	dividerComponent = createDividerComponent();
	dividerComponent.setBackground(null);
	dividerComponent.addMouseListener(new MouseAdapter() {
	    public void mouseEntered(MouseEvent e) {
		if (!dragging) {
		    dividerComponent.setCursor(Cursor
			    .getPredefinedCursor(Divider.this.node.getOrientation() == SplitConstants.HORIZONTAL_SPLIT ? Cursor.W_RESIZE_CURSOR
				    : Cursor.N_RESIZE_CURSOR));
		    if (highlightDivider) {
			Color c = dividerComponent.getBackground().darker();
			dividerComponent.setBackground(c);
		    }
		}
	    }

	    public void mouseExited(MouseEvent e) {
		if (!dragging) {
		    dividerComponent.setBackground(null);
		}
	    }

	    public void mouseReleased(MouseEvent e) {
		dividerComponent.setBackground(null);
	    }
	});
	dividerDragHandler = new DividerDragHandler(this);
    }

    public Component getDividerComponent() {
	return dividerComponent;
    }

    public ActionListener getActionListener() {
	return dividerDragHandler.dragListener;
    }

    public void addActionListener(ActionListener listener) {
	dividerDragHandler.addActionListener(listener);
    }

    public void removeActionListener(ActionListener listener) {
	dividerDragHandler.removeActionListener(listener);
    }

    protected abstract Component createDividerComponent();

    static class LwDividerComponent extends JPanel implements DividerComponent {

	private static final long serialVersionUID = 2204890372812340594L;
	private Divider divider;

	public LwDividerComponent(Divider divider) {
	    this.divider = divider;
	}

	public Divider getDivider() {
	    return divider;
	}
    }

    static class HwDividerComponent extends Canvas implements DividerComponent {

	private static final long serialVersionUID = -1949855744840443922L;
	private Divider divider;

	public HwDividerComponent(Divider divider) {
	    this.divider = divider;
	}

	public Divider getDivider() {
	    return divider;
	}
    }

    public static Divider createLightweightDivider(Node n) {
	return new Divider(n) {
	    protected Component createDividerComponent() {
		return new LwDividerComponent(this);
	    }
	};
    }

    public static Divider createHeawyweightDivider(Node n) {
	return new Divider(n) {
	    protected Component createDividerComponent() {
		return new HwDividerComponent(this);
	    }
	};
    }

    public Node getNode() {
	return node;
    }

    /**
     * Compute constraints needed to add Component at defined Point
     * 
     * @param p
     *            Point to add/drop Component
     * @return SplitPaneConstraints
     */
    public SplitPaneConstraints computeConstraints(Point p) {
	SplitPaneConstraints constraints;
	Node node = getNode();

	int split = SplitConstants.HORIZONTAL_SPLIT;
	int align = SplitConstants.ALIGN_LEFT;

	Component dc = dividerComponent;

	Dimension d = dc.getSize();
	Point p0 = dc.getLocation();

	if (node.getOrientation() == SplitConstants.HORIZONTAL_SPLIT) {
	    split = SplitConstants.HORIZONTAL_SPLIT;
	    if (p.x >= p0.x + d.width / 2) {
		align = SplitConstants.ALIGN_RIGHT;
	    } else {
		align = SplitConstants.ALIGN_LEFT;
	    }
	} else {
	    split = SplitConstants.VERTICAL_SPLIT;
	    if (p.y >= p0.y + d.height / 2) {
		align = SplitConstants.ALIGN_RIGHT;
	    } else {
		align = SplitConstants.ALIGN_LEFT;
	    }
	}
	constraints = new SplitPaneConstraints(dc, split, align);
	return constraints;
    }

    public Rectangle computeRect(Point p) {
	Rectangle r0;
	Node node = getNode();
	Component dc = dividerComponent;

	Dimension d = dc.getSize();
	Point p0 = dc.getLocation();

	if (node.getOrientation() == SplitConstants.HORIZONTAL_SPLIT) {
	    if (p.x >= p0.x + d.width / 2) {
		r0 = new Rectangle(node.getRight().getRectangle());
		r0.width = r0.width / 2;
	    } else {
		r0 = new Rectangle(node.getLeft().getRectangle());
		r0.width = r0.width / 2;
		r0.x += r0.width;
	    }
	} else {
	    if (p.y >= p0.y + d.height / 2) {
		r0 = new Rectangle(node.getRight().getRectangle());
		r0.height = r0.height / 2;
	    } else {
		r0 = new Rectangle(node.getLeft().getRectangle());
		r0.height = r0.height / 2;
		r0.y += r0.height;
	    }
	}
	return r0;
    }

    static class DividerDragHandler extends DragHandler {
	Divider divider;
	int lw, rw, lh, rh;
	Rectangle rect;
	DividerDragListener dragListener;
	

	Bounds bounds;

	public DividerDragHandler(Divider divider) {
	    super(divider.dividerComponent, divider.dividerComponent, false);
	    this.divider = divider;

	    dragListener = new DividerDragListener();
	    addActionListener(dragListener);
	}

	public void mousePressed(MouseEvent e) {
	    super.mousePressed(e);
	    final Node node = DividerDragHandler.this.divider.node;

	    node.dividerDragStart();
	    divider.dragging = true;

	    Rectangle r1 = node.left.rectangle;
	    Rectangle r2 = node.right.rectangle;
	    lw = r1.width;
	    lh = r1.height;
	    rw = r2.width;
	    rh = r2.height;

	    bounds = checkNodeBounds(node);

	    int orientation = node.getOrientation();

	    if (!node.sp.isIgnorePreferredSize()) {
		node.left.getBoundsL(orientation, bounds);
		node.right.getBoundsR(orientation, bounds);
	    }
	}

	private Bounds checkNodeBounds(final Node node) {
	    Bounds bounds = null;
	    Point p0 = node.divider.dividerComponent.getLocation();
	    int orientation = node.getOrientation();
	    if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
		int x = p0.x;
		bounds = new Bounds(x, x + node.getDividerWidth(), node.rectangle.x, node.rectangle.x + node.rectangle.width);
	    } else {
		int y = p0.y;
		bounds = new Bounds(y, y + node.getDividerWidth(), node.rectangle.y, node.rectangle.y + node.rectangle.height);
	    }
	    return bounds;
	}

	public void mouseReleased(MouseEvent e) {
	    super.mouseReleased(e);
	    divider.dragging = false;
	    final Node node = DividerDragHandler.this.divider.node;
	    node.dividerDragEnd();
	}

	public void mouseClicked(MouseEvent e) {
	    if (e.getClickCount() == 2) {
		divider.node.setMaximized(!divider.node.isMaximized());
	    }
	}

	private void update(final Node node, Point p) {
	    node.setSelected(true);

	    Rectangle r1 = node.left.rectangle;
	    Rectangle r2 = node.right.rectangle;

	    int dividerWidth = node.getDividerWidth();

	    if (node.getOrientation() == SplitConstants.HORIZONTAL_SPLIT) {
		p.y = y0;

		if (p.x < node.left.rectangle.x) {
		    p.x = node.left.rectangle.x;
		}

		int dx = p.x - x0;

		int w1 = lw + dx;
		int w2 = rw - dx;

		dx = w1 - lw;
		p.x = dx + x0;

		node.left.setBoundsIndependentMode(new Rectangle(r1.x, r1.y, w1, r1.height), -1);
		node.right.setBoundsIndependentMode(new Rectangle(r1.x + w1 + dividerWidth, r2.y, w2, r2.height), -1);
		node.lastDividerLocation = node.left.rectangle.width / (double) node.rectangle.width;
	    } else {
		p.x = x0;
		int dy = p.y - y0;

		int h1 = lh + dy;
		int dh1 = 0;

		int h2 = rh - dy;
		int dh2 = 0;

		int nh = lh + rh - h1 - h2;
		if (nh != 0) {
		    if (dh1 != 0 && dh2 != 0) {
			h1 += nh / 2;
			h2 += nh / 2;
		    } else if (dh1 != 0) {
			h2 += nh;
		    } else if (dh2 != 0) {
			h1 += nh;
		    }
		}

		dy = h1 - lh;
		p.y = dy + y0;

		node.left.setBoundsIndependentMode(new Rectangle(r1.x, r1.y, r1.width, h1), -1);
		node.right.setBoundsIndependentMode(new Rectangle(r2.x, r1.y + h1 + dividerWidth, r2.width, h2), -1);
		node.lastDividerLocation = node.left.rectangle.height / (double) node.rectangle.height;
	    }
	}

	private class DividerDragListener implements ActionListener {

	    public void actionPerformed(ActionEvent e) {
		Point p = (Point) e.getSource();

		final Node node = DividerDragHandler.this.divider.node;

		Rectangle r = node.rectangle;
		if (p.x < r.x) {
		    p.x = r.x;
		} else if (p.x > r.x + r.width - node.getDividerWidth()) {
		    p.x = r.x + r.width - node.getDividerWidth();
		}
		if (p.y < r.y) {
		    p.y = r.y;
		} else if (p.y > r.y + r.height - node.getDividerWidth()) {
		    p.y = r.y + r.height - node.getDividerWidth();
		}

		if (bounds == null) {
		    bounds = checkNodeBounds(node);
		}
		if (node.getOrientation() == SplitConstants.HORIZONTAL_SPLIT) {
		    if (p.x < bounds.nearestMin) {
			p.x = bounds.nearestMin;
		    } else if (p.x > bounds.nearestMax - node.getDividerWidth()) {
			p.x = bounds.nearestMax - node.getDividerWidth();
		    }
		} else {
		    if (p.y < bounds.nearestMin) {
			p.y = bounds.nearestMin;
		    } else if (p.y > bounds.nearestMax - node.getDividerWidth()) {
			p.y = bounds.nearestMax - node.getDividerWidth();
		    }
		}

		if (e.getActionCommand() == "mousePressed") {
		    return;
		}
		if (node.getOrientation() == SplitConstants.HORIZONTAL_SPLIT) {
		    p.y = y0;
		} else {
		    p.x = x0;
		}

		if (node.isContinuousLayout() || e.getActionCommand() == "mouseReleased") {
		    update(node, p);
		    node.validate();
		}
	    }
	}
    }
}
