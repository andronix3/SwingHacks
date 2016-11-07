package com.smartg.swing.treetable;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;

import javax.swing.tree.TreeNode;

import com.smartg.java.util.SafeIterator;

public class TreeRow implements TreeNode {
    private final Row row;
    private TreeRow parent;

    boolean collapsed;
    private final ArrayList<TreeRow> children = new ArrayList<>();

    public TreeRow(Row row) {
	this.row = Objects.requireNonNull(row);
    }

    public int getChildCount() {
	return getChildren().size();
    }

    public boolean isLeaf() {
	return getChildCount() == 0;
    }

    public void addChild(TreeRow child) {
	children.add(child);
	child.setParent(this);
    }

    public ArrayList<TreeRow> getChildren() {
	return children;
    }

    boolean isCollapsed() {
	return collapsed;
    }

    public TreeRow getParent() {
	return parent;
    }

    public Row getRow() {
	return row;
    }

    public int getCountToRoot() {
	TreeRow p = parent;
	int count = -1;
	do {
	    p = p.getParent();
	    count++;
	} while (p != null);
	return count;
    }

    public void setParent(TreeRow parent) {
	this.parent = parent;
	getRow().setParentId(parent.getRow().getId());
    }

    public void setCollapsed(boolean collapsed) {
	this.collapsed = collapsed;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 67 * hash + Objects.hashCode(this.row);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final TreeRow other = (TreeRow) obj;
	return Objects.equals(this.row, other.row);
    }

    @Override
    public TreeRow getChildAt(int childIndex) {
	return children.get(childIndex);
    }

    @Override
    public int getIndex(TreeNode node) {
	return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
	return true;
    }

    @Override
    public Enumeration<TreeRow> children() {
	return new SafeIterator<TreeRow>(children.iterator());
    }
}