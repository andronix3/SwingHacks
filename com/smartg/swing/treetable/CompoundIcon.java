package com.smartg.swing.treetable;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class CompoundIcon implements Icon {

    private Icon firstIcon;
    private Icon secondIcon;
    private int gap;

    public CompoundIcon() {
    }

    public CompoundIcon(int gap) {
	this(null, null, gap);
    }

    public CompoundIcon(Icon firstIcon) {
	this(firstIcon, null, 0);
    }

    public CompoundIcon(Icon firstIcon, int gap) {
	this(firstIcon, null, gap);
    }

    public CompoundIcon(Icon firstIcon, Icon secondIcon) {
	this(firstIcon, secondIcon, 0);
    }

    public CompoundIcon(Icon firstIcon, Icon secondIcon, int gap) {
	this.firstIcon = firstIcon;
	this.secondIcon = secondIcon;
	this.gap = gap;
    }

    public Icon getFirstIcon() {
	return firstIcon;
    }

    public void setFirstIcon(Icon firstIcon) {
	this.firstIcon = firstIcon;
    }

    public Icon getSecondIcon() {
	return secondIcon;
    }

    public void setSecondIcon(Icon secondIcon) {
	this.secondIcon = secondIcon;
    }

    public int getGap() {
	return gap;
    }

    public void setGap(int gap) {
	this.gap = gap;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
	if (firstIcon != null) {
	    firstIcon.paintIcon(c, g, x, y);
	    x += firstIcon.getIconWidth() + gap;
	}
	if (secondIcon != null) {
	    secondIcon.paintIcon(c, g, x, y);
	}
    }

    @Override
    public int getIconWidth() {
	int w = gap;
	if (firstIcon != null) {
	    w += firstIcon.getIconWidth();
	}
	if (secondIcon != null) {
	    w += secondIcon.getIconWidth();
	}
	return w;
    }

    @Override
    public int getIconHeight() {
	int h = 0;
	if (firstIcon != null) {
	    h = firstIcon.getIconHeight();
	}
	if (secondIcon != null) {
	    h = Math.max(h, secondIcon.getIconHeight());
	}
	return h;
    }
}
