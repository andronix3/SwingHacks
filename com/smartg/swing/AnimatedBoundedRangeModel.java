package com.smartg.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.smartg.function.impl.LinearInterpolation;

public class AnimatedBoundedRangeModel extends DefaultBoundedRangeModel {

    private static final long serialVersionUID = 3187063644968078181L;

    private BoundedRangeModel model;

    private JComponent component;

    static final int defaultMaxSteps = 20;

    private int maxIncrement = 10;
    private int zielValue;
    private int startValue;
    private int smin, smax, mitte, length;
    private int minIncrement = 3;
    private int increment;
    private LinearInterpolation interpol;
    private float[] input = new float[1];
    private float[] output = new float[1];
    private int maxSteps = 20;

    private static AnimatedBoundedRangeModel create(JComponent c, BoundedRangeModel model) {
	if (model instanceof AnimatedBoundedRangeModel) {
	    return (AnimatedBoundedRangeModel) model;
	}
	return new AnimatedBoundedRangeModel(c, model);
    }

    public static void setAnimated(JComponent c) {
	if (c instanceof JSlider) {
	    JSlider slider = (JSlider) c;
	    BoundedRangeModel model = slider.getModel();
	    if (!(model instanceof AnimatedBoundedRangeModel)) {
		slider.setModel(create(c, model));
	    }
	} else if (c instanceof JScrollBar) {
	    JScrollBar jsb = (JScrollBar) c;
	    BoundedRangeModel model = jsb.getModel();
	    if (!(model instanceof AnimatedBoundedRangeModel)) {
		jsb.setModel(create(c, model));
	    }
	} else if (c instanceof JScrollPane) {
	    JScrollPane jsp = (JScrollPane) c;
	    BoundedRangeModel model;

	    JScrollBar vsb = jsp.getVerticalScrollBar();

	    model = vsb.getModel();
	    if (!(model instanceof AnimatedBoundedRangeModel)) {
		vsb.setModel(create(vsb, model));
	    }

	    JScrollBar hsb = jsp.getHorizontalScrollBar();
	    model = hsb.getModel();
	    if (!(model instanceof AnimatedBoundedRangeModel)) {
		hsb.setModel(create(hsb, model));
	    }
	}
    }

    Timer t = new Timer(5, new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    increment();
	}
    });

    private void increment() {
	int value = getValue();
	if (maxIncrement == 0) {
	    activateSnap();
	    super.setValue(zielValue);
	    t.stop();
	    return;
	}
	int maximum = getMaximum();

	boolean b0 = (zielValue > startValue) && (value >= zielValue || value + getExtent() >= maximum);
	int minimum = getMinimum();
	boolean b1 = zielValue < startValue && (value <= zielValue || value <= minimum);
	boolean b3 = zielValue == startValue;
	if (b0 || b1 || b3) {
	    activateSnap();
	    setValueIsAdjusting(false);
	    super.setValue(zielValue);
	    t.stop();
	    return;
	}

	deactivateSnap();
	int abstand;
	if (value > mitte) {
	    abstand = smax - value;
	} else {
	    abstand = value - smin;
	}
	input[0] = abstand;
	interpol.compute(input, output);
	increment = Math.max(minIncrement, (int) output[0]);

	if (zielValue > value) {
	    super.setValue(value + increment);
	} else {
	    super.setValue(value - increment);
	}
    }

    public void breakAnimation() {
	maxIncrement = 0;
	if (t.isRunning()) {
	    increment();
	}
    }

    private void activateSnap() {
	if (model instanceof SnapBoundedRangeModel) {
	    SnapBoundedRangeModel sbr = (SnapBoundedRangeModel) model;
	    sbr.setSnapActive(true);
	}
    }

    private void deactivateSnap() {
	if (model instanceof SnapBoundedRangeModel) {
	    SnapBoundedRangeModel sbr = (SnapBoundedRangeModel) model;
	    sbr.setSnapActive(false);
	}
    }

    @Override
    public void setRangeProperties(int newValue, int newExtent, int newMin, int newMax, boolean adjusting) {
	model.setRangeProperties(newValue, newExtent, newMin, newMax, adjusting);
	super.setRangeProperties(model.getValue(), model.getExtent(), model.getMinimum(), model.getMaximum(), model.getValueIsAdjusting());
    }

    private AnimatedBoundedRangeModel(JComponent c, BoundedRangeModel model) {
	this.model = model;
	this.component = c;
	int min = model.getMinimum();
	int max = model.getMaximum();
	int extent = model.getExtent();
	int value = model.getValue();

	setMinimum(min);
	setMaximum(max);
	setExtent(extent);
	super.setValue(value);

	t.setRepeats(true);
    }

    public void setValue(int newValue) {
	newValue = checkNewValue(newValue);

	if (component instanceof JScrollBar) {
	    JScrollBar jsb = (JScrollBar) component;
	    BasicScrollBarUI ui = (BasicScrollBarUI) jsb.getUI();
	    if (ui.isThumbRollover()) {
		super.setValue(newValue);
		return;
	    }
	}

	int value = getValue();
	if (t.isRunning()) {
	    minIncrement = increment;

	    this.zielValue = newValue;

	    smin = Math.min(startValue, zielValue);
	    smax = Math.max(startValue, zielValue);
	    length = (smax - smin) / 2;
	    mitte = smin + length;
	    interpol = new LinearInterpolation(0, length, 1, maxIncrement);
	    return;
	}

	increment = 0;
	maxIncrement = Math.max(10, Math.abs(value - newValue) / maxSteps);
	minIncrement = Math.max(1, maxIncrement / 5);

	startValue = value;
	this.zielValue = newValue;
	smin = Math.min(startValue, zielValue);
	smax = Math.max(startValue, zielValue);
	length = (smax - smin) / 2;
	mitte = smin + length;
	interpol = new LinearInterpolation(0, length, 1, maxIncrement);
	t.setRepeats(true);
	t.restart();
    }

    public void resetMaxSteps() {
	maxSteps = defaultMaxSteps;
    }

    public int getMaxSteps() {
	return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
	this.maxSteps = maxSteps;
    }

    private int checkNewValue(int newValue) {
	int maximum = getMaximum();
	int extent = getExtent();
	if (newValue > maximum - extent) {
	    newValue = maximum - extent;
	}
	int minimum = getMinimum();
	if (newValue < minimum) {
	    newValue = minimum;
	}
	return newValue;
    }
    
    public static void main(String... s) {
	JPanel panel = new JPanel() {
	    private static final long serialVersionUID = -2039067092080043564L;

	    @Override
	    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = getWidth();
		int h = getHeight();

		GradientPaint gp = new GradientPaint(0, 0, Color.BLUE.darker(), w / 10, h / 10, Color.GREEN.darker(), true);

		((Graphics2D) g).setPaint(gp);
		g.fillRect(0, 0, w, h);
	    }
	};
	panel.setPreferredSize(new Dimension(6000, 4000));
	JScrollPane scrollPane = new JScrollPane(panel);
	AnimatedBoundedRangeModel.setAnimated(scrollPane);
	JFrame frame = new JFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.add(scrollPane);
	frame.setExtendedState(Frame.MAXIMIZED_BOTH);
	frame.setVisible(true);

    }
}
