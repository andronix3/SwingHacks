package com.smartg.swing.treetable;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class FoldHandler extends MouseAdapter {

    private final JTable table;
    private final TreeTableModel model;

    public FoldHandler(JTable table) {
        this.table = table;
        this.model = (TreeTableModel) table.getModel();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            int col = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());
            
            if (col == 0) {
                Rectangle r = table.getCellRect(row, col, true);

                int countToRoot = model.getCountToRoot(row);
                int min = (countToRoot - 2) * 10;
                int max = (countToRoot + 2) * 10;

                int x = e.getX() - r.x;
                if (x >= min && x <= max) {
                    boolean collapsed = model.isCollapsed(row);
                    if (collapsed) {
                	model.expandRow(row);
                    } else {
                	model.collapseRow(row);
                    }
                }
                table.getSelectionModel().setSelectionInterval(row, row);
            }
        }
    }
}