/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * http://www.imagero.com/layout/
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of imagero Andrey Kuznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.smartg.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * <pre>
 * RainLayout features:
 *      both horizontal and vertical orientation,
 *      ordering of Components according to Comparator provided by Container.
 *      inverse order of components
 *      Components can be layed out in one row or in multiple rows
 *      row hiding: show only one row and hide all other rows
 *      and many other features
 * 
 * Constraints:
 *      GROW (Component can grow),
 *      SHRINK (Component can shrink),
 *      FLEX (Component can shrink and grow) and
 *      FIXED (Component can't shrink or grow)
 * 
 * Layout policies:
 *      LAYOUT_POLICY_COMPUTE - preferred size is computed
 *      LAYOUT_POLICY_ASK - width of target container used as preferred width (X_AXIS) or height of target container used as preferred height (Y_AXIS)
 *      LAYOUT_POLICY_EXPLICITE_PIXELS - width (in pixels) was explicitly set
 *      LAYOUT_POLICY_EXPLICITE_COLUMNS - column count was explicitly set
 *      LAYOUT_POLICY_TABLE - same as LAYOUT_POLICY_EXPLICITE_COLUMNS, but layed out as table
 * 
 * </pre>
 * 
 * @author Andrey Kuznetsov
 */
public class RainLayout implements LayoutManager2, Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 3962297242706245069L;
    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;

    /**
     * preferred size is computed - like doing most layout managers
     */
    public static final int LAYOUT_POLICY_COMPUTE = 0;

    /**
     * width of parent returned as preferred width, preferred height computed,
     * based on width (X_AXIS) or height of parent returned as preferred height,
     * preferred width computed, based on height (Y_AXIS)
     */
    public static final int LAYOUT_POLICY_ASK = 1;

    /**
     * width (in pixels) was explicitly set
     */
    public static final int LAYOUT_POLICY_EXPLICITE_PIXELS = 2;

    /**
     * column count was explicitly set
     */
    public static final int LAYOUT_POLICY_EXPLICITE_COLUMNS = 3;

    /**
     * component will be layout as table
     */
    public static final int LAYOUT_POLICY_TABLE = 4;

    /**
     * component cannot grow or shrink
     */
    public static final String FIXED = "fixed";
    /**
     * component can grow
     */
    public static final String GROW = "grow";
    /**
     * component can shrink
     */
    public static final String SHRINK = "shrink";
    /**
     * component can both shrink and grow
     */
    public static final String FLEX = "flex";

    static final int SIZE = 0;
    static final int PREFERRED_SIZE = 1;
    static final int MINIMUM_SIZE = 2;
    static final int MAXIMUM_SIZE = 3;

    private int axis;

    Dimension gaps = new Dimension();

    int explicitSize;
    int layoutPolicy;
    boolean wrap;
    boolean reversedComponentOrder;
    boolean reverseRowOrder;

    private int columnCount;

    HashMap<Component, Object> ht = new HashMap<Component, Object>();

    boolean acrossStretch = true;

    boolean showOneRow;
    int shownRow = -1;

    DimensionHelper dimHelper = new DimensionHelper();

    Object defaultConstraints = FIXED;

    boolean adjustLastRow;
    boolean adjustRows = true;

    private boolean debug;

    /**
     * create horizontal single row RainLayout with hgap=0 and vgap=0
     */
    public RainLayout() {
	this(X_AXIS);
    }

    /**
     * create single row RainLayout with hgap=0 and vgap=0
     * 
     * @param axis
     *            layout axis
     * @see #X_AXIS
     * @see #Y_AXIS
     */
    public RainLayout(int axis) {
	this(axis, 0, 0);
    }

    /**
     * create single row RainLayout
     * 
     * @param axis
     *            layout axis
     * @param hgap
     *            horizontal gap
     * @param vgap
     *            vertical gap
     * @see #X_AXIS
     * @see #Y_AXIS
     */
    public RainLayout(int axis, int hgap, int vgap) {
	this(axis, hgap, vgap, LAYOUT_POLICY_COMPUTE);
    }

    /**
     * create new RainLayout
     * 
     * @param axis
     * @param hgap
     * @param vgap
     * @param layoutPolicy
     * @see #X_AXIS
     * @see #Y_AXIS
     * @see #LAYOUT_POLICY_ASK
     * @see #LAYOUT_POLICY_COMPUTE
     * @see #LAYOUT_POLICY_EXPLICITE_COLUMNS
     * @see #LAYOUT_POLICY_EXPLICITE_PIXELS
     */
    public RainLayout(int axis, int hgap, int vgap, int layoutPolicy) {
	this.axis = axis;
	gaps.width = vgap;
	gaps.height = hgap;
	this.layoutPolicy = layoutPolicy;
    }

    public boolean isDebug() {
	return debug;
    }

    public void setDebug(boolean debug) {
	this.debug = debug;
    }

    public int getWidth(Component c) {
	if (axis == RainLayout.X_AXIS) {
	    return c.getWidth();
	}
	return c.getHeight();
    }

    public int getHeight(Component c) {
	if (axis == RainLayout.X_AXIS) {
	    return c.getHeight();
	}
	return c.getWidth();
    }

    public int getX(Component c) {
	if (axis == RainLayout.X_AXIS) {
	    return c.getX();
	}
	return c.getY();
    }

    public int getY(Component c) {
	if (axis == RainLayout.X_AXIS) {
	    return c.getY();
	}
	return c.getX();
    }

    Dimension getSize(Component c, int type) {
	switch (type) {
	case SIZE:
	    return getSize(c);
	case PREFERRED_SIZE:
	    return getPreferredSize(c);
	case MINIMUM_SIZE:
	    return getMinimumSize(c);
	case MAXIMUM_SIZE:
	    return getMaximumSize(c);
	default:
	    throw new RuntimeException("unknown type");
	}
    }

    int getWidth(Dimension d) {
	if (axis == RainLayout.X_AXIS) {
	    return d.width;
	}
	return d.height;
    }

    Dimension getSize(Component c) {
	Dimension d = c.getSize();
	if (axis == RainLayout.X_AXIS) {
	    return d;
	}
	return new Dimension(d.height, d.width);
    }

    Dimension getPreferredSize(Component c) {
	Dimension d = c.getPreferredSize();
	if (axis == RainLayout.X_AXIS) {
	    return d;
	}
	return new Dimension(d.height, d.width);
    }

    Dimension getMinimumSize(Component c) {
	Dimension d = c.getMinimumSize();
	if (axis == RainLayout.X_AXIS) {
	    return d;
	}
	return new Dimension(d.height, d.width);
    }

    Dimension getMaximumSize(Component c) {
	Dimension d = c.getMaximumSize();
	if (axis == RainLayout.X_AXIS) {
	    return d;
	}
	return new Dimension(d.height, d.width);
    }

    void setBounds(Component c, int x, int y, int width, int height) {
	// if(c instanceof JLabel && width < 50) {
	// Thread.dumpStack();
	// }
	if (axis == RainLayout.X_AXIS) {
	    c.setBounds(x, y, width, height);
	} else {
	    c.setBounds(y, x, height, width);
	}
    }

    void setBounds(Component c, Rectangle r) {
	if (axis == RainLayout.X_AXIS) {
	    c.setBounds(r.x, r.y, r.width, r.height);
	} else {
	    c.setBounds(r.y, r.x, r.height, r.width);
	}
    }

    void setSize(Component c, int width, int height) {
	if (axis == RainLayout.X_AXIS) {
	    c.setSize(width, height);
	} else {
	    c.setSize(height, width);
	}
    }

    int getLeft(Insets insets) {
	if (axis == RainLayout.X_AXIS) {
	    return insets.left;
	}
	return insets.top;
    }

    int getTop(Insets insets) {
	if (axis == RainLayout.X_AXIS) {
	    return insets.top;
	}
	return insets.left;
    }

    int getRight(Insets insets) {
	if (axis == RainLayout.X_AXIS) {
	    return insets.right;
	}
	return insets.bottom;
    }

    int getBottom(Insets insets) {
	if (axis == RainLayout.X_AXIS) {
	    return insets.bottom;
	}
	return insets.right;
    }

    int getHeight(Dimension d) {
	if (axis == RainLayout.X_AXIS) {
	    return d.height;
	}
	return d.width;
    }

    public int getAxis() {
	return axis;
    }

    public void setAxis(int axis) {
	if (this.axis != axis) {
	    this.axis = axis;
	}
    }

    public int getHgap() {
	return gaps.height;
    }

    public void setHgap(int hgap) {
	if (gaps.height != hgap) {
	    gaps.height = hgap;
	}
    }

    public int getVgap() {
	return gaps.width;
    }

    public void setVgap(int vgap) {
	if (gaps.width != vgap) {
	    gaps.width = vgap;
	}
    }

    public boolean isWrap() {
	return wrap;
    }

    public void setWrap(boolean wrap) {
	this.wrap = wrap;
    }

    public boolean isShowOneRow() {
	return showOneRow;
    }

    public void setShowOneRow(boolean showOneRow) {
	this.showOneRow = showOneRow;
    }

    public int getShownRow() {
	return shownRow;
    }

    public void setShownRow(int shownRow) {
	this.shownRow = shownRow;
    }

    /**
     * default constraints are used if supplied constraints was null
     */
    public Object getDefaultConstraints() {
	return defaultConstraints;
    }

    /**
     * default constraints are used if supplied constraints was null
     */
    public void setDefaultConstraints(Object constraints) {
	if (constraints == null) {
	    throw new NullPointerException("defaultConstraints can't be null");
	}
	this.defaultConstraints = constraints;
    }

    public void addLayoutComponent(String name, Component comp) {
	if (name != null) {
	    ht.put(comp, FIXED);
	}
    }

    public void addLayoutComponent(Component comp, Object constraints) {
	if (constraints != null) {
	    ht.put(comp, constraints);
	}
    }

    public void removeLayoutComponent(Component comp) {
	ht.remove(comp);
    }

    public float getLayoutAlignmentX(Container target) {
	return 0.5f;
    }

    public float getLayoutAlignmentY(Container target) {
	return 0.5f;
    }

    public int getExplicitSize() {
	return explicitSize;
    }

    public void setExplicitSize(int explicitSize) {
	this.explicitSize = explicitSize;
    }

    public int getLayoutPolicy() {
	return layoutPolicy;
    }

    public void setLayoutPolicy(int policy) {
	this.layoutPolicy = policy;
    }

    public int getRowCount(Container c) {
	preferredLayoutSize(c);
	return dimHelper.rowCount;
    }

    public int getColumnCount() {
	return columnCount;
    }

    public void setColumnCount(int columnCount) {
	this.columnCount = columnCount;
	this.dimHelper.columns = null;
    }

    public boolean isAcrossStretch() {
	return acrossStretch;
    }

    public void setAcrossStretch(boolean acrossStretch) {
	this.acrossStretch = acrossStretch;
    }

    public boolean isReversedComponentOrder() {
	return reversedComponentOrder;
    }

    public void setReversedComponentOrder(boolean reversedComponentOrder) {
	this.reversedComponentOrder = reversedComponentOrder;
    }

    public boolean isReverseRowOrder() {
	return reverseRowOrder;
    }

    public void setReverseRowOrder(boolean reverseRowOrder) {
	this.reverseRowOrder = reverseRowOrder;
    }

    public Object getConstraints(Object o) {
	Object constraints = ht.get(o);
	if (constraints == null) {
	    return defaultConstraints;
	}
	return constraints;
    }

    public void invalidateLayout(Container target) {
    }

    public boolean isAdjustLastRow() {
	return adjustLastRow;
    }

    public void setAdjustLastRow(boolean adjustLastRow) {
	this.adjustLastRow = adjustLastRow;
    }

    public boolean isAdjustRows() {
	return adjustRows;
    }

    public void setAdjustRows(boolean adjustRows) {
	this.adjustRows = adjustRows;
    }

    private class DimensionHelper {
	Container parent;
	Dimension psize;
	Dimension res = new Dimension();
	int maxRowHeight;
	int maxRowWidth;

	HashMap<Integer, Integer> rws = new HashMap<Integer, Integer>();

	int rowCount;
	int column;

	int[] columns;

	public void start(Container parent) {
	    this.parent = parent;
	    switch (layoutPolicy) {
	    case LAYOUT_POLICY_COMPUTE:
		psize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
		break;
	    case LAYOUT_POLICY_ASK:
		psize = getSize(parent);
		break;
	    case LAYOUT_POLICY_EXPLICITE_PIXELS:
		psize = new Dimension(explicitSize, explicitSize);
		break;
	    case LAYOUT_POLICY_EXPLICITE_COLUMNS:
		break;
	    case LAYOUT_POLICY_TABLE:
		columns = new int[columnCount];
		break;
	    default:
		throw new RuntimeException("unknown layout policy:" + layoutPolicy);
	    }
	    maxRowHeight = 0;
	    maxRowWidth = 0;
	    res = new Dimension();
	    rowCount = 1;
	}

	public Dimension finish() {
	    rws.put(new Integer(rowCount), new Integer(maxRowHeight));
	    res.height += maxRowHeight;
	    if (layoutPolicy == LAYOUT_POLICY_EXPLICITE_PIXELS) {
		res.width = explicitSize;
	    } else if (layoutPolicy == LAYOUT_POLICY_EXPLICITE_COLUMNS) {
		res.width = maxRowWidth;
	    } else if (layoutPolicy == LAYOUT_POLICY_TABLE) {
		res.width = 0;
		for (int i = 0; i < columns.length; i++) {
		    res.width += columns[i];
		}
	    }
	    if (showOneRow) {
		int row = Math.min(shownRow + 1, rowCount);
		Integer integer = rws.get(new Integer(row));
		if (integer != null) {
		    res.height = integer.intValue();
		}
	    }

	    Insets insets = parent.getInsets();
	    res.width += insets.left + insets.right;
	    res.height += insets.top + insets.bottom;
	    return axis == X_AXIS ? res : new Dimension(res.height, res.width);
	}

	public void computeNext(Dimension d) {
	    boolean useColumns = layoutPolicy == LAYOUT_POLICY_EXPLICITE_COLUMNS || layoutPolicy == LAYOUT_POLICY_TABLE;
	    boolean b0 = (useColumns && column++ < columnCount);
	    int size = res.width + d.width + getWidth(gaps);
	    boolean b1 = !useColumns && (size <= psize.width);
	    if (b0 || b1) {
		maxRowHeight = Math.max(maxRowHeight, d.height + getHeight(gaps));
		res.width += d.width + getWidth(gaps);
		if (layoutPolicy == LAYOUT_POLICY_TABLE) {
		    columns[column - 1] = Math.max(columns[column - 1], d.width);
		}
	    } else {
		column = 1;
		rws.put(new Integer(rowCount), new Integer(maxRowHeight + getHeight(gaps)));
		res.height += maxRowHeight + getHeight(gaps);
		maxRowHeight = d.height + getHeight(gaps);
		maxRowWidth = Math.max(res.width, maxRowWidth);
		res.width = d.width + getWidth(gaps);
		if (layoutPolicy == LAYOUT_POLICY_TABLE) {
		    columns[column - 1] = Math.max(columns[column - 1], d.width);
		}
		rowCount++;
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private Dimension getSizeP(Container parent, int type) {
	if (parent instanceof SortableContainer) {
	    Component[] components = ((SortableContainer) parent).getComponentsOrdered();
	    if (components != null) {
		return computeSize(parent, components, type);
	    }
	    Comparator<Component> cmp = (Comparator<Component>) ((SortableContainer) parent).getComparator();
	    if (cmp != null) {
		components = parent.getComponents();
		Arrays.sort(components, cmp);
		return computeSize(parent, components, type);
	    }
	} else if (reversedComponentOrder) {
	    Component[] components = parent.getComponents();
	    if (reversedComponentOrder) {
		components = swap(components);
	    }
	    return computeSize(parent, components, type);
	}
	synchronized (parent.getTreeLock()) {
	    int count = parent.getComponentCount();
	    dimHelper.start(parent);
	    for (int i = 0; i < count; i++) {
		Component c = parent.getComponent(i);
		if (c.isVisible()) {
		    Dimension d = getSize(c, type);
		    dimHelper.computeNext(d);
		}
	    }
	    return dimHelper.finish();
	}
    }

    private Dimension computeSize(Container parent, Component[] components, int type) {
	synchronized (parent.getTreeLock()) {
	    dimHelper.start(parent);
	    for (int i = 0; i < components.length; i++) {
		Component c = components[i];
		if (c.isVisible()) {
		    Dimension d = getSize(c, type);
		    dimHelper.computeNext(d);
		}
	    }
	    return dimHelper.finish();
	}
    }

    private Component[] swap(Component[] components) {
	Component[] comps = new Component[components.length];
	for (int i = 0; i < components.length; i++) {
	    comps[i] = components[components.length - 1 - i];
	}
	return comps;
    }

    public Dimension preferredLayoutSize(Container parent) {
	Dimension sizeP = getSizeP(parent, PREFERRED_SIZE);
	// System.out.println(sizeP);
	return sizeP;
    }

    public Dimension minimumLayoutSize(Container parent) {
	return getSizeP(parent, MINIMUM_SIZE);
    }

    public Dimension maximumLayoutSize(Container parent) {
	return getSizeP(parent, MAXIMUM_SIZE);
    }

    @SuppressWarnings("unchecked")
    public void layoutContainer(Container parent) {
	if (parent instanceof SortableContainer) {
	    Component[] components = ((SortableContainer) parent).getComponentsOrdered();
	    if (components == null) {
		Comparator<Component> cmp = (Comparator<Component>) ((SortableContainer) parent).getComparator();
		if (cmp != null) {
		    components = parent.getComponents();
		    Arrays.sort(components, cmp);
		}
	    }
	    if (components != null) {
		if (reversedComponentOrder) {
		    components = swap(components);
		}
		layoutX(parent, components);
		return;
	    }
	}
	if (reversedComponentOrder) {
	    Component[] components = parent.getComponents();
	    components = swap(components);
	    layoutX(parent, components);
	} else {
	    Component[] components = parent.getComponents();
	    layoutX(parent, components);
	}
    }

    private void layoutX(Container parent, Component[] components) {
	Dimension preferredSize = null;
	if (reverseRowOrder || dimHelper.columns == null) {
	    preferredSize = preferredLayoutSize(parent);
	}
	Insets insets = parent.getInsets();
	int maxW = getWidth(parent) - (getLeft(insets) + getRight(insets));
	int count = components.length;

	Dimension pos = new Dimension();
	pos.width = getLeft(insets);
	pos.height = getTop(insets);
	pos.height = reverseRowOrder ? getHeight(preferredSize) - getBottom(insets) : getTop(insets);
	int max = 0;
	int first = 0;

	boolean wrp = wrap | (layoutPolicy != LAYOUT_POLICY_COMPUTE);
	boolean useColumns = layoutPolicy == LAYOUT_POLICY_EXPLICITE_COLUMNS || layoutPolicy == LAYOUT_POLICY_TABLE;

	int column = 0;

	int row = 0;
	int _shownRow = 0;
	if (showOneRow && shownRow >= 0) {
	    _shownRow = Math.min(shownRow, getRowCount(parent) - 1);
	}
	for (int i = 0; i < components.length; i++) {
	    Component c = components[i];

	    if (c.isVisible()) {
		Dimension d = getPreferredSize(c);

		boolean b0 = useColumns && column++ < columnCount;
		boolean b1 = !useColumns && (pos.width == getLeft(insets) || pos.width + (d.width + getWidth(gaps)) <= maxW);
		if (!wrp || (b0 || b1)) {
		    if (layoutPolicy == LAYOUT_POLICY_TABLE) {
			setBounds(c, pos.width, pos.height, dimHelper.columns[column - 1], d.height);
			pos.width += dimHelper.columns[column - 1] + getWidth(gaps);
		    } else {
			setBounds(c, pos.width, pos.height, d.width, d.height);
			pos.width += d.width + getWidth(gaps);
		    }
		    max = Math.max(max, d.height + getHeight(gaps));
		} else {
		    column = 1;
		    boolean hideRow = !(row++ == _shownRow || (!showOneRow || shownRow < 0));
		    if (!hideRow) {
			adjustRow(parent, components, first, i, maxW, max, false);
			if (reverseRowOrder) {
			    pos.height -= max + getHeight(gaps);
			} else {
			    pos.height += max + getHeight(gaps);
			}
		    } else {
			hideRow(components, first, i);
		    }
		    first = i;

		    pos.width = getLeft(insets);
		    if (layoutPolicy == LAYOUT_POLICY_TABLE) {
			setBounds(c, pos.width, pos.height, dimHelper.columns[column - 1], d.height);
			pos.width += dimHelper.columns[column - 1] + getWidth(gaps);
		    } else {
			setBounds(c, pos.width, pos.height, d.width, d.height);
			pos.width += d.width + getWidth(gaps);
		    }
		    max = d.height + getHeight(gaps);
		}
	    }
	}
	boolean hideRow = !(row++ == _shownRow || (!showOneRow || shownRow < 0));
	if (!hideRow) {
	    adjustRow(parent, components, first, count, maxW, max, true);
	} else {
	    hideRow(components, first, count);
	}
    }

    void hideRow(Container parent, int first, int last) {
	for (int i = first; i < last; i++) {
	    Component c = parent.getComponent(i);
	    Rectangle r = c.getBounds();
	    c.setBounds(-r.x, -r.y, 0, 0);
	}
    }

    void hideRow(Component[] components, int first, int last) {
	for (int i = first; i < last; i++) {
	    Component c = components[i];
	    Rectangle r = c.getBounds();
	    c.setBounds(-r.x, -r.y, 0, 0);
	}
    }

    private void adjustRow(Container parent, Component[] components, int first, int last, int rowWidth, int rowHeight, boolean lastRow) {

	if (!adjustRows) {
	    return;
	}

	if (lastRow && !adjustLastRow && dimHelper.rowCount > 1) {
	    if (layoutPolicy == LAYOUT_POLICY_TABLE) {
		// int f = Math.max(first, columnCount);
		for (int i = first; i < last; i++) {
		    Component c = components[i];
		    int k = i - columnCount;
		    if (k >= 0) {
			Component c0 = components[k];
			setBounds(c, getX(c0), getY(c), getWidth(c0), getHeight(c));
		    }
		}
	    }
	    if (reverseRowOrder) {
		for (int i = first; i < last; i++) {
		    Component c = components[i];
		    setBounds(c, getX(c), getY(c) - rowHeight, getWidth(c), getHeight(c));
		}
	    }
	    return;
	}

	int w = 0;
	ArrayList<Component> list = new ArrayList<Component>();
	int column = 0;
	if (layoutPolicy == LAYOUT_POLICY_TABLE) {
	    for (int i = first; i < last; i++) {
		Component c = components[i];
		if (c.isVisible()) {
		    list.add(c);
		    w += dimHelper.columns[column++] + getWidth(gaps);
		}
	    }
	} else {
	    for (int i = first; i < last; i++) {
		Component c = components[i];
		if (c.isVisible()) {
		    list.add(c);
		    w += getPreferredSize(c).width + getWidth(gaps);
		}
	    }
	}
	if (list.size() == 0) {
	    return;
	}
	if (rowWidth >= w) {
	    adjustRowGrow(parent, list, w, rowWidth, rowHeight);
	} else {
	    adjustRowShrink(parent, list, rowWidth, w, rowHeight);
	}
    }

    private void adjustRowGrow(Container parent, ArrayList<Component> list, int preferredWidth, int rowWidth, int rowHeight) {
	int count = list.size();
	int cnt = count;
	if (rowWidth > preferredWidth) {
	    for (int i = 0; i < list.size(); i++) {
		Component component = list.get(i);
		Object o = getConstraints(component);
		if (o != GROW && o != FLEX) {
		    cnt--;
		}
	    }
	} else if (rowWidth < preferredWidth) {
	    for (int i = 0; i < list.size(); i++) {
		Component component = list.get(i);
		Object o = getConstraints(component);
		if (o != SHRINK && o != FLEX) {
		    cnt--;
		}
	    }
	}

	cnt = cnt == 0 ? 1 : cnt;
	int dw = (rowWidth - preferredWidth) / cnt;
	int height = rowHeight - getHeight(gaps);

	int adjY = reverseRowOrder ? rowHeight : 0;
	// layout first component
	{
	    Component c = list.get(0);
	    Insets insets = parent.getInsets();
	    Object o = getConstraints(c);
	    boolean stretch = (rowWidth > preferredWidth && (o == GROW || o == FLEX)) || (rowWidth < preferredWidth && (o == SHRINK || o == FLEX));

	    int width = getWidth(c) + (stretch ? dw : 0);
	    if (acrossStretch) {
		setBounds(c, getLeft(insets), getY(c) - adjY, width, height);
	    } else {
		setBounds(c, getLeft(insets), getY(c) - adjY + (height - getHeight(c)), width, getHeight(c) /**
		 * 
		 * 
		 * c.getAlignmentY()
		 */
		);
	    }
	}
	for (int i = 1; i < count - 1; i++) {
	    Component c = list.get(i);
	    Component c0 = list.get(i - 1);
	    Object o = getConstraints(c);
	    boolean stretch = (rowWidth > preferredWidth && (o == GROW || o == FLEX)) || (rowWidth < preferredWidth && (o == SHRINK || o == FLEX));

	    int width = getWidth(c) + (stretch ? dw : 0);
	    int x = getX(c0) + getWidth(c0) + getWidth(gaps);
	    if (acrossStretch) {
		setBounds(c, x, getY(c) - adjY, width, height);
	    } else {
		setBounds(c, x, getY(c) - adjY + +(height - getHeight(c)), width, getHeight(c) /**
		 * 
		 * 
		 * c.getAlignmentY()
		 */
		);
	    }
	}
	// layout last component - only if we have more the 1 component
	if (count > 1) {
	    Component c = list.get(count - 1);
	    Component c0 = list.get(count - 2);
	    int x = getX(c0) + getWidth(c0) + getWidth(gaps);
	    Object o = getConstraints(c);
	    boolean stretch = (rowWidth > preferredWidth && (o == GROW || o == FLEX)) || (rowWidth < preferredWidth && (o == SHRINK || o == FLEX));

	    int width = stretch ? rowWidth - x : getWidth(c);
	    if (acrossStretch) {
		setBounds(c, x, getY(c) - adjY, width, height);
	    } else {
		setBounds(c, x, getY(c) - adjY + (height - getHeight(c)), width, getHeight(c) /**
		 * 
		 * 
		 * c.getAlignmentY()
		 */
		);
	    }
	}
    }

    private void adjustRowShrink(Container parent, ArrayList<Component> list, int rowWidth, int w, int h) {
	int count = list.size();
	int cnt = count;
	int dw = 0;
	int _dw = 0;

	int deficite = w - rowWidth;
	int possibleShrink = 0;
	ArrayList<Component> shrinkable = new ArrayList<Component>();
	for (int i = 0; i < list.size(); i++) {
	    Component component = list.get(i);
	    Object o = getConstraints(component);
	    if (o == SHRINK || o == FLEX) {
		shrinkable.add(component);
		possibleShrink += getWidth(component);
	    }
	}
	if (deficite > possibleShrink) {
	    _dw = (deficite - possibleShrink) / count;
	}
	cnt = shrinkable.size();

	if (cnt > 0) {
	    dw = deficite / cnt;
	}
	if (dw > 0) {
	    for (int j = 0; j < shrinkable.size(); j++) {
		boolean removed = false;
		for (int i = shrinkable.size() - 1; i >= 0; i--) {
		    Component component = shrinkable.get(i);
		    if (getWidth(component) < dw) {
			int msh = getWidth(component) - _dw;
			setSize(component, _dw, getHeight(component));
			shrinkable.remove(i);
			removed = true;
			deficite -= msh;
		    }
		}
		if (removed) {
		    cnt = shrinkable.size();
		    if (cnt > 0) {
			dw = deficite / cnt;
		    }
		}
		for (int i = 0; i < shrinkable.size(); i++) {
		    Component component = shrinkable.get(i);
		    setSize(component, getWidth(component) - dw, getHeight(component));
		}
		if (!removed) {
		    break;
		}
	    }
	}

	int height = h - getHeight(gaps);
	// int adjY = 0;
	int adjY = reverseRowOrder ? h : 0;

	{
	    Component c = list.get(0);
	    Insets insets = parent.getInsets();
	    setBounds(c, getLeft(insets), getY(c) - adjY, getWidth(c) - _dw, acrossStretch ? height : getHeight(c));
	}
	for (int i = 1; i < count - 1; i++) {
	    Component c = list.get(i);
	    Component c0 = list.get(i - 1);
	    int x = getX(c0) + getWidth(c0) + getWidth(gaps);
	    setBounds(c, x, getY(c) - adjY, getWidth(c) - _dw, acrossStretch ? height : getHeight(c));
	}
	if (count > 1) {
	    Component c = list.get(count - 1);
	    Component c0 = list.get(count - 2);
	    int x = getX(c0) + Math.max(getWidth(c0), 0) + getWidth(gaps);
	    int width = rowWidth - x;
	    setBounds(c, x, getY(c) - adjY, Math.max(width, getWidth(c)), acrossStretch ? height : getHeight(c));
	}
    }
}
