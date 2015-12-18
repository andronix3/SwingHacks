package com.smartg.swing.splitpane;

/**
 * @author Andrey Kuznetsov
 */
class Bounds {
    int nearestMin, nearestMax;
    int minValue, maxValue;

    public Bounds(int value, int nearestMin, int nearestMax) {
        this(value, value, nearestMin, nearestMax);
    }

    public Bounds(int minValue, int maxValue, int nearestMin, int nearestMax) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.nearestMax = nearestMax;
        this.nearestMin = nearestMin;
    }

    public void addMin(int min) {
        if(min > nearestMin) {
            nearestMin = min;
        }
        if(nearestMin >= minValue) {
            nearestMin = minValue - 1;
        }
    }

    public void addMax(int max) {
        if(max < nearestMax) {
            nearestMax = max;
        }
        if(nearestMax <= maxValue) {
            nearestMax = maxValue + 1;
        }
    }

    public void add(int v) {
        if (v > this.minValue) {
            if (v < nearestMax) {
                nearestMax = v;
            }
        }
        else {
            if (v > nearestMin) {
                nearestMin = v;
            }
        }
    }
}
