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
package com.smartg.swing.splitpane;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * @author Andrei Kouznetsov Date: 04.05.2004 Time: 09:43:42
 */
public class DragHandler extends MouseInputAdapter {

    protected int x0, y0, x, y;
    protected Component master, slave;
    protected Rectangle rw = new Rectangle();
    protected boolean drag;
    protected boolean ignoreBorder;
    private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();

    /**
     * easy dragging of components. note that control and slave can be the same
     * component.
     *
     * @param master
     *            control Component (like titleBar)
     * @param slave
     *            Container to move
     * @param ignoreBorder
     */
    public DragHandler(Component master, Component slave, boolean ignoreBorder) {
	this.master = master;
	this.slave = slave;
	this.ignoreBorder = ignoreBorder;
	master.addMouseListener(this);
	master.addMouseMotionListener(this);
    }

    public void mousePressed(MouseEvent e) {
	x = e.getX();
	y = e.getY();

	if (ignoreBorder) {
	    Container c = (Container) e.getComponent();
	    Insets i = c.getInsets();
	    Dimension d = c.getSize();

	    if ((x > i.left) && (x < d.width - i.right) && (y > i.top) && (y < d.height - i.bottom)) {

		drag = true;
	    } else {
		return;
	    }
	}

	Point p = new Point(x, y);
	SwingUtilities.convertPointToScreen(p, e.getComponent());
	x = p.x;
	y = p.y;

	x0 = slave.getX();
	y0 = slave.getY();

	final Container parent = slave.getParent();
	if (parent instanceof JDesktopPane && slave instanceof JComponent) {
	    JDesktopPane jdp = (JDesktopPane) parent;
	    jdp.getDesktopManager().beginDraggingFrame((JComponent) slave);
	}
	Point pt = computePoint(e);
	fireActionEvent(new ActionEvent(pt, ActionEvent.ACTION_PERFORMED, "mousePressed"));
    }

    public void mouseReleased(MouseEvent e) {
	drag = false;
	final Container parent = slave.getParent();
	if (parent instanceof JDesktopPane && slave instanceof JComponent) {
	    JDesktopPane jdp = (JDesktopPane) parent;
	    jdp.getDesktopManager().endDraggingFrame((JComponent) slave);
	}
	Point pt = computePoint(e);
	fireActionEvent(new ActionEvent(pt, ActionEvent.ACTION_PERFORMED, "mouseReleased"));
    }

    public void mouseDragged(MouseEvent e) {
	if (ignoreBorder && !drag) {
	    return;
	}
	Point pt = computePoint(e);

	final Container parent = slave.getParent();
	if (parent instanceof JDesktopPane && slave instanceof JComponent) {
	    JDesktopPane jdp = (JDesktopPane) parent;
	    fireActionEvent(new ActionEvent(pt, ActionEvent.ACTION_PERFORMED, "nextLocation"));
	    jdp.getDesktopManager().setBoundsForFrame((JComponent) slave, pt.x, pt.y, slave.getWidth(), slave.getHeight());
	} else {
	    fireActionEvent(new ActionEvent(pt, ActionEvent.ACTION_PERFORMED, "nextLocation"));
	    slave.setLocation(pt.x, pt.y);
	}
    }

    private void fireActionEvent(ActionEvent e) {
	for (ActionListener l : listeners) {
	    l.actionPerformed(e);
	}
    }

    public void addActionListener(ActionListener l) {
	if (!listeners.contains(l)) {
	    listeners.add(l);
	}
    }

    public void removeActionListener(ActionListener l) {
	listeners.remove(l);
    }

    private Point computePoint(MouseEvent e) {
	Point p = e.getPoint();
	SwingUtilities.convertPointToScreen(p, e.getComponent());
	int dx = x - p.x;
	int dy = y - p.y;
	rw = master.getBounds(rw);
	rw.x = x0 - dx;
	rw.y = y0 - dy;

	Point pt = new Point(rw.x, rw.y);

	return pt;
    }
}
