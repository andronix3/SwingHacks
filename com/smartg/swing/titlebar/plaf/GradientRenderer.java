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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * @author Andrei Kouznetsov
 * Date: 08.08.2004
 * Time: 23:49:25
 */
public class GradientRenderer implements TitleBarRenderer {

    boolean horizontal;

    private Color color;
    private Color gradient;
    private float colorPosition;
    private float gradientPosition;

    /**
     * create horizontal GradientRenderer with default colors (according to InternalFrameUI))
     */
    public GradientRenderer() {
        this(true);
    }

    /**
     * create GradientRenderer with default colors (according to InternalFrameUI))
     * @param horizontal if true then gradient is horizontal, otherwise vertical
     */
    public GradientRenderer(boolean horizontal) {
        this(UIManager.getColor("InternalFrame.activeTitleBackground"),
                UIManager.getColor("InternalFrame.activeTitleGradient"), horizontal);
    }

    /**
     * create horizontal GradientRenderer
     * @param color first color
     * @param gradient gradient color
     */
    public GradientRenderer(Color color, Color gradient) {
        this(color, gradient, true);
    }

    /**
     * create GradientRenderer
     * @param color first color
     * @param gradient gradient color
     * @param horizontal if true then gradient is horizontal, otherwise vertical
     */
    public GradientRenderer(Color color, Color gradient, boolean horizontal) {
        this(color, gradient, horizontal, 0.0f, 0.75f);
    }

    /**
     * @param color first color
     * @param gradient gradient color
     * @param horizontal if true then gradient is horizontal, otherwise vertical
     * @param colorPosition float - usually between 0.0f and 1.0f
     * @param gradientPosition float - usually between 0.0f and 1.0f
     */
    public GradientRenderer(Color color, Color gradient, boolean horizontal, float colorPosition, float gradientPosition) {
        this.horizontal = horizontal;
        this.color = color;
        this.gradient = gradient;
        if (this.gradient == null) {
            this.gradient = color.brighter().brighter();
        }
        this.colorPosition = colorPosition;
        this.gradientPosition = gradientPosition;
    }

    @Override
	public void paint(Graphics g, JComponent c) {
        paint(g, c, c.getBounds());
    }

    @Override
	public void paint(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2d = (Graphics2D) g;
        int w = c.getWidth();
        int h = c.getHeight();

        GradientPaint titleGradient;
        if (horizontal) {
            titleGradient = new GradientPaint(w * colorPosition, 0, color, w * gradientPosition, 0, gradient);
        }
        else {
            titleGradient = new GradientPaint(0, h * colorPosition, color, 0, h * gradientPosition, gradient);
        }
        g2d.setPaint(titleGradient);
        g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
    }

    public float getColorPosition() {
        return colorPosition;
    }

    /**
     * @param colorPosition float - usually between 0.0f and 1.0f
     */
    public void setColorPosition(float colorPosition) {
        this.colorPosition = colorPosition;
    }

    public float getGradientPosition() {
        return gradientPosition;
    }

    /**
     * @param gradientPosition float - usually between 0.0f and 1.0f
     */
    public void setGradientPosition(float gradientPosition) {
        this.gradientPosition = gradientPosition;
    }

    @Override
	public void setBaseColor(Color baseColor) {
        color = baseColor;
    }

    @Override
	public void setArmed(boolean armed) {
    }

    public static void main(String[] args) {
        UIManager.LookAndFeelInfo[] lafinfo = UIManager.getInstalledLookAndFeels();

        for (LookAndFeelInfo lookAndFeelInfo : lafinfo) {
            if (lookAndFeelInfo.getName().toLowerCase().indexOf("motif") != -1) {
                try {
                    final String className = lookAndFeelInfo.getClassName();
                    UIManager.setLookAndFeel(className);
//                    System.out.println(className);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }

        final GradientRenderer renderer = new GradientRenderer(false);
        final JPanel panel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -1446585477125699892L;

			@Override
			protected void paintComponent(Graphics g) {
                renderer.paint(g, this);
            }
        };

        JFrame frame = new JFrame();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
