package com.smartg.swing.taskpane;

import java.awt.Color;

import javax.swing.Icon;

/**
 * @author Andrey Kuznetsov
 */
public interface TaskPaneIcon extends Icon {
    /**
     * same as setExpanded(!isExpanded());
     */
    void flip();

    /**
     * get foreground color
     */
    Color getForeground();

    /**
     * get foreground color
     */
    void setForeground(Color foreground);

    /**
     * change expanded state of TaskPaneIcon (e.g if expanded - arrow shows up)
     */
    void setExpanded(boolean expanded);

    /**
     * get expanded state of TaskPaneIcon
     */
    boolean isExpanded();

    boolean isContentOpaque();

    void setContentOpaque(boolean contentOpaque);
}
