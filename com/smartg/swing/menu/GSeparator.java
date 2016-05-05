package com.smartg.swing.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;


/**
 * Add ability to expand/collapse menu groups
 * 
 * @author andrey
 * 
 */
public class GSeparator extends JSeparator {

    private SeparatorMouseHandler mouseHandler = new SeparatorMouseHandler();

    private final class SeparatorMouseHandler extends MouseAdapter {
	@Override
	public void mousePressed(MouseEvent e) {
	    if (group.isVisible()) {
		group.hideGroup();
	    } else {
		group.showGroup();
	    }
	    if (parent instanceof JMenu) {
		((JMenu) parent).getPopupMenu().pack();
	    }
	    else {
		parent.revalidate();
	    }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	    setBackground(selectionBackground);
	}

	@Override
	public void mouseExited(MouseEvent e) {
	    setBackground(null);
	}
    }

    JComponent parent;
    JGroup group;

    boolean interactive;

    public void setInteractive(boolean b) {
	if (b != interactive) {
	    interactive = b;
	    if (interactive) {
		addMouseListener(mouseHandler);
	    } else {
		removeMouseListener(mouseHandler);
		setBackground(null);
	    }
	}
    }

    private static final long serialVersionUID = 5035861429885310469L;
    Color selectionBackground = UIManager.getColor("MenuItem.selectionBackground");

    public GSeparator(JComponent parent, JGroup g, boolean interactive) {
	this.parent = parent;
	this.group = g;
	setOpaque(true);

	this.interactive = interactive;
	if (interactive) {
	    addMouseListener(mouseHandler);
	}
    }

    @Override
    public Dimension getPreferredSize() {
	if (interactive) {
	    Dimension d = super.getPreferredSize();
	    d.width = Math.max(group.getPreferredWidth(), parent.getWidth());
	    d.height = 5;
	    return d;
	}
	return super.getPreferredSize();
    }

    public boolean isOpaque() {
	return true;
    }
}
