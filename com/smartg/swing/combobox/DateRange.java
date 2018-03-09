package com.smartg.swing.combobox;

import java.util.Calendar;
import java.util.Date;

public class DateRange {
	private final Date from;
	private final Date to;

	public DateRange(Date from, Date to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}
	
	public boolean inRange(int year, int month, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return inRange(c);
	}

	public boolean inRange(Calendar c) {
		return inRange(c.getTime());
	}

	public boolean inRange(Date time) {
		if(from.equals(time)) {
			return true;
		}
		if(to.equals(time)) {
			return true;
		}
		return from.before(time) && to.after(time);
	}	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DateRange other = (DateRange) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DateRange [from=" + from + ", to=" + to + "]";
	}

}