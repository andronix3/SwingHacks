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

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.CellRendererPane;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

/**
 * ButtonRenderer - rotate swing buttons without breaking layout.
 * 
 * @author Andrey Kuznetsov
 */
public class ButtonRenderer extends ComponentRenderer {

    private static final long serialVersionUID = -8622019296749427189L;
    public static final int DEFAULT_DESCRIPTION = 0;
    public static final int SHORT_DESCRIPTION = 1;
    public static final int LONG_DESCRIPTION = 2;

    boolean rotateBorder;

    int toolTipType;

    public ButtonRenderer(AbstractButton button) {
	this(button, null);
    }

    /**
     * create Button renderer with shared CellRendererPane
     * 
     * @param button
     *            AbstractButton
     * @param crp
     *            CellRendererPane
     */
    public ButtonRenderer(AbstractButton button, CellRendererPane crp) {
	super(button, crp);

	button.addActionListener(new ActionListener() {
	    @Override
		public void actionPerformed(ActionEvent e) {
		Container parent = getParent();
		if (parent != null) {
		    parent.repaint();
		}
	    }
	});
    }

    public AbstractButton getButton() {
	return (AbstractButton) component;
    }

    public boolean isRotateBorder() {
	return rotateBorder;
    }

    /**
     * If rotateBorder is false then border is painted without rotating even if
     * component is rotated. Note: rotated borders looks sometimes horrible.
     */
    public void setRotateBorder(boolean rotateBorder) {
	this.rotateBorder = rotateBorder;
    }

    @Override
	public boolean isUseAlpha() {
	return useAlpha && getButton().isRolloverEnabled() && !getButton().getModel().isRollover();
    }

    public int getToolTipType() {
	return toolTipType;
    }

    public void setToolTipType(int toolTipType) {
	this.toolTipType = toolTipType;
	String command = getButton().getActionCommand();
	if (command != null && command.length() == 0) {
	    command = null;
	}
	Action action = getButton().getAction();
	if (action == null) {
	    getButton().setToolTipText(command);
	} else {
	    switch (toolTipType) {
	    case DEFAULT_DESCRIPTION:
		getButton().setToolTipText(command);
		break;
	    case SHORT_DESCRIPTION:
		String text = (String) action.getValue(Action.SHORT_DESCRIPTION);
		if (text != null && text.length() > 0) {
		    getButton().setToolTipText(text);
		} else {
		    getButton().setToolTipText(command);
		}
		break;
	    case LONG_DESCRIPTION:
		text = (String) action.getValue(Action.LONG_DESCRIPTION);
		if (text != null && text.length() > 0) {
		    getButton().setToolTipText(text);
		} else {
		    getButton().setToolTipText(command);
		}
		break;
	    }
	}
    }

    @Override
	public void paintComponent(Graphics g) {
	AbstractButton button = (AbstractButton) component;
	if (axis == RainLayout.Y_AXIS && !rotateBorder) {
	    boolean borderPainted = button.isBorderPainted();
	    button.setBorderPainted(false);
	    super.paintComponent(g);
	    button.setBorderPainted(true);
	    if (borderPainted) {
		Border border = button.getBorder();
		if (border != null) {
		    if (crp.getBackground() == null) {
			crp.setBackground(Color.gray);
		    }
		    border.paintBorder(button, g, 0, 0, getWidth(), getHeight());
		}
	    }
	} else {
	    super.paintComponent(g);
	}
    }

    public static void main(String[] args) {
	JFrame frame = new JFrame();
	AbstractButton renderer = new JToggleButton("JButtonProxy");
	AbstractButton renderer2 = new JToggleButton("JToggleButtonProxy");
	
	renderer.setMnemonic(KeyEvent.VK_B);
	renderer2.setMnemonic(KeyEvent.VK_T);

	ButtonGroup bg = new ButtonGroup();
	bg.add(renderer);
	bg.add(renderer2);

	ButtonRenderer bp = new ButtonRenderer(renderer);
	bp.axis = RainLayout.Y_AXIS;
	frame.getContentPane().add(bp);

	ButtonRenderer bp2 = new ButtonRenderer(renderer2);
	bp2.axis = RainLayout.Y_AXIS;
	frame.getContentPane().add(bp2);

	frame.getContentPane().setLayout(new FlowLayout());
	frame.setSize(200, 200);
	frame.setVisible(true);
    }
}
