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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;

import com.smartg.swing.scroll.icons.GoEndIcon;
import com.smartg.swing.scroll.icons.GoStartIcon;
import com.smartg.swing.scroll.icons.PageDownIcon;
import com.smartg.swing.scroll.icons.PageUpIcon;
import com.smartg.swing.scroll.icons.SampleMenuIcon;

public class Actions {
    public static Action createGoStartAction() {
	return createGoStartAction(null);
    }

    public static Action createGoStartAction(Icon alternateIcon) {
	Icon icon = alternateIcon == null ? new GoStartIcon() : alternateIcon;
	return new AbstractAction(null, icon) {

	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent e) {
		final GScrollPane sp = (GScrollPane) ((Component) e.getSource()).getParent().getParent();
		JScrollBar vb = sp.getVerticalScrollBar();
		vb.setValue(0);
	    }
	};
    }

    public static Action createGoEndAction() {
	return createGoEndAction(null);
    }

    public static Action createGoEndAction(Icon alternateIcon) {
	Icon icon = alternateIcon == null ? new GoEndIcon() : alternateIcon;
	return new AbstractAction(null, icon) {

	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent e) {
		final GScrollPane sp = (GScrollPane) ((Component) e.getSource()).getParent().getParent();
		JScrollBar vb = sp.getVerticalScrollBar();
		vb.setValue(vb.getMaximum() - vb.getBlockIncrement(1));
	    }
	};
    }

    public static Action createPageUpAction() {
	return createPageUpAction(null);
    }

    public static Action createPageUpAction(Icon alternateIcon) {
	Icon icon = alternateIcon == null ? new PageUpIcon() : alternateIcon;
	return new AbstractAction(null, icon) {

	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent e) {
		final GScrollPane sp = (GScrollPane) ((Component) e.getSource()).getParent().getParent();
		JScrollBar vb = sp.getVerticalScrollBar();
		vb.setValue(Math.max(vb.getMinimum(), vb.getValue() - vb.getBlockIncrement(-1)));
	    }
	};
    }

    public static Action createPageDownAction() {
	return createPageDownAction(null);
    }

    public static Action createPageDownAction(Icon alternateIcon) {
	Icon icon = alternateIcon == null ? new PageDownIcon() : alternateIcon;
	return new AbstractAction(null, icon) {

	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent e) {
		final GScrollPane sp = (GScrollPane) ((Component) e.getSource()).getParent().getParent();
		JScrollBar vb = sp.getVerticalScrollBar();
		vb.setValue(Math.min(vb.getMaximum(), vb.getValue() + vb.getBlockIncrement(1)));
	    }
	};
    }

    public static Action createShowMenuAction() {
	return createShowMenuAction(null);
    }

    public static Action createShowMenuAction(Icon alternateIcon) {
	Icon icon = alternateIcon == null ? new SampleMenuIcon() : alternateIcon;
	return new AbstractAction(null, icon) {

	    private static final long serialVersionUID = 1L;

	    public void actionPerformed(ActionEvent e) {
		final JButton b = (JButton) e.getSource();
		final GScrollPane sp = (GScrollPane) b.getParent().getParent();
		JMenu menu = sp.getNavigationMenu();
		if (menu != null) {
		    final JPopupMenu popupMenu = menu.getPopupMenu();
		    Dimension d = popupMenu.getPreferredSize();
		    popupMenu.show(b, b.getX() - d.width, 0);
		}
	    }
	};
    }
}
