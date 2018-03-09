package com.smartg.swing.combobox;

import java.util.Calendar;
import java.util.Date;

public interface DateRangeChecker {

	boolean inRange(int year, int month, int dayOfMonth);

	boolean inRange(Calendar c);

	boolean inRange(Date time);

}