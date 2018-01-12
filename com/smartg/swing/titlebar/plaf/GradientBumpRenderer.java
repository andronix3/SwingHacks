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

package com.smartg.swing.titlebar.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * @author Andrei Kouznetsov
 * Date: 22.01.2004
 * Time: 19:14:31
 */
public class GradientBumpRenderer implements TitleBarRenderer {

    static final float topB = 1.0f;
    static final float backB = 0.8f;
    static final float shadowB = 0.4f;

    Color topColor;
    Color backColor;
    Color shadowColor;

    Color topColor2;
    Color backColor2;
    Color shadowColor2;

    float alpha = 1.0f;
    int _alpha = 0xFF000000;

    public GradientBumpRenderer() {
        this(MetalLookAndFeel.getControl());
    }

    public GradientBumpRenderer(Color c) {
        this(getH(c), getS(c));
    }

    public GradientBumpRenderer(float baseH, float baseS) {
        setBaseColor(baseH, baseS);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this._alpha = ((int) (0xFF * alpha)) << 24;
    }

    static float getH(Color c) {
        float ff [] = new float[3];
        return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), ff)[0];
    }

    static float getS(Color c) {
        float ff [] = new float[3];
        return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), ff)[1];
    }

    private void setBaseColor(float baseH, float baseS) {
        this.backColor = new Color(Color.HSBtoRGB(baseH, baseS, backB));
        this.topColor = new Color(Color.HSBtoRGB(baseH, baseS, topB));
        this.shadowColor = new Color(Color.HSBtoRGB(baseH, baseS, shadowB));

        this.backColor2 = new Color(Color.HSBtoRGB(baseH, baseS, backB - 0.1f));
        this.topColor2 = new Color(Color.HSBtoRGB(baseH, baseS, topB));
        this.shadowColor2 = new Color(Color.HSBtoRGB(baseH, baseS, shadowB));
        applyAlpha();
    }

    private void applyAlpha() {
        this.backColor = new Color(this.backColor.getRGB() | 0xFF000000 & _alpha);
        this.topColor = new Color(this.topColor.getRGB() | 0xFF000000 & _alpha);
        this.shadowColor = new Color(this.shadowColor.getRGB() | 0xFF000000 & _alpha);

        this.backColor2 = new Color(this.backColor2.getRGB() | 0xFF000000 & _alpha);
        this.topColor2 = new Color(this.topColor2.getRGB() | 0xFF000000 & _alpha);
        this.shadowColor2 = new Color(this.shadowColor2.getRGB() | 0xFF000000 & _alpha);
    }

    @Override
	public void setBaseColor(Color c) {
        setBaseColor(getH(c), getS(c));
    }

    private boolean armed;

    @Override
	public void paint(Graphics g, JComponent c) {
        paint(g, c, c.getBounds());
    }

    @Override
	public void paint(Graphics g, JComponent c, Rectangle r) {
        Dimension size = r.getSize();
        Graphics2D g2d = (Graphics2D) g;

        Color topColor = this.topColor;
        Color backColor = armed ? this.backColor2 : this.backColor;
        Color shadowColor = armed ? this.shadowColor2 : this.shadowColor;

        GradientPaint gpback = new GradientPaint(0, 0, backColor, size.width * 2, size.height * 2, backColor.darker());
        g2d.setPaint(gpback);
        g.fillRect(0, 0, size.width, size.height);

        g2d.setColor(topColor);
        g.drawLine(1, 1, 1, size.height - 1);
        g.drawLine(1, 1, size.width - 1, 1);

        g2d.setColor(shadowColor);
        g.drawLine(size.width - 1, 1, size.width - 1, size.height - 1);
        g.drawLine(1, size.height - 1, size.width - 1, size.height - 1);

        g2d.setPaint(topColor);
        for (int x = 4; x < size.width - 4; x += 4) {
            for (int y = 4; y < size.height - 4; y += 4) {
                g.drawLine(x, y, x, y);
                g.drawLine(x + 2, y + 2, x + 2, y + 2);
            }
        }

        g2d.setPaint(shadowColor);
        for (int x = 4; x < size.width - 4; x += 4) {
            for (int y = 4; y < size.height - 4; y += 4) {
                g.drawLine(x + 1, y + 1, x + 1, y + 1);
                g.drawLine(x + 3, y + 3, x + 3, y + 3);
            }
        }
    }

    public static void main(String[] args) {
        final GradientBumpRenderer renderer = new GradientBumpRenderer(MetalLookAndFeel.getPrimaryControl());
        renderer.setAlpha(0.5f);
        final JPanel panel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 366442608166078722L;

			@Override
			protected void paintComponent(Graphics g) {
                renderer.paint(g, this);
            }
        };
        panel.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e) {
                renderer.armed = !renderer.armed;
                panel.repaint();
            }
        });

        JFrame frame = new JFrame();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public boolean isArmed() {
        return armed;
    }

    @Override
	public void setArmed(boolean armed) {
        this.armed = armed;
    }
}
