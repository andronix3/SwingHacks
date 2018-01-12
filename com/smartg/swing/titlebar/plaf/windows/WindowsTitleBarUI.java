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

package com.smartg.swing.titlebar.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import com.smartg.swing.ComponentRenderer;
import com.smartg.swing.titlebar.TitleBar;
import com.smartg.swing.titlebar.plaf.basic.BasicTitleBarUI;

/**
 * @author Andrei Kouznetsov
 *         Date: 04.12.2003
 *         Time: 16:53:43
 */
public class WindowsTitleBarUI extends BasicTitleBarUI {

    static WindowsTitleBarUI windowsTitleBarUI = new WindowsTitleBarUI();

    public static ComponentUI createUI(JComponent c) {
        return windowsTitleBarUI;
    }

    private Color selectedTitleColor, notSelectedTitleColor;
    private Color selectedTitleGradientColor, notSelectedTitleGradientColor;

    WindowsTitleBarRenderer renderer = new WindowsTitleBarRenderer();
    ComponentRenderer proxy = new ComponentRenderer(renderer);

    public WindowsTitleBarUI() {
        selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
        notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");

        selectedTitleGradientColor = UIManager.getColor("InternalFrame.activeTitleGradient");
        notSelectedTitleGradientColor = UIManager.getColor("InternalFrame.inactiveTitleGradient");
    }

    @Override
	protected JLabel createGlueLabel(TitleBar tb) {
        return new JLabel();
    }

    @Override
	public void paint(Graphics g, JComponent c) {
        TitleBar tb = (TitleBar) c;
        Dimension size = c.getSize();
        proxy.setSize(size);
        proxy.setAxis(tb.getAxis());
        renderer.tb = tb;
        proxy.paint(g);
    }

    class WindowsTitleBarRenderer extends JComponent {
       

		/**
		 * 
		 */
		private static final long serialVersionUID = 1615749524928450262L;

		TitleBar tb;

        private boolean bvar = true;
        
        @Override
		public void paint(Graphics g) {
            Dimension size = getSize();

            if (tb.isPalette()) {
                Color bg = tb.getBackground();

                if (tb.isArmed()) {
                    float[] hsb = new float[3];
                    hsb = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), hsb);
                    hsb[2] -= 0.1f;
                    bg = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
                }

                Color hl = Color.white;
                Color sh = bg.darker().darker();

                if (size.width >= size.height) {
                    g.setColor(hl);
                    for (int i = 4; i < size.width - 6; i += 4) {
                        g.fillRect(i, size.height - 5, 2, 2);
                    }
                    g.setColor(sh);
                    for (int i = 5; i < size.width - 5; i += 4) {
                        g.fillRect(i, size.height - 6, 2, 2);
                    }
                }
                else {
                    g.setColor(hl);
                    for (int i = 4; i < size.height - 6; i += 4) {
                        g.fillRect(size.width - 5, i, 2, 2);
                    }
                    g.setColor(sh);
                    for (int i = 5; i < size.height - 5; i += 4) {
                        g.fillRect(size.width - 6, i, 2, 2);
                    }
                }
            }
            else {
                if (bvar) {
                    Graphics2D g2 = (Graphics2D) g;
                    Paint savePaint = g2.getPaint();

                    boolean isSelected = tb.isActive();
                    int w = getWidth();

                    if (isSelected) {
                        GradientPaint gradient = new GradientPaint(0, 0, selectedTitleColor, (int) (w * .75), 0, selectedTitleGradientColor);
                        g2.setPaint(gradient);
                    }
                    else {
                        GradientPaint gradient = new GradientPaint(0, 0, notSelectedTitleColor, (int) (w * .75), 0, notSelectedTitleGradientColor);
                        g2.setPaint(gradient);
                    }
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setPaint(savePaint);
                }
                else {
                    boolean isActive = tb.isActive();

                    if (isActive) {
                        g.setColor(selectedTitleColor);
                    }
                    else {
                        g.setColor(notSelectedTitleColor);
                    }
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        }
    }
}
