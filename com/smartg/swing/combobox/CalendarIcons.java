package com.smartg.swing.combobox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class CalendarIcons {

    public static class LeftArrow2 implements Icon {
	Color color;

	public LeftArrow2() {
	    this(Color.black);
	}

	public LeftArrow2(Color color) {
	    this.color = color;
	}

	public int getIconHeight() {
	    return 16;
	}

	public int getIconWidth() {
	    return 16;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(color);
	    g.drawLine(8, 6, 8 - 3, 9);
	    g.drawLine(8, 7, 8 - 2, 9);

	    g.drawLine(8 - 3, 9, 8, 12);
	    g.drawLine(8 - 2, 9, 8, 11);

	    g.drawLine(12, 6, 12 - 3, 9);
	    g.drawLine(12, 7, 12 - 2, 9);

	    g.drawLine(12 - 3, 9, 12, 12);
	    g.drawLine(12 - 2, 9, 12, 11);
	}
    }

    public static class LeftArrow implements Icon {
	Color color;

	public LeftArrow() {
	    this(Color.BLACK);
	}

	public LeftArrow(Color color) {
	    this.color = color;
	}

	public int getIconHeight() {
	    return 16;
	}

	public int getIconWidth() {
	    return 16;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(color);
	    g.drawLine(8, 6, 8 - 3, 9);
	    g.drawLine(8, 7, 8 - 2, 9);

	    g.drawLine(8 - 3, 9, 8, 12);
	    g.drawLine(8 - 2, 9, 8, 11);

	    g.drawLine(12, 6, 12 - 3, 9);
	    g.drawLine(12, 7, 12 - 2, 9);

	    g.drawLine(12 - 3, 9, 12, 12);
	    g.drawLine(12 - 2, 9, 12, 11);
	}
    }

    public static class LeftArrow3 implements Icon {
	Color color;

	public LeftArrow3() {
	    this(Color.BLACK);
	}

	public LeftArrow3(Color color) {
	    this.color = color;
	}

	public int getIconHeight() {
	    return 16;
	}

	public int getIconWidth() {
	    return 16;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(color);

	    g.drawLine(4, 6, 4 - 3, 9);
	    g.drawLine(4, 7, 4 - 2, 9);

	    g.drawLine(4 - 3, 9, 4, 12);
	    g.drawLine(4 - 2, 9, 4, 11);

	    g.drawLine(8, 6, 8 - 3, 9);
	    g.drawLine(8, 7, 8 - 2, 9);

	    g.drawLine(8 - 3, 9, 8, 12);
	    g.drawLine(8 - 2, 9, 8, 11);

	    g.drawLine(12, 6, 12 - 3, 9);
	    g.drawLine(12, 7, 12 - 2, 9);

	    g.drawLine(12 - 3, 9, 12, 12);
	    g.drawLine(12 - 2, 9, 12, 11);
	}
    }

    public static class RightArrow2 implements Icon {
	Color color;

	public RightArrow2() {
	    this(Color.BLACK);
	}

	public RightArrow2(Color color) {
	    this.color = color;
	}

	public int getIconHeight() {
	    return 16;
	}

	public int getIconWidth() {
	    return 16;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(color);
	    g.drawLine(8 - 2, 6, 8 + 1, 9);
	    g.drawLine(8 - 2, 7, 8, 9);

	    g.drawLine(8 + 1, 9, 8 - 2, 12);
	    g.drawLine(8, 9, 8 - 2, 11);

	    g.drawLine(12 - 2, 6, 12 + 1, 9);
	    g.drawLine(12 - 2, 7, 12, 9);

	    g.drawLine(12 + 1, 9, 12 - 2, 12);
	    g.drawLine(12, 9, 12 - 2, 11);
	}
    }

    public static class RightArrow3 implements Icon {

	Color color;

	public RightArrow3() {
	    this(Color.BLACK);
	}

	public RightArrow3(Color color) {
	    this.color = color;
	}

	public int getIconHeight() {
	    return 16;
	}

	public int getIconWidth() {
	    return 16;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(color);
	    g.drawLine(4 - 2, 6, 4 + 1, 9);
	    g.drawLine(4 - 2, 7, 4, 9);

	    g.drawLine(4 + 1, 9, 4 - 2, 12);
	    g.drawLine(4, 9, 4 - 2, 11);

	    g.drawLine(8 - 2, 6, 8 + 1, 9);
	    g.drawLine(8 - 2, 7, 8, 9);

	    g.drawLine(8 + 1, 9, 8 - 2, 12);
	    g.drawLine(8, 9, 8 - 2, 11);

	    g.drawLine(12 - 2, 6, 12 + 1, 9);
	    g.drawLine(12 - 2, 7, 12, 9);

	    g.drawLine(12 + 1, 9, 12 - 2, 12);
	    g.drawLine(12, 9, 12 - 2, 11);
	}
    }

    public static class RightTriangle implements Icon {
	Color color;

	int height = 12;
	int width = 12;

	public RightTriangle(Color color) {
	    this.color = color;
	}

	public int getIconHeight() {
	    return height;
	}

	public int getIconWidth() {
	    return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(color);

	    int y0 = y;
	    int y1 = y0 + height;
	    int x0 = x + 4;

	    int len = y1 - y0;
	    while (len > 0) {
		g.drawLine(x0, y0++, x0++, y1--);
		len = y1 - y0;
	    }
	}
    }
    
    public static class DownTriangle implements Icon {
	Color color;
	int height = 12;
	int width = 12;

	public DownTriangle(Color color) {
	    this.color = color;
	}

	public int getIconHeight() {
	    return height;
	}

	public int getIconWidth() {
	    return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(color);

	    int y0 = y + 4;
	    int x0 = x;
	    int x1 = x + width;

	    int len = x1 - x0;
	    while (len > 0) {
		g.drawLine(x0++, y0, x1--, y0++);
		len = x1 - x0;
	    }
	}
    }
}
