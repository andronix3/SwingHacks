/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Andrey Kuznetsov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.smartg.swing.scroll;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.EventListenerList;

/**
 * @author Andrey Kuznetsov
 */
public class GBoundedRangeModel extends DefaultBoundedRangeModel {

    private static final long serialVersionUID = -509692143468235299L;

    private int adjustingValue;
    private int adjustingExtent;
    private int adjustingMin;
    private int adjustingMax;

    private boolean adjusted;

    private boolean fireChangesWhileAdjusting = true;

    protected transient AdjustingChangeEvent adjustingChangeEvent;
    protected EventListenerList listenerList2 = new EventListenerList();

    private ValueVerifier verifier;

    public boolean isFireChangesWhileAdjusting() {
	return fireChangesWhileAdjusting;
    }

    public void setFireChangesWhileAdjusting(boolean b) {
	this.fireChangesWhileAdjusting = b;
    }

    public int getAdjustingValue() {
	return adjustingValue;
    }

    public int getAdjustingExtent() {
	return adjustingExtent;
    }

    public int getAdjustingMin() {
	return adjustingMin;
    }

    public int getAdjustingMax() {
	return adjustingMax;
    }

    public ValueVerifier getVerifier() {
	return verifier;
    }

    public void setVerifier(ValueVerifier verifier) {
	this.verifier = verifier;
    }

    public void setRangeProperties(int newValue, int newExtent, int newMin, int newMax, boolean adjusting) {
	boolean valueIsAdjusting = getValueIsAdjusting();

	if (verifier != null) {
	    newValue = verifier.verify(newValue);
	}

	if (!fireChangesWhileAdjusting) {
	    if (valueIsAdjusting && adjusting) {
		adjustingValue = newValue;
		adjustingExtent = newExtent;
		adjustingMin = newMin;
		adjustingMax = newMax;
		adjusted = true;
		fireAdjustingStateChanged();
		return;
	    }

	    if (!valueIsAdjusting || adjusting != valueIsAdjusting) {
		if (adjusted) {
		    super.setRangeProperties(adjustingValue, adjustingExtent, adjustingMin, adjustingMax, adjusting);
		    adjusted = false;
		} else {
		    super.setRangeProperties(newValue, newExtent, newMin, newMax, adjusting);
		}
	    }
	} else {
	    super.setRangeProperties(newValue, newExtent, newMin, newMax, adjusting);
	}
    }

    /**
     * Adds a ChangeListener for change events fired while valueIsAdjusting is
     * true and fireChangesWhileAdjusting is false.
     * 
     * @param l
     *            the ChangeListener to add
     */
    public void addAdjustingChangeListener(AdjustingChangeListener l) {
	listenerList2.add(AdjustingChangeListener.class, l);
    }

    /**
     * Removes a AdjustingChangeListener.
     * 
     * @param l
     *            the AdjustingChangeListener to remove
     * @see #addAdjustingChangeListener
     */
    public void removeAdjustingChangeListener(AdjustingChangeListener l) {
	listenerList2.remove(AdjustingChangeListener.class, l);
    }

    /**
     * Returns an array of all the adjusting change listeners registered on this
     * GBoundedRangeModel.
     * 
     * @return all of this model's <code>AdjustingChangeListener</code>s or an
     *         empty array if no adjusting change listeners are currently
     *         registered
     * 
     * @see #addAdjustingChangeListener
     * @see #removeAdjustingChangeListener
     * 
     */
    public AdjustingChangeListener[] getAdjustingChangeListeners() {
	return listenerList2.getListeners(AdjustingChangeListener.class);
    }

    /**
     * Runs stateChanged() for each registered AdjustingChangeListener.
     */
    protected void fireAdjustingStateChanged() {
	if (adjustingChangeEvent == null) {
	    adjustingChangeEvent = new AdjustingChangeEvent(this);
	}
	AdjustingChangeListener[] listeners = getAdjustingChangeListeners();
	for (int i = 0; i < listeners.length; i++) {
	    listeners[i].stateChanged(adjustingChangeEvent);
	}
    }
}
