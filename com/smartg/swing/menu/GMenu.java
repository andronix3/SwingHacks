package com.smartg.swing.menu;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class GMenu extends JMenu {

    private static final long serialVersionUID = -749328500473703275L;

    public GMenu() {
    }

    public GMenu(String s) {
	super(s);
    }

    public GMenu(Action a) {
	super(a);
    }

    public GMenu(String s, boolean b) {
	super(s, b);
    }
    
    public void addJMenuItem(JMenuItem menuItem) {
	addJMenuItem(menuItem, null);
    }

    public void addJMenuItem(JMenuItem menuItem, String group) {
	JGroup g = JGroup.getGroup(this, group, true);
	g.add(menuItem);
    }

    public JGroup getGroup(String groupName) {
	return JGroup.getGroup(this, groupName, false);
    }
}
