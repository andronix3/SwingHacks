/*
 * Copyright (c) Andrei Kouznetsov. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * o Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * o Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * o Neither the name of imagero Andrei Kouznetsov nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.smartg.swing.gtable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.smartg.swing.BCLayout;
import com.smartg.swing.scroll.GBoundedRangeModel;
import com.smartg.swing.scroll.GScrollPane;
import com.smartg.swing.splitpane.Divider;
import com.smartg.swing.splitpane.GSplitPane;
import com.smartg.swing.splitpane.Node;
import com.smartg.swing.splitpane.SplitConstants;
import com.smartg.swing.splitpane.SplitPaneConstraints;
import com.smartg.swing.splitpane.SplitPaneContainer;
import com.smartg.swing.splitpane.SynchronizedSplitPane;

public class GTableView extends JPanel {

	private static final long serialVersionUID = 1L;
	Container split;
	TableModel model;

	public GTableView(TableModel model) {
		super(new BCLayout());
		this.model = model;
		createGUI();
	}

	public static void main(String[] args) {
		int rows = 200;
		int cols = 100;

		TableModel model = new DefaultTableModel(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String columnName = model.getColumnName(j);
				model.setValueAt(columnName + " " + i, i, j);
			}
		}

		GTableView test = new GTableView(model);

		JMenuBar menuBar;
		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exit);
		menuBar.add(fileMenu);

		JFrame owner = new JFrame("Lightweight SynchronizedSplitPane demo");
		owner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		owner.getContentPane().add(test);
		owner.setJMenuBar(menuBar);
		owner.setBounds(0, 100, 800, 600);
		owner.setVisible(true);
	}

	TableColumnModel columnModel;
	ListSelectionModel selectionModel;

	private void createGUI() {

		JScrollPane[] scrollPanels = new JScrollPane[4];

		final Container top = GSplitPane.createLightweightSplitPane();

		for (int i = 0; i < scrollPanels.length; i++) {
			JTable view = initTable();

			GScrollPane gsp;
			if (i > 1) {
				gsp = new GScrollPane(view, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) {

					private static final long serialVersionUID = -906683085204407973L;

					@Override
					public void setColumnHeaderView(Component view) {

					}
				};
			} else {
				gsp = new GScrollPane(view, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			}

			gsp.setToolTipTextProvider(new GTableScrollBarToolTipTextSupplier(gsp, view));

			GBoundedRangeModel vmodel = (GBoundedRangeModel) gsp.getVerticalScrollBar().getModel();
			GBoundedRangeModel hmodel = (GBoundedRangeModel) gsp.getHorizontalScrollBar().getModel();
			vmodel.setVerifier(new GTableVerticalValueVerifier(view));
			hmodel.setVerifier(new GTableHorizontalValueVerifier(view));
			scrollPanels[i] = gsp;
		}

		scrollPanels[0].getVerticalScrollBar().setModel(scrollPanels[1].getVerticalScrollBar().getModel());
		scrollPanels[2].getVerticalScrollBar().setModel(scrollPanels[3].getVerticalScrollBar().getModel());

		scrollPanels[0].getHorizontalScrollBar().setModel(scrollPanels[2].getHorizontalScrollBar().getModel());
		scrollPanels[1].getHorizontalScrollBar().setModel(scrollPanels[3].getHorizontalScrollBar().getModel());

		JScrollBar sb0 = new PScrollBar(scrollPanels[3].getVerticalScrollBar());
		JScrollBar sb1 = new PScrollBar(scrollPanels[0].getVerticalScrollBar());

		JScrollBar sb2 = new PScrollBar(scrollPanels[0].getHorizontalScrollBar());
		JScrollBar sb3 = new PScrollBar(scrollPanels[1].getHorizontalScrollBar());

		split = SynchronizedSplitPane.createLightweightSplitPane(scrollPanels);
		GSplitPane splitPane = ((SplitPaneContainer) split).getSplitPane();
		splitPane.setResizeWeight(0);

		final PTableHeader header2 = new PTableHeader(columnModel);
		JTable t2 = (JTable) scrollPanels[2].getViewport().getView();
		t2.setTableHeader(header2);

		final PTableHeader header3 = new PTableHeader(columnModel);
		JTable t3 = (JTable) scrollPanels[3].getViewport().getView();
		t3.setTableHeader(header3);

		JTable t1 = (JTable) scrollPanels[1].getViewport().getView();
		JTableHeader header1 = t1.getTableHeader();

		JTable t0 = (JTable) scrollPanels[0].getViewport().getView();
		JTableHeader header0 = t0.getTableHeader();

		header2.addTableHeader(header3);
		header2.addTableHeader(header1);
		header2.addTableHeader(header0);

		header3.addTableHeader(header2);
		header3.addTableHeader(header1);
		header3.addTableHeader(header0);

		Container right = GSplitPane.createLightweightSplitPane();
		Container bottom = GSplitPane.createLightweightSplitPane();

		GSplitPane rightSplit = ((SplitPaneContainer) right).getSplitPane();
		GSplitPane bottomSplit = ((SplitPaneContainer) bottom).getSplitPane();
		GSplitPane topSplit = ((SplitPaneContainer) top).getSplitPane();

		splitPane.setIgnorePreferredSize(true);
		rightSplit.setIgnorePreferredSize(true);
		bottomSplit.setIgnorePreferredSize(true);
		topSplit.setIgnorePreferredSize(true);

		right.add(sb0, new SplitPaneConstraints(null, SplitConstants.VERTICAL_SPLIT, SplitConstants.ALIGN_TOP));
		right.add(sb1, new SplitPaneConstraints(sb0, SplitConstants.VERTICAL_SPLIT, SplitConstants.ALIGN_BOTTOM));

		bottom.add(sb2, new SplitPaneConstraints(null, SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_RIGHT));
		bottom.add(sb3, new SplitPaneConstraints(sb2, SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));

		GScrollPane gsp2 = new GScrollPane(header2, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GScrollPane gsp3 = new GScrollPane(header3, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		gsp2.getHorizontalScrollBar().setModel(scrollPanels[2].getHorizontalScrollBar().getModel());
		gsp3.getHorizontalScrollBar().setModel(scrollPanels[3].getHorizontalScrollBar().getModel());

		top.add(gsp2, new SplitPaneConstraints(null, SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_RIGHT));
		top.add(gsp3, new SplitPaneConstraints(gsp2, SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));

		Divider splitPaneDivider = splitPane.getNode(null).getDivider();
		Divider splitPaneLeftDivider = splitPane.getNode(null).getLeft().getDivider();
		Divider splitPaneRightDivider = splitPane.getNode(null).getRight().getDivider();

		Divider bottomDivider = bottomSplit.getNode(null).getDivider();
		Divider topDivider = topSplit.getNode(null).getDivider();
		final Divider rightDivider = rightSplit.getNode(null).getDivider();

		bottomDivider.addActionListener(new Node.ProxyActionListener(splitPaneDivider.getActionListener()));
		topDivider.addActionListener(new Node.ProxyActionListener(splitPaneDivider.getActionListener()));
		rightDivider.addActionListener(new Node.ProxyActionListener(splitPaneLeftDivider.getActionListener()));
		rightDivider.addActionListener(new Node.ProxyActionListener(splitPaneRightDivider.getActionListener()));

		splitPaneDivider.addActionListener(new Node.ProxyActionListener(bottomDivider.getActionListener()));
		splitPaneDivider.addActionListener(new Node.ProxyActionListener(topDivider.getActionListener()));
		splitPaneLeftDivider.addActionListener(new Node.ProxyActionListener(rightDivider.getActionListener()));
		splitPaneRightDivider.addActionListener(new Node.ProxyActionListener(rightDivider.getActionListener()));

		topDivider.addActionListener(new Node.ProxyActionListener(bottomDivider.getActionListener()));
		bottomDivider.addActionListener(new Node.ProxyActionListener(topDivider.getActionListener()));

		// rightDivider.getActionListener().actionPerformed(new ActionEvent(new
		// Point(5, 5), ActionEvent.ACTION_PERFORMED, "mouseReleased"));

		rightSplit.setResizeWeight(-1);
		bottomSplit.setResizeWeight(-1);
		topSplit.setResizeWeight(-1);
		splitPane.setResizeWeight(-1);

		add(right, BorderLayout.EAST);
		add(bottom, BorderLayout.SOUTH);
		add(top, BorderLayout.NORTH);
		add(split);
	}

	JTable initTable() {
		JTable view = createTable();

		view.setCellSelectionEnabled(true);
		if (selectionModel == null) {
			selectionModel = view.getSelectionModel();
			selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			view.setSelectionModel(selectionModel);
		}

		if (columnModel == null) {
			columnModel = view.getColumnModel();
		} else {
			view.setColumnModel(columnModel);
		}

		view.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		view.getTableHeader().setPreferredSize(new Dimension(0, 0));
		return view;
	}

	protected GTable createTable() {
		return new GTable(model);
	}

	static class PScrollBar extends JScrollBar {

		private static final long serialVersionUID = 1L;
		JScrollBar sb;

		public PScrollBar(JScrollBar sb) {
			this.sb = sb;
			setOrientation(sb.getOrientation());
			setModel(sb.getModel());
		}

		@Override
		public int getUnitIncrement(int direction) {
			return sb.getUnitIncrement(direction);
		}

		@Override
		public int getBlockIncrement(int direction) {
			return sb.getBlockIncrement(direction);
		}
	}

	static class PTableHeader extends JTableHeader {

		private static final long serialVersionUID = 1L;
		JTableHeader[] tableHeaders = new JTableHeader[0];

		public PTableHeader(TableColumnModel cm) {
			super(cm);
		}

		public void addTableHeader(JTableHeader header) {
			if (header == null) {
				throw new NullPointerException();
			} else if (header.equals(this)) {
				throw new IllegalArgumentException();
			}
			JTableHeader[] ths = new JTableHeader[tableHeaders.length + 1];
			for (int i = 0; i < tableHeaders.length; i++) {
				ths[i] = tableHeaders[i];
			}
			ths[tableHeaders.length] = header;
			tableHeaders = ths;
		}

		public JTableHeader removeTableHeader(JTableHeader header) {
			if (header == null) {
				return null;
			}
			for (int i = 0; i < tableHeaders.length; i++) {
				if (header.equals(tableHeaders[i])) {
					return removeTableHeader(i);
				}
			}
			return null;
		}

		public JTableHeader removeTableHeader(int headerIndex) {
			// no check - ArrayIndexOutOfBoundsException thrown here
			JTableHeader header = tableHeaders[headerIndex];
			JTableHeader[] ths = new JTableHeader[tableHeaders.length - 1];
			for (int i = 0, j = 0; i < tableHeaders.length; i++) {
				if (i != headerIndex) {
					ths[j++] = tableHeaders[i];
				}
			}
			tableHeaders = ths;
			return header;
		}

		@Override
		public void setDraggedDistance(int distance) {
			if (draggedDistance != distance) {
				super.setDraggedDistance(distance);
				for (JTableHeader friend : tableHeaders) {
					if (friend.getDraggedDistance() != distance) {
						friend.setDraggedDistance(distance);
					}
				}
			}
		}

		@Override
		public void setDraggedColumn(TableColumn column) {
			if (draggedColumn != column) {
				super.setDraggedColumn(column);
				for (JTableHeader friend : tableHeaders) {
					if (friend.getDraggedColumn() != column) {
						friend.setDraggedColumn(column);
					}
				}
			}
		}
	}
}
