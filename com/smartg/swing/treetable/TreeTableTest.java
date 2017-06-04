package com.smartg.swing.treetable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

public class TreeTableTest {

    public static void main(String[] args) {
	Object[][] objects = { new Object[] { "1", "0, 1", "0, 2", "0, 3", "0, 4", "0, 5" },
		new Object[] { "2", "1, 1", "1, 2", "1, 3", "1, 4", "1, 5" },
		new Object[] { "3", "2, 2", "2, 2", "2, 3", "2, 4", "2, 5" },
		new Object[] { "4", "3, 3", "3, 2", "3, 3", "3, 4", "3, 5" },
		new Object[] { "5", "4, 4", "4, 2", "4, 3", "4, 4", "4, 5" }, };
	Row[] rows = new Row[5];
	for (int i = 0; i < rows.length; i++) {
	    rows[i] = new Row(i, objects[i]);
	}

	rows[1].setParentId(rows[0].getId());
	rows[2].setParentId(rows[0].getId());
	rows[3].setParentId(rows[0].getId());
	rows[4].setParentId(rows[3].getId());

	TreeTableModel model = new TreeTableModel(rows, new Object[] { "A", "B", "C", "D", "E" });
	JTable table = new JTable(model);
	
	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
	table.getColumnModel().getColumn(0).setCellRenderer(new FirstColumnRenderer(model, renderer, SwingUtilities.LEFT));	
	
	FirstColumnEditor cellEditor = new FirstColumnEditor(model, SwingUtilities.LEFT);
	table.getColumnModel().getColumn(0).setCellEditor(cellEditor);
	
	cellEditor.getButton().addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedRow = table.getSelectedRow();
			boolean collapsed = model.isCollapsed(selectedRow);
			if(collapsed) {
				model.expandRow(selectedRow);
			}
			else {
				model.collapseRow(selectedRow);
			}
		}
	});
	
	JFrame frame = new JFrame();
	frame.getContentPane().add(new JScrollPane(table));
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);

    }
}
