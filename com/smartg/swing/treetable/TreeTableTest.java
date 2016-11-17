package com.smartg.swing.treetable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
	BufferedImage b1 = new BufferedImage(11, 11, BufferedImage.TYPE_4BYTE_ABGR);
	Graphics2D g = b1.createGraphics();
	g.setColor(new Color(255, 255, 255, 0));
	g.fillRect(0, 0, 11, 11);
	g.setColor(Color.DARK_GRAY);
	g.drawLine(0, 5, 10, 5);
	g.drawLine(5, 0, 5, 10);
	g.drawRect(0, 0, 10, 10);

	BufferedImage b2 = new BufferedImage(11, 11, BufferedImage.TYPE_4BYTE_ABGR);
	g = b2.createGraphics();
	g.setColor(new Color(255, 255, 255, 0));
	g.fillRect(0, 0, 11, 11);
	g.setColor(Color.DARK_GRAY);
	g.drawLine(0, 5, 10, 5);
	g.drawRect(0, 0, 10, 10);
	
	table.getColumnModel().getColumn(0).setCellRenderer(new FirstColumnRenderer(model, b1, b2));
	table.addMouseListener(new FoldHandler(table));

	JFrame frame = new JFrame();
	frame.getContentPane().add(new JScrollPane(table));
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setVisible(true);

    }
}
