package com.smartg.swing.combobox;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateRange implements DateRangeChecker {

    private Date startDate;
    private Date endDate;

    private boolean includeRangeStart;
    private boolean includeRangeEnd;

    private final DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);

    public DateRange() {

    }

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

    public boolean isIncludeRangeStart() {
        return includeRangeStart;
    }

    public void setIncludeRangeStart(boolean includeRangeStart) {
        this.includeRangeStart = includeRangeStart;
    }

    public boolean isIncludeRangeEnd() {
        return includeRangeEnd;
    }

    public void setIncludeRangeEnd(boolean includeRangeEnd) {
        this.includeRangeEnd = includeRangeEnd;
    }

    /*
	 * (non-Javadoc)
	 * 
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

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.smartg.swing.combobox.DateRangeChecker#inRange(java.util.Calendar)
     */
    @Override
    public boolean inRange(Calendar c) {
        return inRange(c.getTime());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see com.smartg.swing.combobox.DateRangeChecker#inRange(java.util.Date)
     */
    @Override
    public boolean inRange(Date time) {
        if (startDate == null && endDate == null) {
            return false;
        }
        if (startDate == null) {
            return beforeOrEquals(time, endDate);
        }
        if (endDate == null) {
            return afterOrEquals(time, startDate);
        }
        return betweenOrEquals(time);
    }

    public boolean betweenOrEquals(Date time) {
        return beforeOrEquals(time, endDate) && afterOrEquals(time, startDate);
    }

    private boolean afterOrEquals(Date time, Date startDate) {
        return time.after(startDate) || (includeRangeStart && time.equals(startDate));
    }

    private boolean beforeOrEquals(Date time, Date endDate) {
        return time.before(endDate) || (includeRangeEnd && time.equals(endDate));
    }

    @Override
    public String toString() {
        String sd = "";
        String ed = "";
        if (startDate != null) {
            sd = "from=" + format.format(startDate);
        }
        if (endDate != null) {
            ed = "to=" + format.format(endDate);
        }
        return "DateRangeX [" + sd + " " + ed + "]";
    }

}
