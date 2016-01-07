package com.smartg.swing.menu;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class GPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 8408536758166957383L;

    public GPopupMenu() {

    }

    public GPopupMenu(String label) {
	super(label);
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
