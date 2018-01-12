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

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;

import javax.swing.AbstractButton;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

import com.smartg.swing.ButtonRenderer;
import com.smartg.swing.ComponentRenderer;
import com.smartg.swing.RainLayout;
import com.smartg.swing.SortableContainer;
import com.smartg.swing.titlebar.plaf.TitleBarUI;
import com.smartg.swing.titlebar.plaf.basic.BasicTitleBarUI;
import com.smartg.swing.titlebar.plaf.metal.MetalTitleBarUI;
import com.smartg.swing.titlebar.plaf.motif.MotifTitleBarUI;
import com.smartg.swing.titlebar.plaf.windows.WindowsTitleBarUI;

/**
 * TitleBar for FloatingWindow
 * 
 * @author Andrei Kouznetsov Date: 04.12.2003 Time: 13:23:55
 */
public class TitleBar extends BasicTitleBar implements SortableContainer {

	private static final long serialVersionUID = -7059658821640697237L;
	static final String _package = TitleBar.class.getPackage().getName() + ".plaf";
	private static final String uiClassID = "TitleBarUI";

	public static final String MAXIMIZE = "maximize";
	public static final String MINIMIZE = "minimize";
	public static final String CLOSE = "close";
	public static final String RESTORE = "restore";
	public static final String UNDOCK = "undock";
	public static final String DOCK = "dock";
	public static final String SYSTEM = "system";
	public static final String TITLE = "title";
	public static final String GLUE = "glue";

	static {
		checkUI();
		UIManager.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				checkUI();
			}
		});
	}

	private static void checkUI() {
		String lafName = UIManager.getLookAndFeel().getName().toLowerCase();
		if (lafName.indexOf("motif") != -1) {
			put("TitleBarUI", MotifTitleBarUI.class.getName());
		} else if (lafName.indexOf("windows") != -1) {
			put("TitleBarUI", WindowsTitleBarUI.class.getName());
		} else if (lafName.indexOf("metal") != -1) {
			put("TitleBarUI", MetalTitleBarUI.class.getName());
		} else {
			put("TitleBarUI", BasicTitleBarUI.class.getName());
		}
	}

	private static void put(String key, String value) {
		UIManager.put(key, value);
		System.out.println(value);
	}

	CellRendererPane crp = new CellRendererPane();

	protected boolean armed;
	protected boolean active;
	protected boolean maximized;
	protected boolean docked;

	protected boolean closable;
	protected boolean maximizable;
	protected boolean minimizable;
	protected boolean dockable;

	boolean showTitle = true;

	JPopupMenu popup;

	boolean clockwise;

	MenuListener menuListener = new MenuListener();

	@Override
	public Comparator<Component> getComparator() {
		if (comparator == null) {
			comparator = new TitleBarComparator();
		}
		return comparator;
	}

	public AbstractButton getSystemButton() {
		ButtonRenderer c = getButton(SYSTEM);
		if (c != null) {
			return c.getButton();
		}
		return null;
	}

	public boolean isClockwise() {
		return clockwise;
	}

	public void setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
		layout.setReversedComponentOrder(!clockwise && getAxis() == RainLayout.Y_AXIS);
		ComponentRenderer[] comps = (ComponentRenderer[]) getComponentsOrdered();
		for (ComponentRenderer comp : comps) {
			comp.setClockwise(clockwise);
		}
	}

	@Override
	public void setAxis(int axis) {
		super.setAxis(axis);
		layout.setReversedComponentOrder(!clockwise && axis == RainLayout.Y_AXIS);
	}

	public void setSystemButton(AbstractButton systemButton) {
		ButtonRenderer bp = getButton(SYSTEM);
		remove(SYSTEM);
		if (bp != null) {
			bp.getButton().removeActionListener(menuListener);
		}
		if (systemButton != null) {
			add(systemButton, SYSTEM, false);
			systemButton.addActionListener(menuListener);
		}
	}

	public AbstractButton getMinimizeButton() {
		ButtonRenderer c = getButton(MINIMIZE);
		if (c != null) {
			return c.getButton();
		}
		return null;
	}

	public void setMinimizeButton(AbstractButton minimizeButton) {
		remove(MINIMIZE);
		add(minimizeButton, MINIMIZE, false);
	}

	public AbstractButton getMaximizeButton() {
		ButtonRenderer c = getButton(MAXIMIZE);
		if (c != null) {
			return c.getButton();
		}
		return null;
	}

	public void setMaximizeButton(AbstractButton maximizeButton) {
		remove(MAXIMIZE);
		add(maximizeButton, MAXIMIZE, false);
	}

	public AbstractButton getCloseButton() {
		ButtonRenderer c = getButton(CLOSE);
		if (c != null) {
			return c.getButton();
		}
		return null;
	}

	public void setCloseButton(AbstractButton closeButton) {
		remove(CLOSE);
		add(closeButton, CLOSE, false);
	}

	public AbstractButton getDockButton() {
		ButtonRenderer c = getButton(DOCK);
		if (c != null) {
			return c.getButton();
		}
		return null;
	}

	public void setDockButton(AbstractButton dockButton) {
		remove(DOCK);
		add(dockButton, DOCK, false);
	}

	public AbstractButton getUndockButton() {
		ButtonRenderer c = getButton(UNDOCK);
		if (c != null) {
			return c.getButton();
		}
		return null;
	}

	public void setUndockButton(AbstractButton undockButton) {
		remove(UNDOCK);
		add(undockButton, UNDOCK, false);
	}

	public void setGlueLabel(JLabel label) {
		remove(GLUE);
		add(label, GLUE, STRETCH_FLEX);
		getComponent(GLUE).setIgnoreMouse(true);
	}

	public JLabel getGlueLabel() {
		ComponentRenderer c = getComponent(GLUE);
		if (c != null) {
			return (JLabel) c.getComponent();
		}
		return null;
	}

	public AbstractButton getRestoreButton() {
		ButtonRenderer c = getButton(RESTORE);
		if (c != null) {
			return c.getButton();
		}
		return null;
	}

	public void setRestoreButton(AbstractButton restoreButton) {
		remove(RESTORE);
		add(restoreButton, RESTORE, false);
	}

	/**
	 * true if TitleBar is armed (e.g. if FloatingWindow can be docked)
	 * 
	 * @return true if TitleBar is armed
	 */
	public boolean isArmed() {
		return armed;
	}

	/**
	 * set or clear armed state for TitleBar
	 * 
	 * @param armed
	 *            boolean
	 */
	public void setArmed(boolean armed) {
		this.armed = armed;
		repaint();
	}

	public void setTitle(String title) {
		ComponentRenderer cp = getComponent(TITLE);
		cp.setIgnoreMouse(true);
		JLabel label = (JLabel) cp.getComponent();
		label.setText(title);
	}

	public void setSystemIcon(Icon icon) {
		getSystemButton().setIcon(icon);
	}

	public void setMaximized(boolean b) {
		this.maximized = b;
		checkButtons();
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setDocked(boolean b) {
		this.docked = b;
		checkButtons();
	}

	public boolean isDockable() {
		return dockable;
	}

	public void setDockable(boolean b) {
		this.dockable = b;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
		Component c = getComponent(TITLE);
		if (c != null) {
			c.setVisible(showTitle);
		}
	}

	ActionListener actionListener = null;

	public synchronized void addActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.add(actionListener, l);
	}

	public synchronized void removeActionListener(ActionListener l) {
		actionListener = AWTEventMulticaster.remove(actionListener, l);
	}

	public void processEvent(ActionEvent e) {
		ActionListener listener = actionListener;
		if (listener != null) {
			listener.actionPerformed(e);
		}
	}

	/**
	 * create horizontal TitleBar
	 */
	public TitleBar() {
		this(RainLayout.X_AXIS);
	}

	public TitleBar(int axis) {
		super(axis);
		createTitleLabel();
		updateUI();
		definePositions();
		checkButtons();
	}

	protected void checkPositions() {
		checkComponentPosition(TITLE);
		checkComponentPosition(SYSTEM);
		checkComponentPosition(MAXIMIZE);
		checkComponentPosition(MINIMIZE);
		checkComponentPosition(RESTORE);
		checkComponentPosition(CLOSE);
		checkComponentPosition(DOCK);
		checkComponentPosition(UNDOCK);
		checkComponentPosition(GLUE);
	}

	protected void definePositions() {
		defineComponentPosition(CLOSE, Integer.MAX_VALUE);
		defineComponentPosition(SYSTEM, Integer.MIN_VALUE);
		defineComponentPosition(TITLE, Integer.MIN_VALUE + 100);
		defineComponentPosition(GLUE, Integer.MIN_VALUE + 200);

		defineComponentPosition(MAXIMIZE, Integer.MAX_VALUE - 100);
		defineComponentPosition(RESTORE, Integer.MAX_VALUE - 100);
		defineComponentPosition(MINIMIZE, Integer.MAX_VALUE - 200);
		defineComponentPosition(DOCK, Integer.MAX_VALUE - 300);
		defineComponentPosition(UNDOCK, Integer.MAX_VALUE - 300);
	}

	protected void createTitleLabel() {
		TitleLabel label = new TitleLabel();
		add(label, TITLE, STRETCH_SHRINK, 0);
		getComponent(TITLE).setIgnoreMouse(true);
	}

	public TitleBarUI getUI() {
		return (TitleBarUI) ui;
	}

	@Override
	public void updateUI() {
		setUI(UIManager.getUI(this));
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	@Override
	public void setPalette(boolean palette) {
		super.setPalette(palette);
		checkButtons();
	}

	@Override
	public void doLayout() {
		super.doLayout();
	}

	@Override
	public Dimension getMinimumSize() {
		return layout.minimumLayoutSize(this);
	}

	protected void checkButtons() {
		Component label = getComponent(TITLE);
		Component systemButton = getButton(SYSTEM);
		Component maximizeButton = getButton(MAXIMIZE);
		Component minimizeButton = getButton(MINIMIZE);
		Component restoreButton = getButton(RESTORE);
		Component closeButton = getButton(CLOSE);
		Component dockButton = getButton(DOCK);
		Component undockButton = getButton(UNDOCK);
		Component glueLabel = getComponent(GLUE);

		glueLabel.setVisible(true);
		checkPositions();

		label.setVisible(showTitle);

		if (palette) {
			systemButton.setVisible(false);
			maximizeButton.setVisible(false);
			minimizeButton.setVisible(false);
			restoreButton.setVisible(false);
			closeButton.setVisible(false);
			dockButton.setVisible(false);
			undockButton.setVisible(false);
		} else {
			systemButton.setVisible(true);
			if (closable) {
				closeButton.setVisible(true);
			} else {
				closeButton.setVisible(false);
			}
			if (maximizable) {
				if (!maximized) {
					maximizeButton.setVisible(true);
					restoreButton.setVisible(false);
				} else {
					maximizeButton.setVisible(false);
					restoreButton.setVisible(true);
				}
			} else {
				maximizeButton.setVisible(false);
				restoreButton.setVisible(false);
			}
			if (minimizable) {
				minimizeButton.setVisible(true);
			} else {
				minimizeButton.setVisible(false);
			}
			if (dockable) {
				if (docked) {
					dockButton.setVisible(false);
					undockButton.setVisible(true);
				} else {
					dockButton.setVisible(true);
					undockButton.setVisible(false);
				}
			} else {
				dockButton.setVisible(false);
				undockButton.setVisible(false);
			}
		}
	}

	public boolean isMaximizable() {
		return maximizable;
	}

	public void setMaximizable(boolean maximizable) {
		this.maximizable = maximizable;
		checkButtons();
	}

	public boolean isMinimizable() {
		return minimizable;
	}

	public void setMinimizable(boolean minimizable) {
		this.minimizable = minimizable;
		checkButtons();
	}

	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
		checkButtons();
	}

	/**
	 * Determine if this tool bar is currently active. (Active tool bar is
	 * usually highlighted).
	 * 
	 * @return
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets active state of this title bar. Ignored if isPalette() returns true.
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
		ComponentRenderer cp = getComponent(TITLE);
		TitleLabel label = (TitleLabel) cp.getComponent();
		label.setBackground(active ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControl());
		label.setForeground(active ? label.selectedTextColor : label.notSelectedTextColor);
		repaint();
	}

	public JPopupMenu getPopup() {
		return popup;
	}

	public void setPopup(JPopupMenu popup) {
		this.popup = popup;
	}

	class TitleLabel extends JLabel {
		private static final long serialVersionUID = -4760837409888695881L;
		Color selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
		Color notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");

		public TitleLabel() {
			setFont(UIManager.getFont("InternalFrame.titleFont"));
			setForeground(notSelectedTextColor);
			setBorder(new EmptyBorder(0, 5, 0, 5));
		}
	}

	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (getPopup() != null) {
				JComponent b = getComponent(SYSTEM);
				getPopup().show(b, b.getX(), b.getY() + b.getHeight());
			}
		}
	}

}
