package com.smartg.swing.combobox;

import java.awt.Color;
import java.text.SimpleDateFormat;
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
		GCalendarPanel calendarPanel = ctrl.getComponent();
		calendarPanel.setSelectedBorderColor(Color.CYAN);
		Calendar calendar = Calendar.getInstance();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int add = dayOfWeek - Calendar.MONDAY;
		if (add < 0) {
			add += 7;
		}

		int dayInMillis = 24 * 60 * 60 * 1000;
		Date start = new Date(new Date().getTime() + 0 * dayInMillis);
		Date end = new Date(start.getTime() + 5 * dayInMillis);
		SimpleDateFormat format = new SimpleDateFormat("dd MMMM YYYY");
		System.out.println(format.format(start));
		System.out.println(format.format(end));
		
		DateRange range = new DateRange(start, end);
		range.setIncludeRangeStart(true);
		
		calendarPanel.addHighlightRange(range, Color.GRAY);
		calendarPanel.setCellSize(50);
		calendarPanel.setDateChangeAllowed(false);
		calendarPanel.select(end);
		
		frame.add(new GComboBox<String>(ctrl));
		frame.pack();
		frame.setVisible(true);

	}
}
