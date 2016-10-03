package com.smartg.swing;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class NullMarginButton extends JButton {

    private static final long serialVersionUID = -6293657435757168761L;

    public NullMarginButton() {
    }

    public NullMarginButton(Icon icon) {
	super(icon);
    }

    public NullMarginButton(String text) {
	super(text);
    }

    public NullMarginButton(Action a) {
	super(a);
    }

    public NullMarginButton(String text, Icon icon) {
	super(text, icon);
    }

    static final Insets margin = new Insets(0, 0, 0, 0);

    public void setMargin(Insets m) {
	super.setMargin(margin);
    }
}