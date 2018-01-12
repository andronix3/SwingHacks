package com.smartg.swing.taskpane;


/**
 * ArrowIcon for TaskPane.
 * 
 * @author Andrey Kuznetsov
 */
public class TaskPaneArrowIcon extends DoubleArrawIcon implements TaskPaneIcon {

    public TaskPaneArrowIcon(Direction d) {
	super(d);
    }

    @Override
	public void flip() {
	direction = direction.flip();
    }

    @Override
	public void setExpanded(boolean expanded) {
	if (expanded) {
	    direction = Direction.UP;
	} else {
	    direction = Direction.DOWN;
	}
    }

    @Override
	public boolean isExpanded() {
	return direction == Direction.UP;
    }
}
