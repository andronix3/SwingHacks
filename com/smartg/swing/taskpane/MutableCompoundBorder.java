package com.smartg.swing.taskpane;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * @author Andrey Kuznetsov
 */
class MutableCompoundBorder extends CompoundBorder {

    private static final long serialVersionUID = 5533880578292432167L;

    public MutableCompoundBorder() {
    }

    public MutableCompoundBorder(Border outsideBorder, Border insideBorder) {
	super(outsideBorder, insideBorder);
    }

    public void setOutsideBorder(Border outsideBorder) {
	this.outsideBorder = outsideBorder;
    }

    public void setInsideBorder(Border insideBorder) {
	this.insideBorder = insideBorder;
    }
}
