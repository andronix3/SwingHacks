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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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

enum GoType {
	PrevMonth, NextMonth, PrevYear, NextYear, NoGo
}

public class GComboBoxCalendarPanel extends GComboBoxEditorPanel<String> {

	private static final long serialVersionUID = -8617553669929693130L;

	private class NextMonthListener extends MouseAdapter {
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

		@Override
		public void mouseClicked(MouseEvent e) {
			goNextMonth();
		}
	}

	private class PrevMonthListener extends MouseAdapter {
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

		@Override
		public void mouseClicked(MouseEvent e) {
			goPrevMonth();
		}
	}

	private class PrevYearListener extends MouseAdapter {
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

		@Override
		public void mouseClicked(MouseEvent e) {
			goPrevYear();
		}
	}

	private class NextYearListener extends MouseAdapter {
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

		@Override
		public void mouseClicked(MouseEvent e) {
			goNextYear();
		}
	}

	GoType goType = GoType.NoGo;

	Timer t = new Timer(200, new ActionListener() {
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
	});

	Calendar cal;
	Box topBox;

	String[] values = new String[35 + 7];
	String[] days = new String[7];

	JList<String> cdays = new JList<String>(days);

	JLabel prevMonth;
	JLabel nextMonth;

	JLabel prevYear;
	JLabel nextYear;

	JLabel current = new JLabel();
	JPanel middlePanel = new JPanel(new BorderLayout());

	EventListenerList listenerList = new EventListenerList();

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	void fireChangeEvent() {
		ChangeListener[] listeners = listenerList.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(this);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].stateChanged(e);
		}
	}
	
	public GComboBoxCalendarPanel(int year, int month, int day) {
		this(createCalendar(year, month, day));
	}

	private static Calendar createCalendar(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar;
	}

	public GComboBoxCalendarPanel(Calendar cal) {
		this.cal = cal;

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
		cr2.renderer.setHorizontalAlignment(SwingConstants.RIGHT);
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
				String s = list.getSelectedValue();
				if (s.length() > 0) {
					int day = Integer.parseInt(s);
					GComboBoxCalendarPanel.this.cal.set(Calendar.DAY_OF_MONTH, day);
					fireChangeEvent();
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

	public void setDate(Date date) {
		cal.setTime(date);
		updateValues();
	}

	private void goNextMonth() throws NumberFormatException {
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);

		month += 1;
		if (month > Calendar.DECEMBER) {
			month = Calendar.JANUARY;
			year++;
		}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);

		updateValues();

		String s = list.getSelectedValue();
		if (s != null && s.length() > 0) {
			int day = Integer.parseInt(s);
			cal.set(Calendar.DAY_OF_MONTH, day);
		}

		list.repaint();
		fireChangeEvent();
	}

	protected Color getCellBackground(int x, int y, boolean selected) {
		int index = 7 * y + x;
		String s = list.getModel().getElementAt(index);
		if (s == null || s.isEmpty()) {
			return null;
		}
		if (!selected && isHover(x, y)) {
			if (hoverBG == null) {
				hoverBG = createHoverColor(getList().getSelectionBackground());
			}
			return hoverBG;
		}
		return null;
	}

	private void goPrevMonth() throws NumberFormatException {
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);

		month -= 1;
		if (month < Calendar.JANUARY) {
			month = Calendar.DECEMBER;
			year--;
		}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);

		updateValues();

		String s = list.getSelectedValue();
		if (s != null && s.length() > 0) {
			int day = Integer.parseInt(s);
			cal.set(Calendar.DAY_OF_MONTH, day);
		}
		list.repaint();
		fireChangeEvent();
	}

	private void goPrevYear() throws NumberFormatException {
		int year = cal.get(Calendar.YEAR);

		year -= 1;
		cal.set(Calendar.YEAR, year);

		updateValues();

		String s = list.getSelectedValue();
		if (s != null && s.length() > 0) {
			int day = Integer.parseInt(s);
			cal.set(Calendar.DAY_OF_MONTH, day);
		}
		list.repaint();
		fireChangeEvent();
	}

	private void goNextYear() throws NumberFormatException {
		int year = cal.get(Calendar.YEAR);

		year += 1;
		cal.set(Calendar.YEAR, year);

		updateValues();

		String s = list.getSelectedValue();
		if (s != null && s.length() > 0) {
			int day = Integer.parseInt(s);
			cal.set(Calendar.DAY_OF_MONTH, day);
		}
		list.repaint();
		fireChangeEvent();
	}

	private void updateValues() {

		int firstDayOfWeek = cal.getFirstDayOfWeek();
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		cal.set(year, month, 1);
		int dow = cal.get(Calendar.DAY_OF_WEEK);

		cal.set(year, month, day);

		for (int i = 0; i < values.length; i++) {
			values[i] = "";
		}

		Map<String, Integer> displayNames = cal.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.SHORT,
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

		dow -= firstDayOfWeek;
		if (dow < 0) {
			dow += 7;
		}

		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (int i = 0; i < daysInMonth; i++) {
			values[i + dow] = "" + (i + 1);
		}

		current.setText(
				cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " " + cal.get(Calendar.YEAR));
	}
}