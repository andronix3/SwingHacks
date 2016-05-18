package com.smartg.swing;

import java.util.ArrayList;

import javax.swing.DefaultBoundedRangeModel;

public class SnapBoundedRangeModel extends DefaultBoundedRangeModel {

	private static final long serialVersionUID = 168350236213286614L;

	private ArrayList<Integer> snapList = new ArrayList<Integer>();
	private int threshold = 7;

	private boolean snapActive = true;

	public SnapBoundedRangeModel() {
		super();
	}

	public SnapBoundedRangeModel(int value, int extent, int min, int max) {
		super(value, extent, min, max);
	}

	public void add(int v) {
		if (!snapList.contains(v)) {
			snapList.add(v);
		}
	}

	public void add(int... snapPoints) {
		for (int n : snapPoints) {
			if (!snapList.contains(n)) {
				snapList.add(n);
			}
		}
	}

	public void remove(int v) {
		snapList.remove(new Integer(v));
	}

	public int[] stops() {
		int[] res = new int[snapList.size()];
		int p = 0;
		for (int a : snapList) {
			res[p++] = a;
		}

		return res;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	@Override
	public void setValue(int n) {
		if (snapActive) {
			for (int a : snapList) {
				if (Math.abs(a - n) < threshold) {
					n = a;
					break;
				}
			}
		}
		super.setValue(n);
	}

	public boolean isSnapActive() {
		return snapActive;
	}

	public void setSnapActive(boolean snapActive) {
		this.snapActive = snapActive;
	}

	public int getNearestValue(int value) {
		if (snapList.size() == 0) {
			return value;
		}
		int nv = snapList.get(0);
		int diff = Math.abs(nv - value);
		for (int a : snapList) {
			int d = Math.abs(a - value);
			if (d < diff) {
				diff = d;
				nv = a;
			}
		}
		return nv;
	}
}
