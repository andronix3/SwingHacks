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

import javax.swing.JSplitPane;

public interface SplitConstants {
    /**
     * vertical splitpane
     */
    public static final int VERTICAL_SPLIT = JSplitPane.VERTICAL_SPLIT;

    /**
     * horizontal splitpane
     */
    public static final int HORIZONTAL_SPLIT = JSplitPane.HORIZONTAL_SPLIT;

    /**
     * add Component at top or left (depends on current splitpane option)
     */
    public static final int ALIGN_TOP = 0;

    /**
     * add Component at top or left (depends on current splitpane option)
     */
    public static final int ALIGN_LEFT = 0;

    /**
     * add Component at bottom or right (depends on current splitpane option)
     */
    public static final int ALIGN_BOTTOM = 1;

    /**
     * add Component at bottom or right (depends on current splitpane option)
     */
    public static final int ALIGN_RIGHT = 1;
}
