package com.smartg.swing.gtable;

import java.awt.Point;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JTable;

import com.smartg.swing.scroll.GBoundedRangeModel;
import com.smartg.swing.scroll.GScrollBarToolTipTextProvider;
import com.smartg.swing.scroll.GScrollPane;

/**
 * @author Andrey Kuznetsov
 */
public class GTableScrollBarToolTipTextSupplier implements GScrollBarToolTipTextProvider {
    private final GScrollPane sp;
    private final JTable pane;

    public GTableScrollBarToolTipTextSupplier(GScrollPane sp, JTable pane) {
	this.sp = sp;
	this.pane = pane;
    }

    public String getTooTipText(JScrollBar sb) {
	if (sb == sp.getVerticalScrollBar()) {
	    BoundedRangeModel m = sb.getModel();
	    if (m instanceof GBoundedRangeModel) {
		GBoundedRangeModel model = (GBoundedRangeModel) m;
		if (model.getValueIsAdjusting() && !model.isFireChangesWhileAdjusting()) {
		    String s = "" + pane.rowAtPoint(new Point(10, model.getAdjustingValue()));
		    System.out.println(s);
		    return s;
		}
	    }
	}
	return null;
    }
}
