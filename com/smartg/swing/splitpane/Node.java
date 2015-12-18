package com.smartg.swing.splitpane;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EmptyStackException;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Manages component tree.
 * Most methods in Node are recursive.
 */
public class Node {
    GSplitPane sp;

    private int orientation;
    Rectangle rectangle, next;

    Rectangle savedOnDrag = new Rectangle();
    Rectangle savedOnVisible;
    Rectangle savedOnMaximize;

    int savedOnVisibleParent, savedOnVisibleOrientation;

    Node left, right;

    Component component;
    Divider divider;

    double lastDividerLocation = 0.5f;
    int number;

    String name;

    Node(GSplitPane sp, Component c, Rectangle r) {
        this.sp = sp;
        this.component = c;
        this.rectangle = new Rectangle(r);
        this.sp.nodesByComponent.put(c, this);
        number = sp.nodeCount++;
        component.setBounds(r);
        savedOnDrag = new Rectangle(r);
    }

    Node(GSplitPane sp, Node left, Node right, Rectangle r) {
        this.sp = sp;
        this.rectangle = new Rectangle(r);
        number = sp.nodeCount++;
        this.left = left;
        this.right = right;
        createDivider();
    }

    /**
     * determine if given Node is child of this Node
     * @param n Node
     * @return true if Node n is child of this Node
     */
    public boolean isChild(Node n) {
        if (!isLeaf()) {
            if (left == n || right == n) {
                return true;
            }
        }
        return false;
    }

    public Dimension getMinimumSize() {
        if (isVisible()) {
            if (isLeaf()) {
                return component.getMinimumSize();
            }
	    Dimension d0 = left.getMinimumSize();
	    Dimension d1 = right.getMinimumSize();
	    if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
	        return new Dimension(d0.width + d1.width + sp.dividerWidth, Math.max(d0.height, d1.height));
	    }
	    return new Dimension(Math.max(d0.width, d1.width), d0.height + d1.height + sp.dividerWidth);
        }
        return new Dimension(0, 0);
    }

    public Dimension getPreferredSize() {
        if (isVisible()) {
            if (isLeaf()) {
                return component.getPreferredSize();
            }
	    Dimension d0 = left.getPreferredSize();
	    Dimension d1 = right.getPreferredSize();
	    if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
	        return new Dimension(d0.width + d1.width + sp.dividerWidth, Math.max(d0.height, d1.height));
	    }
	    return new Dimension(Math.max(d0.width, d1.width), d0.height + d1.height + sp.dividerWidth);
        }
        return new Dimension(0, 0);
    }

    public Dimension getMaximumSize() {
        if (isVisible()) {
            if (isLeaf()) {
                return component.getMaximumSize();
            }
	    Dimension d0 = left.getMaximumSize();
	    Dimension d1 = right.getMaximumSize();
	    if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
	        return new Dimension(d0.width + d1.width + sp.dividerWidth, Math.max(d0.height, d1.height));
	    }
	    return new Dimension(Math.max(d0.width, d1.width), d0.height + d1.height + sp.dividerWidth);
        }
        return new Dimension(0, 0);
    }

    public Divider getDivider() {
        return divider;
    }

    public Node getParent() {
        if (!sp.nameValid) {
            sp.rename();
        }
        if (this == sp.root) {
            return null;
        }
        return sp.nodesByName.get(name.substring(0, name.length() - 1));
    }

    Node getRoot() {
        return sp.root;
    }

    public boolean isMaximized() {
        return sp.maximizedNode == this;
    }

    public void setMaximized(boolean b) {
        if (b) {
            if (sp.maximizedNode != null && sp.maximizedNode != this) {
                sp.maximizedNode.setBoundsIndependentMode(sp.maximizedNode.savedOnMaximize, Integer.MAX_VALUE);
                sp.maximized.push(sp.maximizedNode);
                sp.root.restoreOnMax();
                sp.maximizedNode = null;
            }
            savedOnMaximize = new Rectangle(this.rectangle);
            sp.maximizedNode = this;
            sp.root.minimizeOnMax();
            setBoundsIndependentMode(sp.container.getBounds(), Integer.MAX_VALUE);
            sp.doLayout();
        }
        else {
            if (sp.maximizedNode == this) {
                setBoundsIndependentMode(savedOnMaximize, Integer.MAX_VALUE);
                try {
                    sp.maximizedNode = sp.maximized.pop();
                    sp.maximizedNode.setMaximized(true);
                }
                catch (EmptyStackException ex) {
                    sp.maximizedNode = null;
                }
                sp.doLayout();
            }
        }
    }

    /**
     * we cant change component order without readding them,
     * so to maximize one component we have to minimize all other components
     */
    void minimizeOnMax() {
        if (sp.maximizedNode != this) {
            if (isLeaf()) {
                component.setSize(0, 0);
            }
            else {
                divider.dividerComponent.setVisible(false);
                left.minimizeOnMax();
                right.minimizeOnMax();
            }
        }
    }

    /**
     * restore all minimized components
     */
    private void restoreOnMax() {
        if (isLeaf()) {
            component.setSize(rectangle.width, rectangle.height);
        }
        else {
            divider.dividerComponent.setVisible(true);
            left.restoreOnMax();
            right.restoreOnMax();
        }
    }

    /**
     * instead of long traversing through tree structure we just compare names
     * @param p
     * @return
     */
    public boolean isDescendant(Node p) {
        if (!sp.nameValid) {
            sp.rename();
        }
        String pname = p.name;
        if (name != null) {
            return name.startsWith(pname);
        }
        return false;
    }

    /**
     * determine if this Node is visible.
     * @return true if this Node is a leaf Node and his Component is visible or
     * if this Node is not a leaf Node and his left or right child Nodes are visible.
     */
    boolean isVisible() {
        if (isLeaf()) {
            return component.isVisible();
        }
	return left.isVisible() || right.isVisible();
    }

    public float getDividerLocation() {
        Rectangle r0 = rectangle;
        float w = getDividerWidth();
        Rectangle r1 = getLeft().rectangle;
        if (getOrientation() == SplitConstants.VERTICAL_SPLIT) {
            return r1.height / (r0.height - w);
        }
	return r1.width / (r0.width - w);
    }

    public void setDividerLocation(float location) {
        Rectangle r0 = rectangle;
        if (getOrientation() == SplitConstants.VERTICAL_SPLIT) {
            setDividerLocation((int) (r0.height * location));
        }
        else {
            setDividerLocation((int) (r0.width * location));
        }
    }

    public void setDividerLocation(int location) {
        if (isLeaf()) {
            return;
        }
        Component c = divider.dividerComponent;
        MouseEvent e1 = new MouseEvent(c, 0, System.currentTimeMillis(), 0, 0, 0, 1, false, MouseEvent.BUTTON1);
        divider.dividerDragHandler.mousePressed(e1);
        int x = rectangle.x - c.getX() + location;
        int y = rectangle.y - c.getY() + location;
        MouseEvent e2 = new MouseEvent(c, 0, System.currentTimeMillis(), 0, x, y, 1, false, MouseEvent.BUTTON1);
        divider.dividerDragHandler.mouseReleased(e2);
    }

    void saveDividerLocation() {
        if (!isLeaf()) {
            if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
                lastDividerLocation = left.rectangle.width / (double) rectangle.width;
            }
            else {
                lastDividerLocation = left.rectangle.height / (double) rectangle.height;
            }
            left.saveDividerLocation();
            right.saveDividerLocation();
        }
    }

    void validate() {
        sp.container.validate();
    }

    /**
     * get this Node's Component
     * @return null if Node is not leaf or Component
     */
    Component getComponent() {
        return component;
    }

    void dividerDragStart() {
        savedOnDrag = new Rectangle(rectangle);
        if (!isLeaf()) {
            left.dividerDragStart();
            right.dividerDragStart();
        }
    }

    Component getComponentAt(int x, int y) {
        if (isLeaf()) {
            if (component.isVisible()) {
                if (component.getBounds().contains(x, y)) {
                    return component;
                }
            }
            else {
                Node p = getParent();
                if (p != null) {
                    if (p.left == this) {
                        if (p.right.isLeaf()) {
                            return p.right.component;
                        }
			return p.right.getComponentAt(x, y);
                    }
                    else if (p.right == this) {
                        if (p.left.isLeaf()) {
                            return p.left.component;
                        }
			return p.left.getComponentAt(x, y);
                    }
                }
                else {
                    return sp.root.getComponent();
                }
            }
        }
        else {
            if (left.rectangle.contains(x, y)) {
                return left.getComponentAt(x, y);
            }
	    return right.getComponentAt(x, y);
        }
        return null;
    }

    void paintNode(Graphics g) {
        if (isLeaf()) {
            component.paint(g);
        }
        else {
            divider.dividerComponent.paint(g);
            left.paintNode(g);
            right.paintNode(g);
        }
    }

    void dividerDragEnd() {
        sp.fireActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "dividerMoved"));
    }

    /**
     * get width of divider
     * @return
     */
    int getDividerWidth() {
        return sp.dividerWidth;
    }

    /**
     * resize weight determines how extra space is distributed.
     * @return current resize weight value (between 0.0f and 1.0f)
     */
    public float getResizeWeight() {
        return sp.resizeWeight;
    }

    /**
     * Determine if this Node is selected Node.
     * Only for debugging purpose.
     * @return true if this Node is selected
     */
    public boolean isSelected() {
        return sp.selectedNode == this;
    }

    /**
     * determine if child components should be are continuously redisplayed
     * and layed out during dragging the divider.
     */
    boolean isContinuousLayout() {
        return sp.continuousLayout;
    }

    /**
     * change selected Node (single selection mode only)
     * @param selected
     */
    public void setSelected(boolean selected) {
        if (selected) {
            sp.selectedNode = this;
        }
        else if (sp.selectedNode == this) {
            sp.selectedNode = null;
        }
    }

    boolean _synchronized;

    public boolean isSynchronized() {
        return _synchronized;
    }

    public void setSynchronized(boolean b) throws SynchronizationException {
        if (b) {
            if (isLeaf()) {
                throw new SynchronizationException("leaf node can't be synchronized");
            }
            if (left.isLeaf() || right.isLeaf()) {
                throw new SynchronizationException("child node can't be leaf");
            }
            if (left.orientation != right.orientation) {
                left.orientation = right.orientation;
            }
            if (left.orientation == orientation || right.orientation == orientation) {
                if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
                    left.orientation = SplitConstants.VERTICAL_SPLIT;
                    right.orientation = SplitConstants.VERTICAL_SPLIT;
                }
                else {
                    left.orientation = SplitConstants.HORIZONTAL_SPLIT;
                    right.orientation = SplitConstants.HORIZONTAL_SPLIT;
                }
            }
            right.divider.dividerDragHandler.addActionListener(new ProxyActionListener(left.divider.dividerDragHandler.dragListener));
            left.divider.dividerDragHandler.addActionListener(new ProxyActionListener(right.divider.dividerDragHandler.dragListener));
        }
        else {
            right.divider.dividerDragHandler.removeActionListener(left.divider.dividerDragHandler.dragListener);
            left.divider.dividerDragHandler.removeActionListener(right.divider.dividerDragHandler.dragListener);
        }
        _synchronized = b;
    }

    /**
     * determine where divider can be moved
     * @param n Node
     * @param bounds Bounds
     */
    void getBounds(Node n, Bounds bounds) {
        if (!isLeaf()) {
            if (!left.isLeaf()) {
                if (left.orientation == n.orientation) {
                    if (n.orientation == SplitConstants.HORIZONTAL_SPLIT) {
                        int v = left.divider.getDividerComponent().getX();
                        bounds.add(v);
                    }
                    else {
                        int v = left.divider.getDividerComponent().getY();
                        bounds.add(v);
                    }
                }
                left.getBounds(n, bounds);
            }
            if (!right.isLeaf()) {
                if (right.orientation == n.orientation) {
                    if (n.orientation == SplitConstants.HORIZONTAL_SPLIT) {
                        int v = right.divider.getDividerComponent().getX();
                        bounds.add(v);
                    }
                    else {
                        int v = right.divider.getDividerComponent().getY();
                        bounds.add(v);
                    }
                }
                right.getBounds(n, bounds);
            }
        }
    }

    void getBoundsL(int orientation, Bounds list) {
        if (isVisible()) {
            if (isLeaf()) {
                Rectangle r = component.getBounds();
                Dimension d = component.getMinimumSize();

                if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
                    int x = r.x + d.width;
                    list.addMin(x);
                }
                else {

                    int y = r.y + d.height;
                    list.addMin(y);
                }
            }
            else {
                left.getBoundsL(orientation, list);
                right.getBoundsL(orientation, list);
            }
        }
    }

    void getBoundsR(int orientation, Bounds list) {
        if (isVisible()) {
            if (isLeaf()) {
                Rectangle r = component.getBounds();
                Dimension d = component.getMinimumSize();

                if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
                    int w = r.width - d.width;
                    list.addMax(r.x + w);
                }
                else {
                    int h = r.height - d.height;
                    list.addMax(r.y + h);
                }
            }
            else {
                left.getBoundsR(orientation, list);
                right.getBoundsR(orientation, list);
            }
        }
    }


    Component getLeftComponent() {
        if (isLeaf()) {
            return component;
        }
	return left.getLeftComponent();
    }

    Component getRightComponent() {
        if (isLeaf()) {
            return component;
        }
	return left.getRightComponent();
    }


    /**
     * Add Component to this Node.
     * Divider component is automaticaly created.
     * @param comp Component to add
     * @param orientation orientation of splitpane
     */
    void add(Component comp, int orientation, int align) {
        if (component == null) {
            Rectangle r1;
            Rectangle r2;
            if (this.orientation == SplitConstants.HORIZONTAL_SPLIT) {
                if (align == SplitConstants.ALIGN_RIGHT) {
                    Rectangle r = right.rectangle;
                    r1 = new Rectangle(r.x, r.y, r.width / 4 - sp.dividerWidth / 2, r.height);
                    r2 = new Rectangle(r.x + r.width / 4 + sp.dividerWidth / 2, r.y, r.width / 4 - sp.dividerWidth / 2, rectangle.height);
                }
                else {
                    Rectangle r = left.rectangle;
                    r1 = new Rectangle(r.x, r.y, r.width/* / 2*/ - sp.dividerWidth / 2, r.height);
                    r2 = new Rectangle(r.x + r.width/* / 2*/ + sp.dividerWidth / 2, r.y, r.width/* / 2*/ - sp.dividerWidth / 2, rectangle.height);
                }
            }
            else {
                if (align == SplitConstants.ALIGN_RIGHT) {
                    Rectangle r = right.rectangle;
                    r1 = new Rectangle(r.x, r.y, r.width, r.height / 4 - sp.dividerWidth / 2);
                    r2 = new Rectangle(r.x, r.y + r.height / 4 + sp.dividerWidth / 2, r.width, rectangle.height / 4 - sp.dividerWidth / 2);
                }
                else {
                    Rectangle r = left.rectangle;
                    r1 = new Rectangle(r.x, r.y, r.width, r.height / 2 - sp.dividerWidth / 2);
                    r2 = new Rectangle(r.x, r.y + r.height / 2 + sp.dividerWidth / 2, r.width, rectangle.height / 2 - sp.dividerWidth / 2);
                }
            }

            if (align == SplitConstants.ALIGN_RIGHT) {
                Node n0 = new Node(sp, comp, r1);
                Node n = new Node(sp, n0, this.right, r2);
                this.right = n;
                n.orientation = this.orientation;
            }
            else {
                Node n0 = new Node(sp, comp, r1);
                Node n = new Node(sp, this.left, n0, r2);
                this.left = n;
                n.orientation = this.orientation;
            }
        }
        else {
            this.orientation = orientation;

            if (comp == component) {
                setBoundsIndependentMode(rectangle, Integer.MAX_VALUE);
                return;
            }

            Rectangle r1;
            Rectangle r2;
            if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
                r1 = new Rectangle(rectangle.x, rectangle.y, rectangle.width / 2 - sp.dividerWidth / 2, rectangle.height);
                r2 = new Rectangle(rectangle.x + rectangle.width / 2 + sp.dividerWidth / 2, rectangle.y, rectangle.width / 2 - sp.dividerWidth / 2, rectangle.height);
            }
            else {
                r1 = new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height / 2 - sp.dividerWidth / 2);
                r2 = new Rectangle(rectangle.x, rectangle.y + rectangle.height / 2 + sp.dividerWidth / 2, rectangle.width, rectangle.height / 2 - sp.dividerWidth / 2);
            }
            checkRect(r1);
            checkRect(r2);

            if (align == SplitConstants.ALIGN_RIGHT) {
                left = new Node(sp, component, r1);
                right = new Node(sp, comp, r2);
            }
            else {
                left = new Node(sp, comp, r1);
                right = new Node(sp, component, r2);
            }
            component = null;
            createDivider();
        }
    }


    private void createDivider() {
        divider = Divider.createLightweightDivider(this);
        divider.getDividerComponent().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                sp.drag = divider;
            }

            public void mouseReleased(MouseEvent e) {
                sp.drag = null;
                saveDividerLocation();
            }
        });
        sp.container.add(divider.getDividerComponent(), null, 0);
    }


    void setBoundsIndependentMode(Rectangle r, int depth) {
        if (depth == 0) {
            return;
        }
        if (isLeaf()) {
            this.rectangle = new Rectangle(r);
            this.component.setBounds(r);
        }
        else {
            this.rectangle = new Rectangle(r);

            boolean lv = left.isVisible();
            boolean rv = right.isVisible();

            Rectangle r1, r2, r3;

            if (lv && rv) {
                if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
                    int dw = r.width > sp.dividerWidth ? sp.dividerWidth : r.width;

                    r1 = new Rectangle(r.x, r.y, divider.getDividerComponent().getX() - r.x, r.height);
                    checkRect(r1);

                    r2 = new Rectangle(r.x + r1.width + dw, r.y, this.rectangle.width - getDividerWidth() - r1.width, r.height);

                    checkRect(r2);
                    r1.width = r2.x - r.x - dw;
                    r2.x = r.x + r1.width + dw;

                    r3 = new Rectangle(r.x + r1.width, r.y, dw, r.height);
                }
                else {
                    int dw = r.height > sp.dividerWidth ? sp.dividerWidth : r.height;

                    r1 = new Rectangle(r.x, r.y, r.width, divider.getDividerComponent().getY() - r.y);
                    checkRect(r1);

                    r2 = new Rectangle(r.x, r.y + r1.height + dw, r.width, this.rectangle.height - getDividerWidth() - r1.height);

                    checkRect(r2);
                    r1.height = r2.y - r.y - dw;
                    r2.y = r.y + r1.height + dw;

                    r3 = new Rectangle(r.x, r.y + r1.height, r.width, dw);
                }

                left.setBoundsIndependentMode(r1, depth - 1);
                right.setBoundsIndependentMode(r2, depth - 1);
                divider.getDividerComponent().setVisible(true);
                divider.getDividerComponent().setBounds(r3);
            }
            else if (lv && !rv) {
                left.setBoundsIndependentMode(r, depth - 1);
                divider.getDividerComponent().setVisible(false);
            }
            else if (!lv && rv) {
                divider.getDividerComponent().setVisible(false);
                right.setBoundsIndependentMode(r, depth - 1);
            }
        }
    }

    boolean ignoreMinimumSize = false;

    private void checkRect(Rectangle r) {
        switch (orientation) {
            case SplitConstants.HORIZONTAL_SPLIT:
                if (r.width > this.rectangle.width) {
                    r.width = this.rectangle.width;
                }
                if (r.x > this.rectangle.x + this.rectangle.width) {
                    r.x = this.rectangle.x + this.rectangle.width;
                }
                break;
            case SplitConstants.VERTICAL_SPLIT:
                if (r.height > this.rectangle.height) {
                    r.height = this.rectangle.height;
                }
                if (r.y > this.rectangle.y + this.rectangle.height) {
                    r.y = this.rectangle.y + this.rectangle.height;
                }
                break;
        }
    }

    protected void setBoundsResize(Rectangle r) {
        if (isLeaf()) {
            this.rectangle = new Rectangle(r);
            this.component.setBounds(r);
        }
        else {

            float dividerLocation = getDividerLocation();
            final int w = getDividerWidth();

            int dx = r.width - this.rectangle.width;
            int dy = r.height - this.rectangle.height;

            this.rectangle = new Rectangle(r);

            boolean lv = left.isVisible();
            boolean rv = right.isVisible();

            Rectangle r1, r2, r3;

            if (lv && rv) {
                if (orientation == SplitConstants.HORIZONTAL_SPLIT) {
                    int dw = r.width > sp.dividerWidth ? sp.dividerWidth : r.width;
                    if (savedOnVisible != null && savedOnVisibleOrientation == orientation) {
                        int dx2 = r.width - savedOnVisibleParent;
                        if (sp.resizeWeight >= 0) {
                            r1 = new Rectangle(r.x, r.y, (int) (savedOnVisible.width + dx2 * sp.resizeWeight), r.height);
                        }
                        else {
                            r1 = new Rectangle(r.x, r.y, (int) ((r.width - w) * dividerLocation), r.height);
                        }
                        savedOnVisible = null;
                    }
                    else {
                        if (sp.resizeWeight >= 0) {
                            r1 = new Rectangle(r.x, r.y, (int) (this.left.rectangle.width + dx * sp.resizeWeight), r.height);
                        }
                        else {
                            r1 = new Rectangle(r.x, r.y, (int) ((r.width - w) * dividerLocation), r.height);
                        }
                    }
                    checkRect(r1);

                    r2 = new Rectangle(r.x + r1.width + dw, r.y, r.width - r1.width - dw, r.height);

                    checkRect(r2);
                    r1.width = r2.x - r.x - dw;
                    r2.x = r.x + r1.width + dw;

                    r3 = new Rectangle(r.x + r1.width, r.y, dw, r.height);
                }
                else {
                    int dw = r.height > sp.dividerWidth ? sp.dividerWidth : r.height;

                    if (savedOnVisible != null && savedOnVisibleOrientation == orientation) {
                        if (sp.resizeWeight >= 0) {
                            int dy2 = r.height - savedOnVisibleParent;
                            r1 = new Rectangle(r.x, r.y, r.width, (int) (savedOnVisible.height + dy2 * sp.resizeWeight));
                            savedOnVisible = null;
                        }
                        else {
                            r1 = new Rectangle(r.x, r.y, r.width, (int) ((r.height - w) * dividerLocation));
                        }
                    }
                    else {
                        if (sp.resizeWeight >= 0) {
                            r1 = new Rectangle(r.x, r.y, r.width, (int) (this.left.rectangle.height + dy * sp.resizeWeight));
                        }
                        else {
                            r1 = new Rectangle(r.x, r.y, r.width, (int) ((r.height - w) * dividerLocation));
                        }
                    }
                    checkRect(r1);

                    r2 = new Rectangle(r.x, r.y + r1.height + dw, r.width, r.height - r1.height - dw);

                    checkRect(r2);
                    r1.height = r2.y - r.y - dw;
                    r2.y = r.y + r1.height + dw;

                    r3 = new Rectangle(r.x, r.y + r1.height, r.width, dw);
                }

                left.next = r1;
                right.next = r2;
                divider.getDividerComponent().setVisible(true);
                divider.getDividerComponent().setBounds(r3);
            }
            else if (lv && !rv) {
                if (savedOnVisible == null) {
                    savedOnVisible = new Rectangle(left.rectangle);
                    savedOnVisibleParent = orientation == SplitConstants.HORIZONTAL_SPLIT ? r.width : r.height;
                    savedOnVisibleOrientation = orientation;
                }
                divider.getDividerComponent().setVisible(false);
                left.next = r;
            }
            else if (!lv && rv) {
                if (savedOnVisible == null) {
                    savedOnVisible = new Rectangle(left.rectangle);
                    savedOnVisibleParent = orientation == SplitConstants.HORIZONTAL_SPLIT ? r.width : r.height;
                    savedOnVisibleOrientation = orientation;
                }
                divider.getDividerComponent().setVisible(false);
                right.next = r;
            }
        }
    }

    void renumber(int i) {
        this.number = i;
        if (!isLeaf()) {
            left.renumber(i + 1);
            right.renumber(i + 1);
        }
        else {
            component.setName("" + i);
        }
    }

    void renameNode() {
        if (this.name == null) {
            this.name = "0";
        }
        if (!isLeaf()) {
            left.name = name + "0";
            right.name = name + "1";
            left.renameNode();
            right.renameNode();
        }
        sp.nodesByName.put(this.name, this);
    }

    void putToBag() {
        sp.hashBag.put(new Integer(number), this);
        if (!isLeaf()) {
            left.putToBag();
            right.putToBag();
        }
    }

    /**
     * find and remove from tree specified node
     * @param n Node to remove
     */
    void remove(Node n) {
        if (!isLeaf()) {
            if (left == n) {
                removeLeftLeaf().sp = null;
                return;
            }
            else if (right == n) {
                removeRightLeaf().sp = null;
                return;
            }
            else {
                left.remove(n);
                right.remove(n);
            }
        }
    }

    /**
     * remove left Node
     * @return removed Node
     */
    Node removeLeftLeaf() {
        Node n = left;
        left = null;
        if (n.divider != null) {
            sp.container.remove(n.divider.getDividerComponent());
        }

        //leaf
        if (right.component != null) {
            component = right.component;
            right = null;
            sp.nodesByComponent.put(component, this);
            sp.container.remove(divider.getDividerComponent());
        }
        else {
            final Node rn = right;
            orientation = rn.orientation;
            if (rn.divider != null) {
                sp.container.remove(rn.divider.getDividerComponent());
                rn.divider = null;
            }

            left = right.left;
            right = right.right;
            sp.nodesByComponent.remove(n.component);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                sp.container.doLayout();
            }
        });
        return n;
    }

    /**
     * remove right Node
     * @return removed Node
     */
    Node removeRightLeaf() {
        Node n = right;
        right = null;
        if (n.divider != null) {
            sp.container.remove(n.divider.getDividerComponent());
        }

        //leaf
        if (left.component != null) {
            component = left.component;
            left = null;
            sp.nodesByComponent.put(component, this);
            sp.container.remove(divider.getDividerComponent());
        }
        else {
            final Node ln = left;
            orientation = ln.orientation;
            if (ln.divider != null) {
                sp.container.remove(ln.divider.getDividerComponent());
                ln.divider = null;
            }

            right = ln.right;
            left = ln.left;
            sp.nodesByComponent.remove(n.component);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                sp.doLayout();
            }
        });
        return n;
    }

    public boolean isLeaf() {
        return component != null;
    }

    /**
     * Helps to visualize tree structure.
     * @param parent DefaultMutableTreeNode
     */
    void addToNode(DefaultMutableTreeNode parent) {
        parent.setUserObject(this);
        if (!isLeaf()) {
            final DefaultMutableTreeNode l = new DefaultMutableTreeNode();
            final DefaultMutableTreeNode r = new DefaultMutableTreeNode();
            parent.add(l);
            parent.add(r);
            left.addToNode(l);
            right.addToNode(r);
        }
    }

    public String getName() {
        if (!sp.nameValid) {
            sp.rename();
        }
        return name;
    }

    public void debug() {
        System.out.println("-------------------");
        System.out.println(number + " " + rectangle.getBounds());
        if (isLeaf()) {
            System.out.println(component.getBounds());
        }
        else {
            left.debug();
            right.debug();
        }
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public static class ProxyActionListener implements ActionListener {
        ActionListener listener;

        public ProxyActionListener(ActionListener listener) {
            this.listener = listener;
        }

        public void actionPerformed(ActionEvent e) {
            Point p = (Point) e.getSource();
            ActionEvent e0 = new ActionEvent(new Point(p), e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
            listener.actionPerformed(e0);
        }

        public boolean equals(Object obj) {
            return listener.equals(obj);
        }

        public int hashCode() {
            return listener.hashCode();
        }

        public String toString() {
            return listener.toString();
        }
    }
}
