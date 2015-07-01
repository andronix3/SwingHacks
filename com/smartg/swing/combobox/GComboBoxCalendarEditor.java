package com.smartg.swing.combobox;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class GComboBoxCalendarEditor extends GComboBoxEditor {

    DateFormat format = DateFormat.getDateInstance();
    Renderer renderer = new Renderer();

    public GComboBoxCalendarEditor() {
	super(new GComboBoxCalendarPanel(Calendar.getInstance()));
    }
    
    @Override
    public ListCellRenderer getRenderer() {
	return renderer;
    }

    @Override
    public Object getValue() {
	return getTime();
    }

    protected Date getTime() {
	GComboBoxCalendarPanel panel = (GComboBoxCalendarPanel)getComponent();
	String s = panel.list.getSelectedValue();
	if(s != null && !s.isEmpty()) {
	    return panel.cal.getTime();
	}
	return null;
    }

    class Renderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 5036330073179253078L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	    JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	    Date time = getTime();
	    if(time != null) {
		c.setText(format.format(time));
	    }
	    else {
		c.setText("");
	    }
	    return c;
	}
    }
}
