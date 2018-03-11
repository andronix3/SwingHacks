/**
 * 
 */
package com.smartg.swing.combobox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.smartg.swing.FixedListModel;

public class GCalendarPanel extends GComboBoxEditorPanel<String> {

	private static final long serialVersionUID = -8617553669929693130L;

	private static Calendar createCalendar(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar;
	}

	protected EventListenerList listenerList = new EventListenerList();

	private Calendar calendar;

	private String[] days = new String[7];
	private JList<String> cdays = new JList<String>(days);
	private JLabel current = new JLabel();

	private GoType goType = GoType.NoGo;

	private JPanel middlePanel = new JPanel(new BorderLayout());
	private JLabel nextMonth;
	private JLabel nextYear;

	private JLabel prevMonth;

	private JLabel prevYear;

	private Timer t = new Timer(200, new TimerHandler());
	private Box topBox;

	private String[] values = new String[35 + 7];

	private Map<Color, List<DateRangeChecker>> highlightMap = new LinkedHashMap<>();

	private boolean showSelection = true;

	private boolean dateChangeAllowed = true;

	public GCalendarPanel(Calendar cal) {
		this.calendar = cal;

		t.setInitialDelay(500);

		FixedListModel<String> model = new FixedListModel<String>(values);
		list.setModel(model);

		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(6);

		list.setFixedCellHeight(30);
		list.setFixedCellWidth(30);

		horizontalAlignment = SwingConstants.CENTER;
		drawGrid = false;

		cdays.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		cdays.setVisibleRowCount(1);

		cdays.setFixedCellHeight(30);
		cdays.setFixedCellWidth(30);

		CellRenderers.Selection_ListCellRenderer<String> cr2 = new CellRenderers.Selection_ListCellRenderer<String>();
		cr2.renderer.setHorizontalAlignment(SwingConstants.CENTER);
		cdays.setCellRenderer(cr2);

		updateValues();

		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dom = "" + day;
		list.setSelectedValue(dom, false);

		prevYear = new JLabel(new CalendarIcons.LeftArrow3());
		nextYear = new JLabel(new CalendarIcons.RightArrow3());

		prevYear.setOpaque(true);
		nextYear.setOpaque(true);

		prevYear.addMouseListener(new PrevYearListener());
		nextYear.addMouseListener(new NextYearListener());

		prevMonth = new JLabel(new CalendarIcons.LeftArrow2());
		nextMonth = new JLabel(new CalendarIcons.RightArrow2());

		prevMonth.setOpaque(true);
		nextMonth.setOpaque(true);

		prevMonth.addMouseListener(new PrevMonthListener());
		nextMonth.addMouseListener(new NextMonthListener());

		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (ignoreValueChanged) {
					return;
				}
				if (dateChangeAllowed) {
					String s = list.getSelectedValue();
					if (s != null && s.length() > 0) {
						int day = Integer.parseInt(s);
						GCalendarPanel.this.calendar.set(Calendar.DAY_OF_MONTH, day);
						setDate(calendar.getTime());
					}
				}
				else {
					ignoreValueChanged = true;
					list.setSelectedValue("" + GCalendarPanel.this.date.get(Calendar.DAY_OF_MONTH), false);
					ignoreValueChanged = false;
				}
			}
		});

		topBox = Box.createHorizontalBox();
		topBox.add(Box.createHorizontalStrut(5));
		topBox.add(prevYear);
		topBox.add(prevMonth);
		topBox.add(Box.createHorizontalGlue());
		topBox.add(current);
		topBox.add(Box.createHorizontalGlue());
		topBox.add(nextMonth);
		topBox.add(nextYear);
		topBox.add(Box.createHorizontalStrut(5));

		setLayout(new BorderLayout());

		add(topBox, BorderLayout.NORTH);
		add(middlePanel);
		middlePanel.add(list);
		middlePanel.add(cdays, BorderLayout.NORTH);
	}

	public boolean isDateChangeAllowed() {
		return dateChangeAllowed;
	}

	public void setDateChangeAllowed(boolean dateChangeAllowed) {
		this.dateChangeAllowed = dateChangeAllowed;
	}

	public GCalendarPanel(int year, int month, int day) {
		this(createCalendar(year, month, day));
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public Date getTime() {
		return calendar.getTime();
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	@Override
	public void setCellSize(int size) {
		super.setCellSize(size);
		cdays.setFixedCellWidth(size);
	}

	private Calendar date = Calendar.getInstance();

	public void setDate(Date date) {
		this.date.setTime(date);
		this.calendar.setTime(date);
		updateValues();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		list.setSelectedValue("" + day, false);
		fireChangeEvent();
	}

	public void addHighlightRange(DateRange range, Color color) {
		List<DateRangeChecker> list = highlightMap.get(color);
		if (list == null) {
			list = new ArrayList<>();
			highlightMap.put(color, list);
		}
		list.add(range);
	}

	public void removeHighlightRange(DateRangeChecker range, Color color) {
		List<? extends DateRangeChecker> list = highlightMap.get(color);
		list.remove(range);
	}

	protected void fireChangeEvent() {
		ChangeListener[] listeners = listenerList.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(this);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].stateChanged(e);
		}
	}

	public boolean isShowSelection() {
		return showSelection;
	}

	public void setShowSelection(boolean showSelection) {
		this.showSelection = showSelection;
	}

	protected Color getCellBackground(int x, int y, boolean selected) {
		int index = 7 * y + x;
		String s = list.getModel().getElementAt(index);
		if (s == null || s.isEmpty()) {
			return null;
		}
		if (isHover(x, y)) {
			if (hoverBG == null) {
				hoverBG = createHoverColor(getList().getSelectionBackground());
			}
			return hoverBG;
		}

		Color highlightColor = getHighlightColor(s);
		if (highlightColor != null) {
			return highlightColor;
		}
		if (selected && showSelection) {
			return list.getSelectionBackground();
		}
		return null;
	}

	public Color getHighlightColor(String s) {
		Set<Entry<Color, List<DateRangeChecker>>> entrySet = highlightMap.entrySet();
		int day = Integer.parseInt(s);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		for (Entry<Color, List<DateRangeChecker>> entry : entrySet) {
			List<DateRangeChecker> ranges = entry.getValue();
			for (DateRangeChecker range : ranges) {
				if (range.inRange(year, month, day)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public int getYear() {
		return date.get(Calendar.YEAR);
	}

	public int getMonth() {
		return date.get(Calendar.MONTH);
	}

	public int getDayOfMonth() {
		return date.get(Calendar.DAY_OF_MONTH);
	}

	private void goNextMonth() throws NumberFormatException {
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		month += 1;
		if (month > Calendar.DECEMBER) {
			month = Calendar.JANUARY;
			year++;
		}
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);

		updateValues();

		// String s = list.getSelectedValue();
		// if (s != null && s.length() > 0) {
		// int day = Integer.parseInt(s);
		// calendar.set(Calendar.DAY_OF_MONTH, day);
		// }

		list.repaint();
		fireChangeEvent();
	}

	private boolean sameYearAndMonth() {
		return (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
				&& date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH));
	}

	private void goNextYear() throws NumberFormatException {
		int year = calendar.get(Calendar.YEAR);

		year += 1;
		calendar.set(Calendar.YEAR, year);

		updateValues();

		// String s = list.getSelectedValue();
		// if (s != null && s.length() > 0) {
		// int day = Integer.parseInt(s);
		// calendar.set(Calendar.DAY_OF_MONTH, day);
		// }
		list.repaint();
		fireChangeEvent();
	}

	private void goPrevMonth() throws NumberFormatException {
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		month -= 1;
		if (month < Calendar.JANUARY) {
			month = Calendar.DECEMBER;
			year--;
		}
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);

		updateValues();

		// String s = list.getSelectedValue();
		// if (s != null && s.length() > 0) {
		// int day = Integer.parseInt(s);
		// calendar.set(Calendar.DAY_OF_MONTH, day);
		// }
		list.repaint();
		fireChangeEvent();
	}

	private void goPrevYear() throws NumberFormatException {
		int year = calendar.get(Calendar.YEAR);

		year -= 1;
		calendar.set(Calendar.YEAR, year);

		updateValues();

		// String s = list.getSelectedValue();
		// if (s != null && s.length() > 0) {
		// int day = Integer.parseInt(s);
		// calendar.set(Calendar.DAY_OF_MONTH, day);
		// }
		list.repaint();
		fireChangeEvent();
	}

	private boolean ignoreValueChanged;

	private void updateValues() {
		ignoreValueChanged = true;
		int firstDayOfWeek = calendar.getFirstDayOfWeek();
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.set(year, month, 1);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		calendar.set(year, month, day);

		for (int i = 0; i < values.length; i++) {
			values[i] = "";
		}

		Map<String, Integer> displayNames = calendar.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.SHORT,
				Locale.getDefault());

		Set<String> keySet = displayNames.keySet();
		Iterator<String> keys = keySet.iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Integer k = displayNames.get(key);
			int index = k - firstDayOfWeek;
			if (index < 0) {
				index += 7;
			}
			days[index] = key;
		}

		dayOfWeek -= firstDayOfWeek;
		if (dayOfWeek < 0) {
			dayOfWeek += 7;
		}

		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (int i = 0; i < daysInMonth; i++) {
			values[i + dayOfWeek] = "" + (i + 1);
		}

		current.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " "
				+ calendar.get(Calendar.YEAR));

		if (sameYearAndMonth()) {
			list.setSelectedValue("" + date.get(Calendar.DAY_OF_MONTH), false);
		} else {
			list.clearSelection();
		}
		ignoreValueChanged = false;
	}

	static enum GoType {
		NextMonth, NextYear, NoGo, PrevMonth, PrevYear
	}

	private class NextMonthListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			goNextMonth();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			nextMonth.setBackground(Color.orange);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			nextMonth.setBackground(null);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			goType = GoType.NextMonth;
			t.restart();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			goType = GoType.NoGo;
			t.stop();
		}
	}

	private class NextYearListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			goNextYear();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			nextYear.setBackground(Color.orange);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			nextYear.setBackground(null);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			goType = GoType.NextYear;
			t.restart();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			goType = GoType.NoGo;
			t.stop();
		}
	}

	private class PrevMonthListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			goPrevMonth();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			prevMonth.setBackground(Color.orange);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			prevMonth.setBackground(null);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			goType = GoType.PrevMonth;
			t.restart();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			goType = GoType.NoGo;
			t.stop();
		}
	}

	private class PrevYearListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			goPrevYear();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			prevYear.setBackground(Color.orange);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			prevYear.setBackground(null);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			goType = GoType.PrevYear;
			t.restart();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			goType = GoType.NoGo;
			t.stop();
		}
	}

	private final class TimerHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			switch (goType) {
			case NoGo:
				t.stop();
				break;
			case NextMonth:
				goNextMonth();
				break;
			case NextYear:
				goNextYear();
				break;
			case PrevMonth:
				goPrevMonth();
				break;
			case PrevYear:
				goPrevYear();
				break;
			}
		}
	}
}
