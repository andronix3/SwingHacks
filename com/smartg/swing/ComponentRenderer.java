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
package com.smartg.swing;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.MouseInputListener;

/**
 * ComponentRenderer can rotate (90, 180, 270) any JComponent (except those
 * which are using CellRenderer to paint itself)
 * 
 * @author Andrey Kuznetsov
 */
public class ComponentRenderer extends JComponent {

    private static final long serialVersionUID = -8277583826481874278L;
    int axis;
    CellRendererPane crp;
    boolean clockwise;
    JComponent component;
    private MouseHandler mouseHandler;
    boolean borderPainted;

    boolean ignoreMouse;
    boolean contentPainted = true;

    float alpha = 1.0f;
    boolean useAlpha;

    protected ComponentRenderer() {
    }

    public ComponentRenderer(JComponent c) {
	this(c, null);
    }

    public ComponentRenderer(JComponent c, CellRendererPane crp) {
	component = c;
	if (crp == null) {
	    this.crp = createRendererPane();
	} else {
	    setCellRendererPane(crp);
	}
	setRenderer(c);

	setInputMap(WHEN_IN_FOCUSED_WINDOW, component.getInputMap(WHEN_IN_FOCUSED_WINDOW));
	ActionMap actionMap = this.component.getActionMap();
	setActionMap(actionMap);

	addMouseListener(getMouseHandler());
	addMouseMotionListener(getMouseHandler());
    }

    protected void setRenderer(JComponent c) {
	if (c != null) {
	    if (c.getParent() != crp) {
		crp.add(c);
	    }
	    c.setFocusable(false);
	}
    }

    protected void setCellRendererPane(CellRendererPane crp) {
	this.crp = crp;
	if (this.crp.getParent() == null) {
	    add(this.crp);
	}
    }

    /**
     * overridden to redirect actions to component.
     */
    @Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
	InputMap map = getInputMap(condition);
	ActionMap am = getActionMap();

	if (map != null && am != null && isEnabled()) {
	    Object binding = map.get(ks);
	    Action action = (binding == null) ? null : am.get(binding);
	    if (action != null) {
		return SwingUtilities.notifyAction(action, ks, e, getComponent(), e.getModifiers());
	    }
	}
	return false;
    }

    public JComponent getComponent() {
	return component;
    }

    protected void setComponent(JComponent component) {
	this.component = component;
    }

    @Override
	public String getToolTipText() {
	return getComponent().getToolTipText();
    }

    public void showToolTip(boolean show) {
	if (show) {
	    registerComponent();
	} else {
	    unregisterComponent();
	}
    }

    protected void registerComponent() {
	// ensure that InputMap and ActionMap are created
	InputMap imap = component.getInputMap();
	// ActionMap amap = component.getActionMap();
	boolean removeKeyStroke = false;
	KeyStroke[] ks = imap.keys();
	if (ks == null || ks.length == 0) {
	    imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0), "backSlash");
	    removeKeyStroke = true;
	}
	ToolTipManager.sharedInstance().registerComponent(component);
	if (removeKeyStroke) {
	    imap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0));
	}
	component.removeMouseListener(tipMouseHandler);
	component.addMouseListener(tipMouseHandler);
    }

    protected void unregisterComponent() {
	ToolTipManager.sharedInstance().unregisterComponent(component);
	component.removeMouseListener(tipMouseHandler);
    }

    TipMouseHandler tipMouseHandler = new TipMouseHandler();

    class TipMouseHandler extends MouseAdapter {
	@Override
	public void mouseEntered(MouseEvent e) {
	    Action action = component.getActionMap().get("postTip");
	    if (action != null) {
		action.actionPerformed(new ActionEvent(ComponentRenderer.this, ActionEvent.ACTION_PERFORMED, "postTip"));
	    }
	}
    }

    protected CellRendererPane createRendererPane() {
	return new CellRendererPane();
    }

    protected void dispatchToRenderer(JComponent c, AWTEvent e) {
	Rectangle r = c.getBounds();
	Insets insets = getInsets();
	c.setBounds(insets.left, insets.top, getWidth() - (insets.left + insets.right), getHeight() - (insets.top + insets.bottom));
	e.setSource(c);
	c.dispatchEvent(e);
	c.setBounds(r);
	repaint();
    }

    @Override
	public Dimension getPreferredSize() {
	Dimension d = component.getPreferredSize();
	Insets insets = getInsets();
	if (axis == RainLayout.Y_AXIS) {
	    int w = d.width;
	    d.width = d.height;
	    d.height = w;
	}
	d.width += insets.left + insets.right;
	d.height += insets.top + insets.bottom;
	return d;
    }

    @Override
	public Dimension getMinimumSize() {
	Dimension d = component.getMinimumSize();
	Insets insets = getInsets();
	if (axis == RainLayout.Y_AXIS) {
	    int w = d.width;
	    d.width = d.height;
	    d.height = w;
	}
	d.width += insets.left + insets.right;
	d.height += insets.top + insets.bottom;
	return d;
    }

    @Override
	public Dimension getMaximumSize() {
	Dimension d = component.getMaximumSize();
	Insets insets = getInsets();
	if (axis == RainLayout.Y_AXIS) {
	    int w = d.width;
	    d.width = d.height;
	    d.height = w;
	}
	d.width += insets.left + insets.right;
	d.height += insets.top + insets.bottom;
	return d;
    }

    AlphaComposite composite;
    boolean compositeValid;

    protected AlphaComposite getComposite() {
	if (composite == null || !compositeValid) {
	    composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha());
	    compositeValid = true;
	}
	return composite;
    }

    public void setComposite(AlphaComposite composite) {
	this.composite = composite;
    }

    @Override
	public void paintComponent(Graphics g) {
	if (component == null) {
	    return;
	}
	if (contentPainted) {
	    Dimension d = getSize();
	    Insets insets = getInsets();
	    Composite oldComposite = null;
	    if (isUseAlpha()) {
		oldComposite = ((Graphics2D) g).getComposite();
		final AlphaComposite composite = getComposite();
		((Graphics2D) g).setComposite(composite);
	    }

	    int width = d.width - (insets.left + insets.right);
	    int height = d.height - (insets.top + insets.bottom);
	    if (axis == RainLayout.X_AXIS) {
		crp.paintComponent(g, component, this, insets.left, insets.top, width, height);
	    } else {
		Graphics2D g2d = (Graphics2D) g.create();
		if (!clockwise) {
		    g2d.rotate(Math.toRadians(-90));
		    g2d.translate(-d.height, 0);
		} else {
		    g2d.rotate(Math.toRadians(90));
		    g2d.translate(0, -d.width);
		}
		crp.paintComponent(g2d, component, this, insets.left, insets.top, height, width);
		g2d.dispose();
	    }
	    if (useAlpha) {
		if (oldComposite != null) {
		    ((Graphics2D) g).setComposite(oldComposite);
		} else {
		    g.setPaintMode();
		}
	    }
	}
    }

    @Override
	protected void paintBorder(Graphics g) {
	if (borderPainted) {
	    super.paintBorder(g);
	}
    }

    protected MouseHandler getMouseHandler() {
	if (mouseHandler == null) {
	    mouseHandler = new MouseHandler();
	}
	return mouseHandler;
    }

    public boolean isBorderPainted() {
	return borderPainted;
    }

    @Override
	public boolean contains(int x, int y) {
	return !ignoreMouse && super.contains(x, y);
    }

    public void setBorderPainted(boolean borderPainted) {
	this.borderPainted = borderPainted;
    }

    public boolean isClockwise() {
	return clockwise;
    }

    public void setClockwise(boolean clockwise) {
	this.clockwise = clockwise;
    }

    public int getAxis() {
	return axis;
    }

    public void setAxis(int axis) {
	this.axis = axis;
    }

    public boolean isIgnoreMouse() {
	return ignoreMouse;
    }

    public void setIgnoreMouse(boolean ignoreMouse) {
	this.ignoreMouse = ignoreMouse;
    }

    public boolean isContentPainted() {
	return contentPainted;
    }

    public void setContentPainted(boolean contentPainted) {
	this.contentPainted = contentPainted;
    }

    public float getAlpha() {
	return alpha;
    }

    public void setAlpha(float alpha) {
	this.alpha = alpha;
	compositeValid = false;
    }

    public boolean isUseAlpha() {
	return useAlpha;
    }

    public void setUseAlpha(boolean useAlpha) {
	this.useAlpha = useAlpha;
    }

    protected class MouseHandler implements MouseInputListener {
	@Override
	public void mouseClicked(MouseEvent e) {
	    dispatchToRenderer(component, e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	    dispatchToRenderer(component, e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    dispatchToRenderer(component, e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	    dispatchToRenderer(component, e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
	    dispatchToRenderer(component, e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	    dispatchToRenderer(component, e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	    dispatchToRenderer(component, e);
	}
    }
}
