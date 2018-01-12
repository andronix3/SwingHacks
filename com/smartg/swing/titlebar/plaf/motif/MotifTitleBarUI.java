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

package com.smartg.swing.titlebar.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import com.smartg.swing.titlebar.TitleBar;
import com.smartg.swing.titlebar.plaf.basic.BasicTitleBarUI;

/**
 * @author Andrei Kouznetsov
 * Date: 04.12.2003
 * Time: 16:53:43
 */
public class MotifTitleBarUI extends BasicTitleBarUI {

    static MotifTitleBarUI motifTitleBarUI = new MotifTitleBarUI();

    public static ComponentUI createUI(JComponent c) {
        return motifTitleBarUI;
    }

    @Override
	public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        TitleBar tb = (TitleBar) c;

        if (tb.isPalette()) {
            Color bg = tb.getBackground();

            if (tb.isArmed()) {
                float[] hsb = new float[3];
                hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), hsb);
                hsb[2] -= 0.1f;
                bg = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
            }
            g.setColor(bg);
            g.fillRect(0, 0, size.width, size.height);
        }
        else {
            Color color = getBaseColor(tb);
            Color highlight = color.brighter();
            Color shadow = color.darker().darker();

            Dimension d = tb.getSize();
            int maxX = d.width - 1;
            int maxY = d.height - 1;

            // draw background
            g.setColor(color);
            g.fillRect(0, 0, d.width, d.height);

            // draw border
            g.setColor(highlight);
            g.drawLine(0, 0, maxX, 0);
            g.drawLine(0, 0, 0, maxY);
            g.setColor(shadow);
            g.drawLine(1, maxY, maxX, maxY);
            g.drawLine(maxX, 1, maxX, maxY);
        }
    }

    public Color getBaseColor(TitleBar tb) {
        if (tb.isActive()) {
            return UIManager.getColor("InternalFrame.activeTitleBackground");
        }
        else {
            return UIManager.getColor("InternalFrame.inactiveTitleBackground");
        }
    }

    @Override
	public Icon getSystemIcon(TitleBar tb) {
        return new MotifSystemIcon(((MotifTitleBarUI)tb.getUI()).getBaseColor(tb));
    }

    @Override
	public Icon getPressedSystemIcon(TitleBar tb) {
        return new MotifPressedSystemIcon(((MotifTitleBarUI) tb.getUI()).getBaseColor(tb));
    }

}
