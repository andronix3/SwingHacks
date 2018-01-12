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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * @author Andrei Kouznetsov
 * Date: 22.01.2004
 * Time: 19:14:31
 */
public class BumpRenderer implements TitleBarRenderer {

    static final float topB = 1.0f;
    static final float backB = 0.8f;
    static final float shadowB = 0.4f;

    Color topColor2 = MetalLookAndFeel.getPrimaryControlHighlight();
    Color shadowColor2 = MetalLookAndFeel.getPrimaryControlDarkShadow();
    Color backColor2 = MetalLookAndFeel.getPrimaryControl();

    Color topColor = MetalLookAndFeel.getControlHighlight();
    Color shadowColor = MetalLookAndFeel.getControlDarkShadow();
    Color backColor = MetalLookAndFeel.getControl();

    float alpha = 1.0f;

    BumpPainter painter = new BumbPainter1();

    public BumpRenderer() {

    }

    public BumpRenderer(Color c) {
        this(getH(c), getS(c));
    }

    public BumpRenderer(Color topColor, Color shadowColor, Color backColor) {
        this.topColor = topColor;
        this.backColor = backColor;
        this.shadowColor = shadowColor;

        this.topColor2 = topColor;
        this.backColor2 = backColor;
        this.shadowColor2 = shadowColor;
    }

    public BumpRenderer(float baseH, float baseS) {
        setBaseColor(baseH, baseS);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        bi = null;
        abi = null;
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

        bi = null;
        abi = null;
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

    public Color getBackColor() {
        return armed ? this.backColor2 : this.backColor;
    }

    BufferedImage bi;
    BufferedImage abi;

    @Override
	public void paint(Graphics g, JComponent c, Rectangle r) {
        Dimension size = r.getSize();

        BufferedImage bi = getImage();

        for (int i = 0; i < size.height; i += bi.getHeight()) {
            for (int j = 0; j < size.width; j += bi.getWidth()) {
                g.drawImage(bi, j, i, null);
            }
        }
    }

    private BufferedImage getImage() {
        if (armed) {
            if (abi == null) {
                abi = create();
            }
            return abi;
        }
        else {
            if (bi == null) {
                bi = create();
            }
            return bi;
        }
    }

    private BufferedImage create() {
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        paintBumps(g2d, new Dimension(20, 20));
        return bi;
    }

    private void paintBumps(Graphics g, Dimension size) {
        Color topColor = armed ? this.topColor2: this.topColor;
        Color backColor = armed ? this.backColor2 : this.backColor;
        Color shadowColor = armed ? this.shadowColor2 : this.shadowColor2;

        painter.paint((Graphics2D) g, size, backColor, topColor, shadowColor);
    }

    public BumpPainter getPainter() {
        return painter;
    }

    public void setPainter(BumpPainter painter) {
        this.painter = painter;
    }

    static class BumbPainter1 implements BumpPainter {
        @Override
		public void paint(Graphics2D g, Dimension size, Color backColor, Color topColor, Color shadowColor) {
            g.setColor(backColor);
            g.fillRect(0, 0, size.width, size.height);

            g.setColor(topColor);
            for (int x = 0; x < size.width; x += 4) {
                for (int y = 0; y < size.height; y += 4) {
                    g.drawLine(x, y, x, y);
                    g.drawLine(x + 2, y + 2, x + 2, y + 2);
                }
            }
            g.setColor(shadowColor);
            for (int x = 0; x < size.width; x += 4) {
                for (int y = 0; y < size.height; y += 4) {
                    g.drawLine(x + 1, y + 1, x + 1, y + 1);
                    g.drawLine(x + 3, y + 3, x + 3, y + 3);
                }
            }
        }
    }

    static class BumbPainter2 implements BumpPainter {
        @Override
		public void paint(Graphics2D g, Dimension size, Color backColor, Color topColor, Color shadowColor) {
            g.setColor(backColor);
            g.fillRect(0, 0, size.width, size.height);

            g.setColor(topColor);
            for (int x = 0; x < size.width; x += 4) {
                for (int y = 0; y < size.height; y += 4) {
                    g.fillRect(x + 1, y + 1, 1, 1);
                }
            }
            g.setColor(shadowColor);
            for (int x = 0; x < size.width; x += 4) {
                for (int y = 0; y < size.height; y += 4) {
                    g.fillRect(x + 2, y + 2, 1, 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        final BumpRenderer renderer = new BumpRenderer(Color.gray);
        renderer.setPainter(new BumbPainter1());
//        renderer.setAlpha(0.5f);
        final JPanel panel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
                super.paintComponent(g);
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
