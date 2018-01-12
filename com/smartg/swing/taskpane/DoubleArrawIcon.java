package com.smartg.swing.taskpane;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.SwingConstants;

public class DoubleArrawIcon implements Icon {

    public static enum Direction {
	UP, DOWN, LEFT, RIGHT;

	public Direction flip() {
	    switch (this) {
	    case DOWN:
		return UP;
	    case LEFT:
		return RIGHT;
	    case RIGHT:
		return LEFT;
	    case UP:
		return DOWN;
	    default:
		return null;
	    }
	}
    }

    protected Direction direction;
    protected int iconWidth = 19;
    protected int iconHeight = 19;
    protected Color background = new Color(200, 217, 247);
    protected Color cirleColor = Color.white;
    protected Color shadowColor = new Color(160, 160, 200);
    protected Color foreground = new Color(0, 60, 165);

    int orientation;

    boolean opaque;
    boolean contentOpaque = true;

    public DoubleArrawIcon(Direction d) {
	this.direction = d;
    }

    protected void drawRightArrow(Graphics g) {
	g.drawLine(8 - 2, 6, 8 + 1, 9);
	g.drawLine(8 - 2, 7, 8, 9);

	g.drawLine(8 + 1, 9, 8 - 2, 12);
	g.drawLine(8, 9, 8 - 2, 11);

	g.drawLine(12 - 2, 6, 12 + 1, 9);
	g.drawLine(12 - 2, 7, 12, 9);

	g.drawLine(12 + 1, 9, 12 - 2, 12);
	g.drawLine(12, 9, 12 - 2, 11);
    }

    protected void drawLeftArrow(Graphics g) {
	g.drawLine(8, 6, 8 - 3, 9);
	g.drawLine(8, 7, 8 - 2, 9);

	g.drawLine(8 - 3, 9, 8, 12);
	g.drawLine(8 - 2, 9, 8, 11);

	g.drawLine(12, 6, 12 - 3, 9);
	g.drawLine(12, 7, 12 - 2, 9);

	g.drawLine(12 - 3, 9, 12, 12);
	g.drawLine(12 - 2, 9, 12, 11);
    }

    protected void drawDownArrow(Graphics g) {
	g.drawLine(6, 8 - 2, 9, 8 + 1);
	g.drawLine(7, 8 - 2, 9, 8);

	g.drawLine(9, 8 + 1, 12, 8 - 2);
	g.drawLine(9, 8, 11, 8 - 2);

	g.drawLine(6, 12 - 2, 9, 12 + 1);
	g.drawLine(7, 12 - 2, 9, 12);

	g.drawLine(9, 12 + 1, 12, 12 - 2);
	g.drawLine(9, 12, 11, 12 - 2);
    }

    protected void drawUpArrow(Graphics g) {
	g.drawLine(6, 8, 9, 8 - 3);
	g.drawLine(7, 8, 9, 8 - 2);

	g.drawLine(9, 8 - 3, 12, 8);
	g.drawLine(9, 8 - 2, 11, 8);

	g.drawLine(6, 12, 9, 12 - 3);
	g.drawLine(7, 12, 9, 12 - 2);

	g.drawLine(9, 12 - 3, 12, 12);
	g.drawLine(9, 12 - 2, 11, 12);
    }

    @Override
	public int getIconWidth() {
	return iconWidth;
    }

    @Override
	public int getIconHeight() {
	return iconHeight;
    }

    public Color getBackground() {
	return background;
    }

    public void setBackground(Color background) {
	this.background = background;
    }

    public Color getCirleColor() {
	return cirleColor;
    }

    public void setCirleColor(Color cirleColor) {
	this.cirleColor = cirleColor;
    }

    public Color getShadowColor() {
	return shadowColor;
    }

    public void setShadowColor(Color shadowColor) {
	this.shadowColor = shadowColor;
    }

    public Color getForeground() {
	return foreground;
    }

    public void setForeground(Color foreground) {
	this.foreground = foreground;
    }

    public Direction getDirection() {
	return direction;
    }

    public void setDirection(Direction direction) {
	this.direction = direction;
    }

    @Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
	Graphics2D g2d = (Graphics2D) g;

	if (opaque) {
	    g.setColor(background);
	    g.fillRect(0, 0, iconWidth, iconHeight);
	}

	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	if (contentOpaque) {
	    g.setColor(cirleColor);
	    g.fillOval(1, 1, iconWidth - 3, iconHeight - 3);
	}

	g.setColor(shadowColor);
	g2d.setStroke(new BasicStroke(2));

	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
	g2d.drawOval(1, 1, iconWidth - 3, iconHeight - 3);

	g.setPaintMode();
	g2d.setStroke(new BasicStroke(1));
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

	g2d.setStroke(new BasicStroke(1));
	g.setColor(foreground);

	if (direction == Direction.LEFT) {
	    drawLeftArrow(g);
	} else if (direction == Direction.RIGHT) {
	    drawRightArrow(g);
	} else if (orientation == SwingConstants.HORIZONTAL) {
	    if (direction == Direction.UP) {
		drawUpArrow(g);
	    } else if (direction == Direction.DOWN) {
		drawDownArrow(g);
	    }
	} else {
	    if (direction == Direction.UP) {
		drawLeftArrow(g);
	    } else if (direction == Direction.DOWN) {
		drawRightArrow(g);
	    }
	}
    }

    public void flipOrientation() {
	orientation = Math.abs(orientation - 1);
    }

    public int getOrientation() {
	return orientation;
    }

    public void setOrientation(int orientation) {
	this.orientation = orientation;
    }

    public boolean isOpaque() {
	return opaque;
    }

    public void setOpaque(boolean opaque) {
	this.opaque = opaque;
    }

    public boolean isContentOpaque() {
	return contentOpaque;
    }

    public void setContentOpaque(boolean contentOpaque) {
	this.contentOpaque = contentOpaque;
    }
}