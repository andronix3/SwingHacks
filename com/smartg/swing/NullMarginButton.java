package com.smartg.swing;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class NullMarginButton extends JButton {

    private static final long serialVersionUID = -6293657435757168761L;
    private static final Insets MARGIN = new Insets(0, 0, 0, 0);

    public NullMarginButton() {
        setMargin(MARGIN);
    }

    public NullMarginButton(Icon icon) {
        super(icon);
        setMargin(MARGIN);
    }

    public NullMarginButton(String text) {
        super(text);
        setMargin(MARGIN);
    }

    public NullMarginButton(Action a) {
        super(a);
        setMargin(MARGIN);
    }

    public NullMarginButton(String text, Icon icon) {
        super(text, icon);
        setMargin(MARGIN);
    }

    @Override
    public final void setMargin(Insets m) {
        super.setMargin(MARGIN);
    }
}
