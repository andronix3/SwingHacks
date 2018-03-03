package com.smartg.swing.treetable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

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

		TableCellRenderer renderer = new DefaultTableCellRenderer();
		TreeTableModel.Builder builder = new TreeTableModel.Builder().setTable(table).setRenderer(renderer);
		model.installFirstColumnRenderer(builder);

		JFrame frame = new JFrame();
		frame.getContentPane().add(new JScrollPane(table));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
