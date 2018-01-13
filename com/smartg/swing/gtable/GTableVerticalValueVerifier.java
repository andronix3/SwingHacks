package com.smartg.swing.gtable;

import java.awt.Point;

import javax.swing.JTable;

import com.smartg.swing.scroll.ValueVerifier;

/**
 * @author Andrey Kuznetsov
 */
public class GTableVerticalValueVerifier implements ValueVerifier {
    private final JTable pane;

    public GTableVerticalValueVerifier(JTable pane) {
        this.pane = pane;
    }

    public int verify(int value) {
        int row = pane.rowAtPoint(new Point(10, value + 1));
        return pane.getCellRect(row, 1, false).y;
    }
}
