package com.smartg.swing.splitpane;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

/**
 * @author Andrey Kuznetsov
 */
public class HeavyweightSplitPanel extends Panel implements SplitPaneContainer {


    private static final long serialVersionUID = -4842580192510889858L;
    GSplitPane msp;

    public HeavyweightSplitPanel() {
	addContainerListener(new ContainerAdapter() {
	    public void componentRemoved(ContainerEvent e) {
		Node node = msp.getNode(e.getChild());
		if (node != null && msp.root != null) {
		    msp.root.remove(node);
		}
	    }
	});
    }

    public GSplitPane getSplitPane() {
	return msp;
    }

    public Component getComponentAt(int x, int y) {
	Component c = msp.getComponentAt(x, y);
	if (c != null) {
	    return c;
	}
	return super.getComponentAt(x, y);
    }

    public void doLayout() {
	msp.doLayout();
    }

    /**
     * does nothing - GSplitPane controls its Layout by itself
     */
    public void setLayout(LayoutManager mgr) {
    }

    public Dimension getPreferredSize() {
	Dimension d = msp.getPreferredSize();
	if (d != null && d.width > 0 && d.height > 0) {
	    return d;
	}
	return super.getPreferredSize();
    }

    public Dimension getMinimumSize() {
	Dimension d = msp.getMinimumSize();
	if (d != null && d.width > 0 && d.height > 0) {
	    return d;
	}
	return super.getMinimumSize();
    }

    public Dimension getMaximumSize() {
	Dimension d = msp.getMaximumSize();
	if (d != null && d.width > 0 && d.height > 0) {
	    return d;
	}
	return super.getMaximumSize();
    }

    public void paint(Graphics g) {
	super.paint(g);
	if (!msp.continuousLayout && msp.drag != null) {
	    Rectangle r = msp.drag.getDividerComponent().getBounds();
	    g.setColor(Color.darkGray);
	    g.fillRect(r.x, r.y, r.width, r.height);
	}
	if (msp.dropArea != null) {
	    Graphics2D g2d = ((Graphics2D) g);
	    switch (msp.showDropAreaMode) {
	    case GSplitPane.DRAW_RECTANGLE:
		g.setColor(Color.darkGray);
		g2d.setStroke(new BasicStroke(4));
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g.drawRect(msp.dropArea.x + 4, msp.dropArea.y + 4, msp.dropArea.width - 8, msp.dropArea.height - 8);
		break;
	    case GSplitPane.FILL_RECTANGLE:
		g.setColor(Color.BLUE);
		// g2d.setStroke(new BasicStroke(4));
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
		g.fillRect(msp.dropArea.x + 4, msp.dropArea.y + 4, msp.dropArea.width - 8, msp.dropArea.height - 8);
		g.setColor(Color.darkGray);
		g2d.setStroke(new BasicStroke(4));
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g.drawRect(msp.dropArea.x + 4, msp.dropArea.y + 4, msp.dropArea.width - 8, msp.dropArea.height - 8);
		break;
	    case GSplitPane.DRAW_COMPONENT:
		if (msp.volatileImage != null && !msp.volatileImage.contentsLost()) {
		    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		    g.drawImage(msp.volatileImage, msp.dropArea.x, msp.dropArea.y, null);
		}
	    }
	}
    }

    /**
     * Adds component to JMultiSplitPane. Since children in JMultiSplitPane
     * never overlap, order of components doesn't matters and I misuse index as
     * orientation.
     * 
     * @param comp
     *            Component to add
     * @param constraints
     *            Component whose place should be splitted
     * @param index
     *            splitpane index
     */
    protected void addImpl(Component comp, Object constraints, int index) {
	if (comp instanceof Divider.LwDividerComponent) {
	    super.addImpl(comp, constraints, -1);
	    return;
	}
	if (msp.root != null) {
	    if (constraints instanceof SplitPaneConstraints) {
		SplitPaneConstraints spc = (SplitPaneConstraints) constraints;
		if (spc.getComponent() != null) {
		    super.addImpl(comp, constraints, 0);
		    msp.split(spc.getComponent(), comp, spc.getOrientation(), spc.getAlign());
		} else {
		    Component root = getComponent(0);
		    super.addImpl(comp, constraints, -1);
		    msp.split(root, comp, spc.getOrientation(), spc.getAlign());
		}
	    } else {
		throw new IllegalArgumentException();
	    }
	} else {
	    Rectangle bounds = getBounds();
	    Insets insets = getInsets();
	    bounds.x += insets.left;
	    bounds.y += insets.top;
	    bounds.width -= insets.left + insets.right;
	    bounds.height -= insets.top + insets.bottom;
	    msp.root = msp.createNode(comp, bounds);
	    msp.nodesByComponent.put(comp, msp.root);
	    super.addImpl(comp, constraints, 0);
	}
	msp.nameValid = false;
	msp.fireActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "componentAdded"));
    }
}
