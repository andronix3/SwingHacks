package com.smartg.swing.titlebar.plaf.motif;

import java.awt.Color;
import java.awt.Graphics;

/**
 * MotifPressedSystemIcon.
 * Pressed system icons for MotifLookAndFeel.
 * @author Andrey Kuznetsov
 */
public class MotifPressedSystemIcon extends MotifSystemIcon {
    public MotifPressedSystemIcon(Color baseColor) {
        super(baseColor);
    }

    protected void paint(Graphics g, Color highlight, Color shadow, int x, int y) {
        g.setColor(shadow);
        g.drawLine(x + 5, y + 8, x + 5, y + 11);
        g.drawLine(x + 5, y + 8, x + getIconWidth() - 6, y + 8);
        g.drawLine(x + getIconWidth() - 2, y, x + getIconWidth() - 2, y + getIconHeight());
        g.setColor(highlight);
        g.drawLine(x + 5, y + 11, x + getIconWidth() - 6, y + 11);
        g.drawLine(x + getIconWidth() - 6, y + 8, x + getIconWidth() - 6, y + 11);
        g.drawLine(x + getIconWidth() - 1, y, x + getIconWidth() - 1, y + getIconHeight());
    }
}
