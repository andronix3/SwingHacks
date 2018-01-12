package com.smartg.swing.titlebar;

import java.awt.Component;
import java.util.Comparator;

/**
 * Date: 26.01.2008
 *
 * @author Andrey Kuznetsov
 */
class TitleBarComparator implements Comparator<Component> {
    public int compare(Component o1, Component o2) {

        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }

        if (o1 instanceof BasicTitleBar.Ordered && o2 instanceof BasicTitleBar.Ordered) {

            int oo1 = ((BasicTitleBar.Ordered) o1).getPosition();
            int oo2 = ((BasicTitleBar.Ordered) o2).getPosition();

            if (oo1 == oo2) {
                return 0;
            }
            else if (oo1 > oo2) {
                return 1;
            }
            else {
                return -1;
            }
        }
        return 0;
    }
}
