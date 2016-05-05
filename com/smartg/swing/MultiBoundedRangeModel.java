package com.smartg.swing;

import java.util.ArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MultiBoundedRangeModel allows programmer to show single JProcessBar for
 * multiple simultan running processes. Important note - dont use setter methods
 * in this class and in JProgressBar, use appropriate setters in each
 * BoundedRangeModel.
 * 
 * @author andro
 *
 */
public class MultiBoundedRangeModel implements BoundedRangeModel {

    private ArrayList<BoundedRangeModel> modelList = new ArrayList<BoundedRangeModel>();
    private ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
    
    private ChangeListener changeListener;

    private int minimum;
    private int maximum;

    public MultiBoundedRangeModel(BoundedRangeModel... models) {
	changeListener = new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		fireChangeEvent();
	    }
	};
	for (BoundedRangeModel m : models) {
	    m.addChangeListener(changeListener);
	    modelList.add(m);
	}
	compute();
    }

    public void addModel(BoundedRangeModel m) {
	if(!modelList.contains(m)) {
	    modelList.add(m);
	    m.addChangeListener(changeListener);
	    
	    compute();
	}
    }
    
    public void removeModel(BoundedRangeModel m) {
	if(m != null) {
	    m.removeChangeListener(changeListener);
	    modelList.remove(m);
	    
	    compute();
	}
    }
    
    public int getCount() {
	return modelList.size();
    }

    private void compute() {
	int min = 0;
	int max = 0;
	for (BoundedRangeModel m : modelList) {
	    min += m.getMinimum();
	    max += m.getMaximum();
	}
	minimum = min;
	maximum = max;
    }

    public int getMinimum() {
	return minimum;
    }

    public int getMaximum() {
	return maximum;
    }

    public int getValue() {
	int value = 0;
	for (BoundedRangeModel m : modelList) {
	    value += m.getValue();
	}
	return value;
    }

    public boolean getValueIsAdjusting() {
	for (BoundedRangeModel m : modelList) {
	    if (m.getValueIsAdjusting()) {
		return true;
	    }
	}
	return false;
    }

    public void addChangeListener(ChangeListener x) {
	if (!changeListeners.contains(x)) {
	    changeListeners.add(x);
	}
    }

    public void removeChangeListener(ChangeListener x) {
	changeListeners.remove(x);
    }

    private void fireChangeEvent() {
	ChangeEvent e = new ChangeEvent(this);
	for (ChangeListener c : changeListeners) {
	    c.stateChanged(e);
	}
    }

    // Unused methods

    public void setMinimum(int newMinimum) {

    }

    public void setMaximum(int newMaximum) {

    }

    public void setValue(int newValue) {
    }

    public void setValueIsAdjusting(boolean b) {

    }

    public int getExtent() {
	return 0;
    }

    public void setExtent(int newExtent) {

    }

    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {

    }
}