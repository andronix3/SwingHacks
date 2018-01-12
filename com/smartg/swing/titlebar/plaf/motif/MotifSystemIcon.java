package com.smartg.swing.titlebar.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * MotifSystemIcon.
 * Systm icons for MotifLookAndFeel.
 * @author Andrey Kuznetsov
 */
public class MotifSystemIcon implements Icon {
    Color color;

    public MotifSystemIcon(Color baseColor) {
        this.color = baseColor;
    }

    public int getIconWidth() {
        return 21;
    }

    public int getIconHeight() {
        return 21;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color highlight = color.brighter();
        Color shadow = color.darker().darker();
        paint(g, highlight, shadow, x, y);
    }

    protected void paint(Graphics g, Color highlight, Color shadow, int x, int y) {
        g.setColor(highlight);
        g.drawLine(x + 5, y + 8, x + 5, y + 11);
        g.drawLine(x + 5, y + 8, x + getIconWidth() - 6, y + 8);
        g.drawLine(x + getIconWidth() - 1, y, x + getIconWidth() - 1, y + getIconHeight());
        g.setColor(shadow);
        g.drawLine(x + 5, y + 11, x + getIconWidth() - 6, y + 11);
        g.drawLine(x + getIconWidth() - 6, y + 8, x + getIconWidth() - 6, y + 11);
        g.drawLine(x + getIconWidth() - 2, y, x + getIconWidth() - 2, y + getIconHeight());
    }
}
