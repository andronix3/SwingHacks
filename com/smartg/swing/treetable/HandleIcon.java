package com.smartg.swing.treetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class HandleIcon implements Icon {

	private final Icon image;
	private int indent;

	public HandleIcon(Icon image) {
		this.image = Objects.requireNonNull(image);
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		image.paintIcon(c, g, x + indent, y);
	}

	@Override
	public int getIconWidth() {
		return image.getIconWidth() + indent;
	}

	@Override
	public int getIconHeight() {
		return image.getIconHeight();
	}

	public static Icon getCollapsedImage() {
		BufferedImage bi = new BufferedImage(11, 11, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, 11, 11);
		g.setColor(Color.DARK_GRAY);
		g.drawLine(0, 5, 10, 5);
		g.drawLine(5, 0, 5, 10);
		g.drawRect(0, 0, 10, 10);
		return new ImageIcon(bi);
	}

	public static Icon getExpandedImage() {
		BufferedImage bi = new BufferedImage(11, 11, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, 11, 11);
		g.setColor(Color.DARK_GRAY);
		g.drawLine(0, 5, 10, 5);
		g.drawRect(0, 0, 10, 10);
		return new ImageIcon(bi);
	}
}
