package com.smartg.swing.table;

import java.awt.Point;

import javax.swing.JTable;

import com.smartg.swing.scroll.ValueVerifier;

/**
 * @author Andrey Kuznetsov
 */
public class GTableHorizontalValueVerifier implements ValueVerifier {
    private final JTable pane;

    public GTableHorizontalValueVerifier(JTable pane) {
        this.pane = pane;
    }

    public int verify(int value) {
        int col = pane.columnAtPoint(new Point(value + 1, 10));
        return pane.getCellRect(1, col, false).x;
    }
}
