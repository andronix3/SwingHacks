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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.smartg.java.util.HashBag;

/**
 * GSplitPane Like JSplitPane, but not restricted (e.g. more then 2 components
 * allowed).
 */
public abstract class GSplitPane implements SplitConstants {

    protected Container container;

    Node root;

    int dividerWidth = 4;
    float resizeWeight = -1f;

    HashMap<String, Node> nodesByName;
    HashMap<Component, Node> nodesByComponent = new HashMap<Component, Node>();

    Divider drag;

    boolean continuousLayout = true;
    // boolean independentMode = false;

    Node selectedNode;

    HashBag<Integer, Node> hashBag;
    int nodeCount;

    private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();

    int showDropAreaMode = DRAW_COMPONENT;

    public static final int DRAW_RECTANGLE = 0;
    public static final int FILL_RECTANGLE = 1;
    public static final int DRAW_COMPONENT = 2;

    Rectangle dropArea;
    Node maximizedNode;

    Stack<Node> maximized = new Stack<Node>();

    boolean ignorePreferredSize;

    boolean nameValid;

    public static Container createLightweightSplitPane() {
	GSplitPane gsp = new GSplitPane() {
	    protected void createContentPane() {
		LightweightSplitPanel lsp = new LightweightSplitPanel();
		container = lsp;
		lsp.msp = this;
	    }
	};
	return gsp.getContainer();
    }

    public static Container createHeawyweightSplitPane() {
	GSplitPane gsp = new GSplitPane() {
	    protected void createContentPane() {
		HeavyweightSplitPanel hsp = new HeavyweightSplitPanel();
		container = hsp;
		hsp.msp = this;
	    }
	};
	return gsp.getContainer();
    }

    public Dimension getPreferredSize() {
	if (root != null) {
	    return root.getPreferredSize();
	}
	return null;
    }

    public Dimension getMinimumSize() {
	if (root != null) {
	    return root.getMinimumSize();
	}
	return null;
    }

    public Dimension getMaximumSize() {
	if (root != null) {
	    return root.getMaximumSize();
	}
	return null;
    }

    /**
     * return Node from tree.
     * 
     * @param name
     *            String representation of node position in tree: root node name
     *            is "0", root->left "00", root->right "01", root->right->left
     *            "010", ...
     * @return Node
     */
    public Node getNodeByName(String name) {
	if (!nameValid) {
	    rename();
	}
	return nodesByName.get(name);
    }

    protected GSplitPane() {
	createContentPane();

	container.addContainerListener(new ContainerAdapter() {
	    public void componentRemoved(ContainerEvent e) {
		Component comp = e.getChild();
		if (comp instanceof com.smartg.swing.splitpane.Divider.LwDividerComponent) {
		    return;
		}
		Node n = nodesByComponent.remove(comp);
		if (n != null) {
		    if (root == n) {
			root = null;
		    } else {
			if (root != null) {
			    root.remove(n);
			}
		    }
		}
		fireActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "componentRemoved"));
	    }

	});
    }

    void fireActionEvent(ActionEvent e) {
	for(ActionListener l: listeners) {
	    l.actionPerformed(e);
	}
    }
    
    public void addActionListener(ActionListener l) {
	if(!listeners.contains(l)) {
	    listeners.add(l);
	}
    }
    
    public void removeActionListener(ActionListener l) {
	listeners.remove(l);
    }

    protected abstract void createContentPane();

    /**
     * get (leaf) Node which contains specified Component
     * 
     * @param c
     *            Component
     * @return Node
     */
    public Node getNode(Component c) {
	if (c != null) {
	    return nodesByComponent.get(c);
	}
	return root;
    }

    /**
     * get Node name
     * 
     * @param c
     *            Component
     * @return String
     * @see #getNodeByName
     */
    public String getNameForComponent(Component c) {
	Node node = nodesByComponent.get(c);
	if (node != null) {
	    return node.getName();
	}
	return null;
    }

    public void debug() {
	if (root != null) {
	    root.debug();
	}
    }

    /**
     * set divider location (same as
     * getNode(child).getParent().setDividerLocation())
     * 
     * @param child
     *            child component
     * @param dividerLocation
     *            new divider location
     */
    public void setDividerLocation(Component child, float dividerLocation) {
	Node node = getNode(child);
	if (node != null) {
	    node.getParent().setDividerLocation(dividerLocation);
	}
    }

    private void resize(Node root) {
	Rectangle r = new Rectangle(container.getSize());
	Insets insets = container.getInsets();
	r.x += insets.left;
	r.y += insets.top;
	r.width -= insets.left + insets.right;
	r.height -= insets.top + insets.bottom;

	root.setBoundsResize(r);
	// root.setBoundsIndependentMode(r, -1);
	addToHashBag(root, false);
	Enumeration<Integer> enum0 = hashBag.keys();
	ArrayList<Integer> list = new ArrayList<Integer>();
	while (enum0.hasMoreElements()) {
	    list.add(enum0.nextElement());
	}
	Integer[] ix = new Integer[list.size()];
	for (int i = 0; i < ix.length; i++) {
	    ix[i] = list.get(i);
	}
	Arrays.sort(ix);
	for (int i = 0; i < ix.length; i++) {
	    int count = hashBag.getCount(ix[i]);
	    for (int j = 0; j < count; j++) {
		Node n = hashBag.get(ix[i], j);
		if (n.next != null) {
		    // n.setBoundsIndependentMode(n.next, 1);
		    n.setBoundsResize(n.next);
		}
	    }
	}
    }

    public void doLayout() {
	if (maximizedNode != null && maximizedNode.isVisible()) {
	    resize(maximizedNode);
	} else if (root != null) {
	    resize(root);
	}
    }

    public Component getComponentAt(int x, int y) {
	if (maximizedNode != null) {
	    Component comp = maximizedNode.getComponentAt(x, y);
	    if (comp != null) {
		return comp;
	    }
	} else if (root != null) {
	    Component comp = root.getComponentAt(x, y);
	    if (comp != null) {
		return comp;
	    }
	}
	return null;
    }

    /**
     * maximize Node
     * 
     * @param c
     */
    public void maximize(Component c) {
	Node n = nodesByComponent.get(c);
	if (n != null) {
	    n.setMaximized(true);
	    container.repaint();
	}
    }

    /**
     * restore maximized Node
     */
    public void restore() {
	if (maximizedNode != null) {
	    maximizedNode.setMaximized(false);
	    container.repaint();
	}
    }

    Node maximizeOnRestore;

    /**
     * minimize Node with specified Component
     * 
     * @param c
     *            Component
     */
    public void minimize(Component c) {
	Node n = nodesByComponent.get(c);
	if (n != null) {
	    if (maximizedNode == n) {
		maximizeOnRestore = maximizedNode;
		restore();
	    }
	    c.setVisible(false);
	}
    }

    /**
     * restore minimized Component
     * 
     * @param c
     *            Component to restore
     */
    public void restore(Component c) {
	if (!c.isVisible()) {
	    Node n = nodesByComponent.get(c);
	    if (n != null) {
		c.setVisible(true);
		if (maximizeOnRestore == n) {
		    maximizeOnRestore = null;
		    maximize(c);
		}
	    }
	}
    }

    private void renumber(Node root) {
	if (root != null) {
	    root.renumber(0);
	}
    }

    /**
     * ensures that node names are valid
     */
    public void rename() {
	nodesByName = new HashMap<String, Node>();
	if (root != null) {
	    root.renameNode();
	}
	nameValid = true;
    }

    private void addToHashBag(Node root, boolean includeRoot) {
	renumber(root);
	if (root != null) {
	    hashBag = new HashBag<Integer, Node>();
	    if (includeRoot) {
		root.putToBag();
	    } else {
		if (!root.isLeaf()) {
		    root.left.putToBag();
		    root.right.putToBag();
		}
	    }
	}
    }

    protected void split(Component child, final Component add, final int orientation, final int align) {
	if (child instanceof com.smartg.swing.splitpane.Divider.LwDividerComponent) {
	    com.smartg.swing.splitpane.Divider.LwDividerComponent divider = (com.smartg.swing.splitpane.Divider.LwDividerComponent) child;
	    Node n = divider.getDivider().node;
	    n.add(add, orientation, align);
	} else {
	    Node n = nodesByComponent.get(child);
	    if (n != null) {
		n.add(add, orientation, align);
	    }
	}
    }

    /**
     * determine if child components should be are continuously redisplayed and
     * layed out during dragging the divider.
     */
    public boolean isContinuousLayout() {
	return continuousLayout;
    }

    public void setContinuousLayout(boolean continuousLayout) {
	this.continuousLayout = continuousLayout;
    }

    public float getResizeWeight() {
	return resizeWeight;
    }

    /**
     * set resize weight. Valid values are from 0.0f to 1.0f and -1.0f. Value of
     * -1.0f means that after resize the return value of
     * Node#getDividerLocation() stays the same.
     * 
     * @param resizeWeight
     */
    public void setResizeWeight(float resizeWeight) {
	this.resizeWeight = resizeWeight;
    }

    public void saveDividerLocation() {
	if (root != null) {
	    root.saveDividerLocation();
	}
    }

    public TreeModel createTreeModel() {
	final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
	rename();
	if (root != null) {
	    root.addToNode(rootNode);
	}
	DefaultTreeModel model = new DefaultTreeModel(rootNode);
	return model;
    }

    public int getDividerWidth() {
	return dividerWidth;
    }

    public void setDividerWidth(int dividerWidth) {
	this.dividerWidth = dividerWidth;
	container.doLayout();
    }

    public Rectangle getDropArea() {
	return dropArea;
    }

    boolean recreatePhantom;
    VolatileImage volatileImage;

    public void setDropArea(Rectangle dropArea) {
	if (dropArea != null && dropArea.equals(this.dropArea)) {
	    recreatePhantom = false;
	} else {
	    recreatePhantom = true;
	    this.dropArea = dropArea;
	    if (showDropAreaMode == DRAW_COMPONENT && dropArea != null) {
		if (dropArea.width <= 0) {
		    dropArea.width = 1;
		}
		if (dropArea.height <= 0) {
		    dropArea.height = 1;
		}
		volatileImage = container.createVolatileImage(dropArea.width, dropArea.height);
	    }
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    container.repaint(200);
		}
	    });
	}
    }

    public boolean shouldRecreatePhantom() {
	return recreatePhantom || volatileImage == null || volatileImage.contentsLost();
    }

    public int getShowDropAreaMode() {
	return showDropAreaMode;
    }

    public void setShowDropAreaMode(int mode) {
	this.showDropAreaMode = mode;
    }

    public VolatileImage getVolatileImage() {
	if (volatileImage == null && dropArea != null) {
	    volatileImage = container.createVolatileImage(dropArea.width, dropArea.height);
	}
	return volatileImage;
    }

    public Node createNode(Component c, Rectangle r) {
	return new Node(this, c, r);
    }

    public Node createNode(Node left, Node right, Rectangle r) {
	return new Node(this, left, right, r);
    }

    public Container getContainer() {
	return container;
    }

    public boolean isIgnorePreferredSize() {
	return ignorePreferredSize;
    }

    public void setIgnorePreferredSize(boolean b) {
	this.ignorePreferredSize = b;
    }
}
