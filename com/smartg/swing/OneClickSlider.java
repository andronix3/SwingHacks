package com.smartg.swing;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class OneClickSlider extends JSlider {

    private static final long serialVersionUID = 126154603745621466L;

    private SnapBoundedRangeModel zsModel;
    private int[] positions;

    public OneClickSlider() {
	this(50, 0, 100);
    }

    public OneClickSlider(int value, int min, int max) {
	super(new SnapBoundedRangeModel(value, 0, min, max));
	zsModel = (SnapBoundedRangeModel) sliderModel;
	replaceMouseListener();
    }

    @Override
    protected void setUI(ComponentUI newUI) {
	super.setUI(newUI);

	if (newUI instanceof BasicSliderUI) {
	    replaceMouseListener();
	}
    }

    private void replaceMouseListener() {
	final BasicSliderUI ui = (BasicSliderUI) getUI();

	MouseListener[] listeners = getMouseListeners();
	for (MouseListener l : listeners) {
	    removeMouseListener(l);
	}

	BasicSliderUI.TrackListener tl = ui.new TrackListener() {
	    @Override
	    public void mousePressed(MouseEvent e) {
		super.mousePressed(e);

		Point p = e.getPoint();
		int value;
		if (getOrientation() == SwingConstants.HORIZONTAL) {
		    value = ui.valueForXPosition(p.x);
		} else {
		    value = ui.valueForYPosition(p.y);
		}
		setValue2(zsModel.getNearestValue(value));
	    }

	    // disable check that will invoke scrollDueToClickInTrack
	    @Override
	    public boolean shouldScroll(int dir) {
		return false;
	    }
	};
	addMouseListener(tl);
    }

    private boolean valid;

    public void addStop(int value) {
	zsModel.add(value);
	valid = false;
    }

    public int nextStop() {
	int[] stops = zsModel.stops();
	Arrays.sort(stops);
	int value = getValue();

	int v = value;
	for (int i = 0; i < stops.length; i++) {
	    v = stops[i];
	    if (v > value) {
		break;
	    }
	}
	return v;
    }

    public int prevStop() {
	int[] stops = zsModel.stops();
	Arrays.sort(stops);
	int value = getValue();

	int v = value;
	for (int i = stops.length - 1; i >= 0; i--) {
	    v = stops[i];
	    if (v < value) {
		break;
	    }
	}
	return v;
    }

    public void removeStop(int value) {
	zsModel.remove(value);
	valid = false;
    }

    protected void setValue2(int value) {
	setValue(value);
    }

    public boolean isOpaque() {
	return false;
    }

    protected void paintComponent(Graphics g) {
	super.paintComponent(g);

	if (!valid) {
	    calculatePositions();
	}
	if (positions != null) {
	    for (int pos : positions) {
		int y = getHeight() / 2 - 4;
		g.fillRect(pos, y, 2, 7);

	    }
	}
    }

    private void calculatePositions() {
	valid = true;

	SliderUI ui = getUI();
	if (ui instanceof BasicSliderUI) {
	    BasicSliderUI bsui = (BasicSliderUI) ui;
	    int[] stops = zsModel.stops();
	    positions = new int[stops.length];
	    Arrays.sort(stops);
	    if (orientation == SwingConstants.HORIZONTAL) {
		int w = getWidth();
		int offset = 0;
		for (int i = 0; i < w; i++) {
		    int v = bsui.valueForXPosition(i);
		    int value = stops[offset];
		    if (v - value > 0) {
			positions[offset] = i;
			if (++offset >= stops.length) {
			    break;
			}
		    }
		}
	    }
	}
    }
}
