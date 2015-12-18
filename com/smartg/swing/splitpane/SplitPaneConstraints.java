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

/**
 * @author Andrey Kuznetsov
 */
public class SplitPaneConstraints {
    private int orientation;
    private int align;
    private Component component;

    /**
     * create SplitPaneConstraints
     *
     * @param comp component used to determine parent node (null to add to root node)
     * @param orientation SplitConstants#HORIZONTAL_SPLIT or SplitConstants.VERTICAL_SPLIT
     * @param align SplitConstants.ALIGN_TOP, SplitConstants.ALIGN_LEFT, SplitConstants.ALIGN_BOTTOM, SplitConstants.ALIGN_RIGHT
     * @see SplitConstants
     */
    public SplitPaneConstraints(Component comp, int orientation, int align) {
        this.component = comp;
        this.orientation = orientation;
        this.align = align;
    }

    /**
     * get orientation
     * @see SplitConstants
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * get align
     * @see SplitConstants
     */
    public int getAlign() {
        return align;
    }

    /**
     * get component
     * @return component used to determine parent node (or null)
     */
    public Component getComponent() {
        return component;
    }


}
