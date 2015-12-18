/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
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

package com.smartg.swing.scroll;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * GScrollPane - is an enhanced JScrollPane with (possibly) modified scroll
 * behavior (useful for tables) and possibility to add additional buttons below
 * vertical scroll bar.
 */
public class GScrollPane extends JScrollPane {
    private static final long serialVersionUID = 142532106130415181L;

    private Container vpanel;
    private JButton currentAction;

    private MouseListener mouseHandler = new MouseHandler();

    private int delay = 100;
    private Timer t;

    private JMenu navigationMenu;

    private boolean continuousScroll = true;

    private GScrollBarToolTipTextProvider toolTipTextprovider = new DefaultGScrollBarToolTipTextProvider();

    private boolean runAction() {
	if (currentAction != null) {
	    Action a = currentAction.getAction();
	    ((ProxyAction) a).getAction().actionPerformed(new ActionEvent(currentAction, ActionEvent.ACTION_PERFORMED, currentAction.getActionCommand()));
	    return true;
	}
	return false;
    }

    boolean isAutoRepeat(JButton b) {
	RProxyAction action = (RProxyAction) b.getAction();
	return action.isAutoRepeat();
    }

    private class MouseHandler extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
	    JButton b = (JButton) e.getComponent();
	    if (isAutoRepeat(b)) {
		currentAction = b;
		runAction();
		t.stop();
		t.start();
	    }
	}

	public void mouseReleased(MouseEvent e) {
	    currentAction = null;
	    if (t.isRunning()) {
		t.stop();
	    }
	}
    }

    public GScrollPane() {
	init();
    }

    public GScrollPane(int vsbPolicy, int hsbPolicy) {
	super(vsbPolicy, hsbPolicy);
	init();
    }

    public GScrollPane(Component view) {
	super(view);
	init();
    }

    public GScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
	super(view, vsbPolicy, hsbPolicy);
	init();
    }

    public JScrollBar createHorizontalScrollBar() {
	return new GScrollBar2(Adjustable.HORIZONTAL);
    }

    public JScrollBar createVerticalScrollBar() {
	return new GScrollBar2(Adjustable.VERTICAL);
    }

    public boolean isContinuousScroll() {
	return continuousScroll;
    }

    public void setContinuousScroll(boolean b) {
	this.continuousScroll = b;
	JScrollBar vsb = getVerticalScrollBar();
	JScrollBar hsb = getHorizontalScrollBar();
	if (vsb instanceof GScrollBar) {
	    ((GScrollBar) vsb).setContinuousScroll(b);
	} else if (vsb instanceof GScrollBar2) {
	    ((GScrollBar2) vsb).setContinuousScroll(b);
	}
	if (hsb instanceof GScrollBar) {
	    ((GScrollBar) hsb).setContinuousScroll(b);
	} else if (hsb instanceof GScrollBar2) {
	    ((GScrollBar2) hsb).setContinuousScroll(b);
	}
    }

    protected void init() {
	if (vpanel == null) {
	    t = new Timer(delay, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!runAction()) {
			t.stop();
		    }
		}
	    });
	    t.setInitialDelay(500);
	    setLayout(new GScrollPaneLayout());
	    vpanel = new Box(BoxLayout.Y_AXIS);
	    add(GScrollPaneLayout.VPANEL, vpanel);
	}
    }

    public JButton addCustomAction(Action a) {
	return addCustomAction(a, false);
    }

    public JButton addCustomAction(Action a, boolean autoRepeat) {
	ProxyAction action = new RProxyAction(a, autoRepeat);
	final JButton b = new JButton(action);
	b.setFocusable(false);
	b.setBorder(new SWBorder2(b.getBorder()));
	b.addMouseListener(mouseHandler);
	vpanel.add(b);
	return b;
    }

    private Action getAction(Component c) {
	if (!(c instanceof JButton)) {
	    return null;
	}
	JButton b = (JButton) c;
	Action a = b.getAction();
	if (a == null) {
	    return null;
	}
	if (a instanceof RProxyAction) {
	    return ((RProxyAction) a).getAction();
	}
	return a;
    }

    public void removeCustomAction(Action a) {
	int count = vpanel.getComponentCount();
	for (int i = 0; i < count; i++) {
	    Component c = vpanel.getComponent(i);
	    Action action = getAction(c);
	    if (action != null && action == a) {
		c.removeMouseListener(mouseHandler);
		if (currentAction == c) {
		    currentAction = null;
		}
		remove(c);
		break;
	    }
	}
    }

    public JMenu getNavigationMenu() {
	return navigationMenu;
    }

    public void setNavigationMenu(JMenu navigationMenu) {
	this.navigationMenu = navigationMenu;
    }

    public GScrollBarToolTipTextProvider getToolTipTextProvider() {
	return toolTipTextprovider;
    }

    public void setToolTipTextProvider(GScrollBarToolTipTextProvider toolTipTextProvider) {
	this.toolTipTextprovider = toolTipTextProvider;
    }

    protected class GScrollBar2 extends ScrollBar {

	private static final long serialVersionUID = 1L;

	private boolean continuousScroll = true;

	private GBoundedRangeModel rangeModel;

	private JWindow popup;

	private boolean popupShown;

	public GScrollBar2(int orientation) {
	    super(orientation);
	    setModel(new GBoundedRangeModel());
	    rangeModel.addAdjustingChangeListener(new GScrollBar2.ChangeHandler());
	    rangeModel.addChangeListener(new GScrollBar2.AdjListener());
	}

	public boolean isContinuousScroll() {
	    return continuousScroll;
	}

	public void setContinuousScroll(boolean b) {
	    this.continuousScroll = b;
	    rangeModel.setFireChangesWhileAdjusting(continuousScroll);
	}

	public void setModel(BoundedRangeModel newModel) {
	    if (newModel instanceof GBoundedRangeModel) {
		super.setModel(newModel);
		rangeModel = (GBoundedRangeModel) newModel;
	    } else {
		throw new IllegalArgumentException();
	    }
	}

	private class ChangeHandler implements AdjustingChangeListener {
	    JLabel contents;

	    public ChangeHandler() {
		popup = new JWindow();
		contents = new JLabel();
		contents.setFont(contents.getFont().deriveFont(15f));
		popup.getContentPane().add(contents);
		popup.setFocusableWindowState(false);
	    }

	    public void stateChanged(AdjustingChangeEvent e) {
		if (toolTipTextprovider != null && rangeModel.getValueIsAdjusting() && !rangeModel.isFireChangesWhileAdjusting()) {
		    contents.setText(toolTipTextprovider.getTooTipText(GScrollBar2.this));
		    Point p = new Point();
		    SwingUtilities.convertPointToScreen(p, GScrollBar2.this);
		    Dimension ps = contents.getPreferredSize();
		    int max = rangeModel.getMaximum();
		    int min = rangeModel.getMinimum();
		    int extent = rangeModel.getExtent();
		    int adjustingValue = rangeModel.getAdjustingValue();
		    if (GScrollBar2.this.getOrientation() == Adjustable.VERTICAL) {
			int h = GScrollBar2.this.getHeight();
			popup.setLocation(p.x - ps.width, p.y + (adjustingValue * h / (max - min + extent)));
		    } else {
			int w = GScrollBar2.this.getWidth();
			popup.setLocation(p.x + (adjustingValue * w / (max - min - extent)), p.y - ps.height);
		    }
		    popup.pack();
		    popup.setVisible(false);
		    popupShown = true;
		}
	    }
	}

	private class AdjListener implements ChangeListener {
	    public void stateChanged(ChangeEvent e) {
		if (popup != null && popupShown) {
		    popup.setVisible(false);
		}
	    }
	}
    }
}
