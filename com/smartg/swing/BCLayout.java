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
import java.util.HashMap;

/**
 * BCLayout - like BorderLayout but components may be also placed in corners.
 * Obsolete since JNodeLayout is there!
 *
 * @author Andrey Kuznetsov
 */
public class BCLayout implements LayoutManager2 {

    int hgap;
    int vgap;

    HashMap<String, Component> compTable = new HashMap<String, Component>(9);
    HashMap<Component, String> revTable = new HashMap<Component, String>(9);

    public static final String NORTH = "North";
    public static final String SOUTH = "South";
    public static final String EAST = "East";
    public static final String WEST = "West";
    public static final String CENTER = "Center";
    public static final String NORTH_EAST = "NorthEast";
    public static final String NORTH_WEST = "NorthWest";
    public static final String SOUTH_EAST = "SouthEast";
    public static final String SOUTH_WEST = "SouthWest";
    

    //used only internally.
    private static final Dimension nd = new Dimension();

    boolean fillEmptyCorners;

    public boolean isFillEmptyCorners() {
        return fillEmptyCorners;
    }

    public void setFillEmptyCorners(boolean b) {
        this.fillEmptyCorners = b;
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        addLayoutComponent((String) constraints, comp);
    }

    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    public void invalidateLayout(Container target) {
    }

    public void addLayoutComponent(String name, Component comp) {
        if (name == null) {
            name = CENTER;
        }
        Object key = revTable.remove(comp);
        compTable.remove(name);
        if(key != null) {
            compTable.remove(key);
        }

        compTable.put(name, comp);
        revTable.put(comp, name);
    }

    public void removeLayoutComponent(Component comp) {
        Object key = revTable.remove(comp);
        if (key != null) {
            compTable.remove(key);
        }
    }

    Dimension computePreferredRow(Component left, Component center, Component right) {
        Dimension dl = left != null ? left.getPreferredSize() : nd;
        Dimension dc = center != null ? center.getPreferredSize() : nd;
        Dimension dr = right != null ? right.getPreferredSize() : nd;

        return new Dimension(dl.width + dc.width + dr.width + hgap + hgap, Math.max(dl.height, Math.max(dr.height, dc.height)));
    }

    Dimension computeMinimumRow(Component left, Component center, Component right) {
        Dimension dl = left != null ? left.getMinimumSize() : nd;
        Dimension dc = center != null ? center.getMinimumSize() : nd;
        Dimension dr = right != null ? right.getMinimumSize() : nd;

        return new Dimension(dl.width + dc.width + dr.width + hgap + hgap, Math.max(dl.height, Math.max(dr.height, dc.height)));
    }

    Dimension computePreferredColumn(Component top, Component center, Component bottom) {
        Dimension dt = top != null ? top.getPreferredSize() : nd;
        Dimension dc = center != null ? center.getPreferredSize() : nd;
        Dimension db = bottom != null ? bottom.getPreferredSize() : nd;

        return new Dimension(Math.max(dt.width, Math.max(db.width, dc.width)), dt.height + dc.height + db.height + vgap + vgap);
    }

    Dimension computeMinimumColumn(Component top, Component center, Component bottom) {
        Dimension dt = top != null ? top.getMinimumSize() : nd;
        Dimension dc = center != null ? center.getMinimumSize() : nd;
        Dimension db = bottom != null ? bottom.getMinimumSize() : nd;

        return new Dimension(Math.max(dt.width, Math.max(db.width, dc.width)), dt.height + dc.height + db.height + vgap + vgap);
    }

    private Dimension computePreferredEastColumn() {
        return computePreferredColumn(get(NORTH_EAST), get(EAST), get(SOUTH_EAST));
    }

    private Dimension computePreferredWestColumn() {
        return computePreferredColumn(get(NORTH_WEST), get(WEST), get(SOUTH_WEST));
    }

    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            return computePreferredSize(target);
        }
    }

    private Dimension computePreferredSize(Container target) {
        Dimension topRow = computePreferredTopRow();
        Dimension middleRow = computePreferredMiddleRow();
        Dimension bottomRow = computePreferredBottomRow();

        Insets insets = target.getInsets();
        Dimension d = new Dimension(
                Math.max(topRow.width, Math.max(middleRow.width, bottomRow.width)),
                topRow.height + middleRow.height + bottomRow.height + vgap + vgap);
        d.width += insets.left + insets.right;
        d.height += insets.top + insets.bottom;
        return d;
    }

    private Dimension computePreferredBottomRow() {
        return computePreferredRow(get(SOUTH_EAST), get(SOUTH), get(SOUTH_WEST));
    }

    private Dimension computePreferredMiddleRow() {
        return computePreferredRow(get(EAST), get(CENTER), get(WEST));
    }

    private Dimension computePreferredTopRow() {
        return computePreferredRow(get(NORTH_EAST), get(NORTH), get(NORTH_WEST));
    }

    public Dimension minimumLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            return computeMinimumSize(target);
        }
    }

    private Dimension computeMinimumSize(Container target) {
        Dimension topRow = computeMinimumTopRow();
        Dimension middleRow = computeMinimumMiddleRow();
        Dimension bottomRow = computeMinimumBottomRow();

        Insets insets = target.getInsets();
        Dimension d = new Dimension(
                Math.max(topRow.width, Math.max(middleRow.width, bottomRow.width)),
                topRow.height + middleRow.height + bottomRow.height + vgap + vgap);
        d.width += insets.left + insets.right;
        d.height += insets.top + insets.bottom;
        return d;
    }

    private Dimension computeMinimumBottomRow() {
        return computeMinimumRow(get(SOUTH_EAST), get(SOUTH), get(SOUTH_WEST));
    }

    private Dimension computeMinimumMiddleRow() {
        return computeMinimumRow(get(EAST), get(CENTER), get(WEST));
    }

    private Dimension computeMinimumTopRow() {
        return computeMinimumRow(get(NORTH_EAST), get(NORTH), get(NORTH_WEST));
    }

    Component get(String key) {
        Component c = compTable.get(key);
        if(c == null || !c.isVisible()) {
            return null;
        }
        return c;
    }

    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();

            Dimension size = target.getSize();
            size.width -= insets.left + insets.right;
            size.height -= insets.top + insets.bottom;

            final int top = insets.top;
            final int bottom = target.getHeight() - insets.bottom;
            final int left = insets.left;
            final int right = target.getWidth() - insets.right;

            Dimension topRow = computePreferredTopRow();
            Dimension bottomRow = computePreferredBottomRow();

            Dimension rightColumn = computePreferredEastColumn();
            Dimension leftColumn = computePreferredWestColumn();

            Component nw = get(NORTH_WEST);
            if(nw != null) {
                nw.setBounds(left, top, leftColumn.width, topRow.height);
            }

            Component sw = get(SOUTH_WEST);
            if(sw != null) {
                sw.setBounds(left, bottom, leftColumn.width, bottomRow.height);
            }

            Component ne = get(NORTH_EAST);
            if(ne != null) {
                ne.setBounds(right - rightColumn.width, top, rightColumn.width, topRow.height);
            }

            Component se = get(SOUTH_EAST);
            if(se != null) {
                se.setBounds(right - rightColumn.width, bottom - bottomRow.height, rightColumn.width, bottomRow.height);
            }

            Component n = get(NORTH);
            if(n != null) {
                int _left = left + ((nw != null || !fillEmptyCorners) ? leftColumn.width : 0);
                n.setBounds(_left, top, right - ((ne != null || !fillEmptyCorners) ? rightColumn.width : 0) - _left, topRow.height);
            }

            Component s = get(SOUTH);
            if(s != null) {
                int _left = left + ((sw != null || !fillEmptyCorners) ? leftColumn.width : 0);
                s.setBounds(_left, bottom - bottomRow.height, right - ((se != null || !fillEmptyCorners) ? rightColumn.width : 0) - _left, bottomRow.height);
            }

            Component w = get(WEST);
            if(w != null) {
                int _top = top + ((nw != null || !fillEmptyCorners || n != null) ? topRow.height : 0);
                w.setBounds(left, _top, leftColumn.width, bottom - ((sw != null || !fillEmptyCorners || s != null) ? bottomRow.height: 0) - _top);
            }

            Component e = get(EAST);
            if(e != null) {
                int _top = top + ((nw != null || !fillEmptyCorners || n != null) ? topRow.height : 0);
                e.setBounds(right - rightColumn.width, _top, rightColumn.width, bottom - ((sw != null || !fillEmptyCorners || s != null) ? bottomRow.height: 0) - _top);
            }

            Component c = get(CENTER);
            if(c != null) {
                int _left = left + leftColumn.width;
                int _top = top + topRow.height;
                c.setBounds(_left, _top, right - rightColumn.width - _left, bottom - bottomRow.height - _top);
            }
        }
    }
}
