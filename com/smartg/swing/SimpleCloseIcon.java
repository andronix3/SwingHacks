package com.smartg.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.Icon;

public final class SimpleCloseIcon implements Icon {
    private final Font font = new Font("Dialog", Font.BOLD, 12);
    private Color background;
    private Color foreground;

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
	if (background != null) {
	    g.setColor(background);
	    g.fillRect(x, y, getIconWidth(), getIconHeight());
	}

	if (foreground != null) {
	    g.setColor(foreground);
	} else {
	    g.setColor(Color.BLACK);
	}
	Font fnt = g.getFont();
	g.setFont(font);
	String s = "x";
	int sw = g.getFontMetrics().stringWidth(s);
	int sh = g.getFontMetrics().getHeight();
	g.drawString(s, x + (getIconWidth() - sw) / 2, y + (getIconHeight() + sh / 2) / 2);
	g.setFont(fnt);
    }

    @Override
    public int getIconWidth() {
	return 15;
    }

    @Override
    public int getIconHeight() {
	return 15;
    }

    public Color getBackground() {
	return background;
    }

    public void setBackground(Color background) {
	this.background = background;
    }

    public Color getForeground() {
	return foreground;
    }

    public void setForeground(Color foreground) {
	this.foreground = foreground;
    }

}