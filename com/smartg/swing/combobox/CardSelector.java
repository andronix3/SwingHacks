package com.smartg.swing.combobox;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;

public class CardSelector extends GComboBox2D_DataEditor<String> {

	static final String[] labels = { "A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2" };

	private static String[] create() {
		int w = labels.length;
		String[] cards = new String[w * w];
		for (int y = 0; y < w; y++) {
			for (int x = 0; x < w; x++) {
				if (x > y) {
					cards[x + y * w] = labels[y] + labels[x] + "o";
				} else if (x < y) {
					cards[x + y * w] = labels[x] + labels[y] + "s";
				} else {
					cards[x + y * w] = labels[x] + labels[y];
				}
			}
		}
		return cards;
	}

	static final Color suitedBG = new Color(0xFFFFF2BF);
	static final Color suitedFG = Color.BLACK;
	static final Color nosuitBG = new Color(0xFFBFFFFF);
	static final Color nosuitFG = Color.BLACK;
	static final Color pairsBG = new Color(0xFF3399FF);
	static final Color pairsFG = Color.WHITE;

	public CardSelector() {
		super(new CardSelectorPanel(labels, labels, new DefaultComboBoxModel<String>(create())));
	}

	static class CardSelectorPanel extends GComboBox2D_DataEditor.GDataPanel_2D<String> {

		private static final long serialVersionUID = 1L;

		public CardSelectorPanel(String[] hlabels, String[] vlabels, ComboBoxModel<String> data) {
			super(hlabels, vlabels, data);
		}

		@Override
		protected Color getCellBackground(int x, int y, boolean selected) {
			if (selected) {
				return null;
			}

			if (isHover(x, y)) {
				if (hoverBG == null) {
					hoverBG = createHoverColor(getList().getSelectionBackground());
				}
				return hoverBG;
			}
			if (x == y) {
				return pairsBG;
			} else if (x > y) {
				return nosuitBG;
			} else {
				return suitedBG;
			}
		}

		@Override
		protected Color getCellForeground(int x, int y, boolean selected) {
			if (selected) {
				return null;
			}
			if (isHover(x, y)) {
				return null;
			}
			if (x == y) {
				return pairsFG;
			} else if (x > y) {
				return nosuitFG;
			} else {
				return suitedFG;
			}
		}
	}

	public static void main(String... args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new GComboBox<String>(new CardSelector()));
		GComboBoxCalendarEditor ctrl = new GComboBoxCalendarEditor();
		GComboBoxCalendarPanel component = ctrl.getComponent();
		component.setHighlightColorForRange(new DateRange(new Date(), new Date(new Date().getTime() + 5 * 24 * 60 * 60 * 1000)), Color.GRAY);
		component.setCellSize(50);
		frame.add(new GComboBox<String>(ctrl));
		frame.pack();
		frame.setVisible(true);

	}
}
