package com.smartg.swing.combobox;

import java.util.Calendar;
import java.util.Date;

public class DateRange implements DateRangeChecker {
	private Date startDate;
	private Date endDate;

	public DateRange(Date from, Date to) {
		this.startDate = from;
		this.endDate = to;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/* (non-Javadoc)
	 * @see com.smartg.swing.combobox.DateRangeChecker#inRange(int, int, int)
	 */
	@Override
	public boolean inRange(int year, int month, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return inRange(c);
	}

	/* (non-Javadoc)
	 * @see com.smartg.swing.combobox.DateRangeChecker#inRange(java.util.Calendar)
	 */
	@Override
	public boolean inRange(Calendar c) {
		return inRange(c.getTime());
	}

	/* (non-Javadoc)
	 * @see com.smartg.swing.combobox.DateRangeChecker#inRange(java.util.Date)
	 */
	@Override
	public boolean inRange(Date time) {
		if (startDate.equals(time)) {
			return true;
		}
		if (endDate.equals(time)) {
			return true;
		}
		return startDate.before(time) && endDate.after(time);
	}

	@Override
	public String toString() {
		return "DateRange [from=" + startDate + ", to=" + endDate + "]";
	}

}