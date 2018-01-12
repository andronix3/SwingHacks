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
package com.smartg.swing.titlebar;

import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.smartg.swing.ButtonRenderer;
import com.smartg.swing.ComponentRenderer;
import com.smartg.swing.RainLayout;
import com.smartg.swing.SortableContainer;

/**
 * Basic implementation of TitleBar - add/remove Actions, x/y axis.
 * 
 * @see com.imagero.swing.renderer.ButtonRenderer
 * @see com.imagero.swing.layout.RainLayout
 * @author Andrey Kuznetsov
 */
public class BasicTitleBar extends JComponent implements SortableContainer {

	private static final long serialVersionUID = 4227524041533615067L;
	RainLayout layout;
	CellRendererPane crp;

	HashMap<String, ComponentRenderer> compTable = new HashMap<String, ComponentRenderer>();
	HashMap<String, Integer> posTable = new HashMap<String, Integer>();

//	public static final String LEFT_GLUE = "leftGlue_";
//	public static final String RIGHT_GLUE = "rightGlue_";
	protected boolean palette;

	public BasicTitleBar() {
		this(RainLayout.X_AXIS);
	}

	public BasicTitleBar(int axis) {
		layout = new RainLayout(axis, 0, 0, RainLayout.LAYOUT_POLICY_COMPUTE);
		super.setLayout(layout);
		crp = new CellRendererPane();
		super.addImpl(crp, null, -1);
	}

	public void add(AbstractButton b, String key, boolean canStretch) {
		Integer position = posTable.get(key);
		if (position == null) {
			add(b, key, canStretch, 0);
		} else {
			add(b, key, canStretch, position.intValue());
		}
	}

	public void add(AbstractButton b, String key, boolean canStretch, int position) {
		PButtonProxy bp = new PButtonProxy(b, crp);
		compTable.put(key, bp);
		bp.position = position;
		super.addImpl(bp, canStretch ? null : RainLayout.FIXED, -1);
		valid = false;
	}

	public JComponent remove(String key) {
		JComponent c = compTable.remove(key);
		if (c != null) {
			super.remove(c);
			crp.remove(c);
			valid = false;
		}
		return c;
	}

	public void add(JLabel label, String key, int stretch) {
		Integer position = posTable.get(key);
		if (position == null) {
			add(label, key, stretch, 0);
		} else {
			add(label, key, stretch, position.intValue());
		}
	}

	public static final int STRETCH_NONE = 0;
	public static final int STRETCH_GROW = 1;
	public static final int STRETCH_SHRINK = 2;
	public static final int STRETCH_FLEX = 3;

	String[] constraints = { RainLayout.FIXED, RainLayout.GROW, RainLayout.SHRINK, RainLayout.FLEX };

	public void add(JLabel label, String key, int flex, int position) {
		PComponentProxy bp = new PComponentProxy(label, crp);
		compTable.put(key, bp);
		bp.position = position;
		super.addImpl(bp, constraints[flex], -1);
		valid = false;
	}

	public ComponentRenderer getComponent(String key) {
		return compTable.get(key);
	}

	public ButtonRenderer getButton(String key) {
		return (ButtonRenderer) compTable.get(key);
	}

	public void defineComponentPosition(String key, int position) {
		posTable.put(key, new Integer(position));
		Object o = compTable.get(key);
		if (o != null) {
			((Ordered) o).setPosition(position);
		}
	}

	public void checkComponentPosition(String key) {
		Integer position = posTable.get(key);
		if (position != null) {
			Object o = compTable.get(key);
			if (o != null) {
				((Ordered) o).setPosition(position.intValue());
			}
		}
	}

	boolean valid;

	protected Comparator<Component> comparator;
	ComponentRenderer[] comps;

	@Override
	public Comparator<Component> getComparator() {
		return comparator;
	}

	@Override
	public Component[] getComponentsOrdered() {
		if (!valid) {
			ComponentRenderer[] comps = new ComponentRenderer[compTable.size()];
			int cnt = 0;
			for (int i = 0; i < getComponentCount(); i++) {
				Component c = getComponent(i);
				if (c instanceof ComponentRenderer) {
					comps[cnt++] = (ComponentRenderer) c;
				}
			}
			Comparator<Component> comparator = getComparator();
			if (comparator != null) {
				Arrays.sort(comps, comparator);
			}
			this.comps = comps;
			valid = true;
		}
		return this.comps;
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		throw new RuntimeException("use add(AbstractButton b, Object key) or add(JLabel label, Object key)");
	}

	@Override
	public void setLayout(LayoutManager mgr) {
		throw new RuntimeException("TitleBar use only RainLayout");
	}

	interface Ordered {
		int getPosition();

		void setPosition(int position);
	}

	public int getAxis() {
		return layout.getAxis();
	}

	public void setAxis(int axis) {
		layout.setAxis(axis);
		for (int i = 0; i < getComponentCount(); i++) {
			Component c = getComponent(i);
			if (c instanceof ComponentRenderer) {
				((ComponentRenderer) c).setAxis(axis);
			}
		}
	}

	public boolean isPalette() {
		return palette;
	}

	public void setPalette(boolean palette) {
		this.palette = palette;
	}

	static class PComponentProxy extends ComponentRenderer implements Ordered {

		private static final long serialVersionUID = -6636277951742697989L;
		int position;

		public PComponentProxy(JComponent c) {
			super(c);
		}

		public PComponentProxy(JComponent c, CellRendererPane crp) {
			super(c, crp);
		}

		@Override
		public int getPosition() {
			return position;
		}

		@Override
		public void setPosition(int position) {
			this.position = position;
		}
	}

	static class PButtonProxy extends ButtonRenderer implements Ordered {

		private static final long serialVersionUID = 2392730986839301249L;
		int position;

		public PButtonProxy(AbstractButton c) {
			super(c);
		}

		public PButtonProxy(AbstractButton c, CellRendererPane crp) {
			super(c, crp);
		}

		@Override
		public int getPosition() {
			return position;
		}

		@Override
		public void setPosition(int position) {
			this.position = position;
		}
	}
}
