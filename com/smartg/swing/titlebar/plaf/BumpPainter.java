package com.smartg.swing.titlebar.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * @author Andrey Kuznetsov
 */
public interface BumpPainter {
    void paint(Graphics2D g, Dimension size, Color backColor, Color topColor, Color shadowColor);
}
