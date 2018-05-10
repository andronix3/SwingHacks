package com.smartg.swing.combobox;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import com.smartg.swing.BCLayout;

public class GComboBox2D_DataEditor<E> extends GComboBoxEditor<E> {

	private ListCellRenderer<E> renderer = new ListCellRenderer<E>() {
		DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

		public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
				boolean cellHasFocus) {
			return dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	};

	public GComboBox2D_DataEditor(String[] hlabels, String[] vlabels, ComboBoxModel<E> data) {
		super(new GDataPanel_2D<E>(hlabels, vlabels, data));
	}

	protected GComboBox2D_DataEditor(GDataPanel_2D<E> component) {
		super(component);
	}

	@Override
	public ListCellRenderer<E> getRenderer() {
		return renderer;
	}

	@Override
	public E getValue() {
		GComboBoxEditorListPanel<E> comp = (GComboBoxEditorListPanel<E>)component;
		return comp.getList().getSelectedValue();
	}

	protected static class GDataPanel_2D<E> extends GComboBoxEditorListPanel<E> {

		private static final long serialVersionUID = -2211273314619791045L;
		protected JList<String> hlist;
		protected JList<String> vlist;
		private int cellSize = 26;

		// setCellSize
		public GDataPanel_2D(String[] hlabels, String[] vlabels, ComboBoxModel<E> data) {
			setLayout(new BCLayout());

			hlist = new JList<String>(hlabels);
			vlist = new JList<String>(vlabels);
			list.setModel(data);

			CellRenderers.Selection_ListCellRenderer<String> cr1 = new CellRenderers.Selection_ListCellRenderer<String>();
			cr1.renderer.setHorizontalAlignment(SwingConstants.CENTER);
			hlist.setCellRenderer(cr1);

			CellRenderers.Selection_ListCellRenderer<String> cr2 = new CellRenderers.Selection_ListCellRenderer<String>();
			cr2.renderer.setHorizontalAlignment(SwingConstants.CENTER);
			vlist.setCellRenderer(cr2);

			setCellSize(cellSize);

			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			list.setVisibleRowCount(hlabels.length);

			vlist.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			vlist.setVisibleRowCount(vlabels.length);

			hlist.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			hlist.setVisibleRowCount(1);

			add(vlist, BCLayout.WEST);
			add(hlist, BCLayout.NORTH);
			add(list, BCLayout.CENTER);
		}

		public void setCellSize(int cellSize) {
			hlist.setFixedCellHeight(cellSize);
			hlist.setFixedCellWidth(cellSize);

			vlist.setFixedCellHeight(cellSize);
			vlist.setFixedCellWidth(cellSize);

			list.setFixedCellHeight(cellSize);
			list.setFixedCellWidth(cellSize);
		}
	}
}
