/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * http://jgui.imagero.com
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
 *  o Neither the name of Andrey Kuznetsov nor the names of
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

package com.smartg.swing.titlebar.plaf.basic;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.smartg.swing.titlebar.TitleBar;
import com.smartg.swing.titlebar.plaf.BumpRenderer;
import com.smartg.swing.titlebar.plaf.TitleBarRenderer;
import com.smartg.swing.titlebar.plaf.TitleBarUI;

/**
 * @author Andrei Kouznetsov
 * Date: 04.12.2003
 * Time: 16:53:43
 */
public class BasicTitleBarUI extends TitleBarUI {

    private static final BasicTitleBarUI basicTitleBarUI = new BasicTitleBarUI();

    public static ComponentUI createUI(JComponent c) {
        return basicTitleBarUI;
    }

    TitleBarRenderer tbr = new /*Gradient*/BumpRenderer();

    @Override
	public Dimension getPreferredSize(JComponent c) {
        TitleBar tbar = (TitleBar) c;
        if (tbar.isPalette()) {
            return new Dimension(10, 10);
        }
        else {
//            int height = tbar.getFontMetrics(tbar.getFont()).getHeight() + 4;
//            Insets insets = tbar.getInsets();
//            height += insets.top + insets.bottom;
//
//            height = Math.max(closeIcon.getIconHeight() + insets.top + insets.bottom, height);
//
//            Dimension d = new Dimension(height, height);
//            return d;
            return null;
        }
    }
}
