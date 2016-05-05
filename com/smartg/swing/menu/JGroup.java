/**
 * 
 */
package com.smartg.swing.menu;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenu;

public class JGroup {

    String name;
    ArrayList<JComponent> items;
    GSeparator separator;

    JComponent parent;

    boolean visible = true;
    boolean interactiveSeparator;

    private static final String groupsKey = "GroupsList";

    public JGroup(JComponent parent, String name) {
	this(parent, name, true);
    }

    public JGroup(JComponent parent, String name, boolean interactiveSeparator) {
	this.name = name;
	this.parent = parent;
	this.interactiveSeparator = interactiveSeparator;

	getList(parent, true).add(this);

	separator = new GSeparator(parent, this, interactiveSeparator);
	items = new ArrayList<JComponent>();

	separator.putClientProperty("Group", this);
	if (parent instanceof JMenu) {
	    ((JMenu) parent).getPopupMenu().addContainerListener(new ContainerListener() {
		public void componentAdded(ContainerEvent e) {
		    validateSeparator();
		}

		public void componentRemoved(ContainerEvent e) {
		    validateSeparator();
		}
	    });
	}
    }

    private void validateSeparator() {
	ArrayList<JGroup> groups = getList(parent, false);
	if (groups != null) {
	    boolean first = true;
	    for (int i = 0; i < groups.size(); i++) {
		JGroup group = groups.get(i);
		final boolean groupVisible = group.isSomeItemVisible();
		if (groupVisible) {
		    if (first) {
			if (!group.isInteractiveSeparator()) {
			    group.separator.setVisible(false);
			}
			first = false;
		    } else {
			group.separator.setVisible(true);
		    }
		} else {
		    if (!group.isInteractiveSeparator()) {
			group.separator.setVisible(false);
		    }
		}
	    }
	}
    }
    
    public static JGroup addGroup(JComponent c, String name) {
	return getGroup(c, name, true);
    }

    public static JGroup getGroup(JComponent c, String name, boolean create) {
	ArrayList<JGroup> list = getList(c, create);
	if (name == null) {
	    name = "default";
	}
	for (JGroup g : list) {
	    if (name.equals(g.name)) {
		return g;
	    }
	}
	if (create) {
	    JGroup g = new JGroup(c, name);
	    list.add(g);
	    return g;
	}
	return null;
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<JGroup> getList(JComponent parent, boolean create) {
	ArrayList<JGroup> groups = (ArrayList<JGroup>) parent.getClientProperty(groupsKey);
	if (groups == null && create) {
	    groups = new ArrayList<JGroup>();
	    parent.putClientProperty(groupsKey, groups);
	}
	return groups;
    }

    int getPreferredWidth() {
	int w = 0;
	for (JComponent item : items) {
	    w = Math.max(w, item.getPreferredSize().width);
	}
	return w;
    }

    public void deleteGroup() {
	ArrayList<JGroup> list = getList(parent, false);
	if (list != null) {
	    list.remove(this);
	    if (list.isEmpty()) {
		parent.putClientProperty(groupsKey, null);
	    }
	}
    }

    public boolean isInteractiveSeparator() {
	return interactiveSeparator;
    }

    public void setInteractiveSeparator(boolean interactiveSeparator) {
	this.interactiveSeparator = interactiveSeparator;
	separator.setInteractive(interactiveSeparator);
    }

    public JComponent add(JComponent item) {
	int index = items.indexOf(item);
	if (index >= 0) {
	    remove(item);
	}
	item.addComponentListener(listener);
	item.putClientProperty("Group", this);

	items.add(item);
	if (items.size() == 1) {
	    parent.add(separator);
	    return (JComponent) parent.add(item);
	}
	JComponent lastItem = items.get(items.size() - 2);

	Component[] components;
	if (parent instanceof JMenu) {
	    components = ((JMenu) parent).getMenuComponents();
	} else {
	    components = parent.getComponents();
	}
	for (int i = 0; i < components.length; i++) {
	    if (components[i] == lastItem) {
		return (JComponent) parent.add(item, i + 1);
	    }
	}
	item.removeComponentListener(listener);
	item.putClientProperty("Group", null);

	throw new RuntimeException("Error in Group#add(JMenuItem item)!!!");
    }

    static final String key = "itemVisible";

    public void hideGroup() {
	if (visible) {
	    for (int i = 0; i < items.size(); i++) {
		JComponent item = items.get(i);
		if (item.isVisible()) {
		    item.putClientProperty(key, Boolean.TRUE);
		    item.setVisible(false);
		} else {
		    item.putClientProperty(key, Boolean.FALSE);
		}
	    }
	    visible = false;
	}
    }

    public boolean isVisible() {
	return visible;
    }

    public void showGroup() {
	if (!visible) {
	    for (int i = 0; i < items.size(); i++) {
		JComponent item = items.get(i);
		boolean visible = getBoolean(item.getClientProperty(key));
		if (visible) {
		    item.setVisible(visible);
		}
	    }
	    visible = true;
	}
    }

    boolean getBoolean(Object o) {
	if (o == null) {
	    return false;
	}
	if (o instanceof Boolean) {
	    return ((Boolean) o).booleanValue();
	}
	return false;
    }

    public void remove(JComponent item) {
	items.remove(item);
	item.putClientProperty("Group", null);

	item.removeComponentListener(listener);
	if (items.size() == 0) {
	    separator.setVisible(false);
	}
    }

    private boolean isSomeItemVisible() {
	for (int i = 0; i < items.size(); i++) {
	    JComponent item = items.get(i);
	    if (item.isVisible()) {
		return true;
	    }
	}
	return false;
    }

    ComponentListener listener = new ComponentAdapter() {
	public void componentShown(ComponentEvent e) {
	    validateSeparator();
	}

	public void componentHidden(ComponentEvent e) {
	    validateSeparator();
	}
    };
}