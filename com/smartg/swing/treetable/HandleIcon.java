package com.smartg.swing.treetable;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Objects;

import javax.swing.Icon;

public class HandleIcon implements Icon {

    private final Image image;
    private int indent;

    public HandleIcon(Image image) {
        this.image = Objects.requireNonNull(image);
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(image, x + indent, y, null);
    }

    @Override
    public int getIconWidth() {
        return image.getWidth(null) + indent;
    }

    @Override
    public int getIconHeight() {
        return image.getHeight(null);
    }
}
