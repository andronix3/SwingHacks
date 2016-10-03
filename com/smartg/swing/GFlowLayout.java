package com.smartg.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * FlowLayout returns incorrect preferredSize, because it ignores width of
 * target Container, so targets parent can't compute correct height.
 * 
 * @author andro
 *
 */
public class GFlowLayout extends FlowLayout {

    private static final long serialVersionUID = 6046127399548228005L;

    public GFlowLayout() {
	super(LEFT);
    }

    public GFlowLayout(int align, int hgap, int vgap) {
	super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
	//
	// int width = target.getWidth();
	// int count = target.getComponentCount();
	// int pw = 0;
	// int ph = 0;
	// int maxHeight = 0;
	// int accumulatedWidth = 0;
	// int start = 0;
	// boolean firstOnLine = true;
	// for (int i = start; i < count; i++) {
	// if (!firstOnLine) {
	// accumulatedWidth += getHgap();
	// }
	// firstOnLine = false;
	// Component c = target.getComponent(i);
	// Dimension ps = c.getPreferredSize();
	// int w = ps.width;
	// accumulatedWidth += w;
	// System.out.println(width);
	// if (accumulatedWidth > width + 10) {
	// System.out.println("LineBreak " + maxHeight);
	// pw = Math.max(pw, accumulatedWidth);
	// accumulatedWidth = w;
	// ph += maxHeight;
	// ph += getVgap();
	// maxHeight = 0;
	// firstOnLine = true;
	//
	// } else {
	// maxHeight = Math.max(maxHeight, ps.height);
	// }
	// }
	// System.out.println(pw + " " + ph);
	// return new Dimension(pw, ph);

	Dimension ps = super.preferredLayoutSize(target);
	Insets insets = target.getInsets();

	int w = target.getWidth();
	w -= insets.left + insets.right + getHgap() * (target.getComponentCount() - 1);
	float m = ps.width / (float) w + 1;
	if (m > 1) {
	    ps.width = w;
	    ps.height = ps.height * Math.round(m + 0.5f);
	}
//	System.out.println(ps);
	return ps;
    }
}