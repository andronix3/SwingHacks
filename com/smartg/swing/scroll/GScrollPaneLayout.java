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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.ScrollPaneLayout;

/**
 * @author Andrey Kuznetsov
 */
public class GScrollPaneLayout extends ScrollPaneLayout {

    private static final long serialVersionUID = 1849230225989595637L;

    public static final String VPANEL = "vpanel";
    private Component vp;

    public void addLayoutComponent(String s, Component c) {
	if (VPANEL.equals(s)) {
	    vp = addSingletonComponent(vp, c);
	} else {
	    super.addLayoutComponent(s, c);
	}
    }

    public void removeLayoutComponent(Component c) {
	if (vp == c) {
	    vp = null;
	} else {
	    super.removeLayoutComponent(c);
	}
    }

    public void layoutContainer(Container parent) {
	super.layoutContainer(parent);
	if (vp != null) {
	    Dimension d = vp.getPreferredSize();
	    if (vsb != null) {
		Rectangle r = vsb.getBounds();
		vp.setBounds(r.x, r.y + r.height - d.height, r.width, d.height);
		vsb.setSize(r.width, r.height - d.height);
	    }
	}
    }
}
