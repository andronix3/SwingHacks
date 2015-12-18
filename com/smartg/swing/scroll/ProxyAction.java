/*
 * Copyright (c) Andrey Kuznetsov. All Rights Reserved.
 *
 * http://jgui.imagero.com
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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

/**
 * Gives the possibility to change Action
 * a) without notifying.
 * b) for any number of buttons
 * @author Andrey Kuznetsov
 */
public class ProxyAction implements Action {
    protected Action action;

    public ProxyAction(Action a) {
        this.action = a;
    }

    public Object getValue(String key) {
        if (action != null) {
            return action.getValue(key);
        }
        return null;
    }

    public void putValue(String key, Object value) {
        if (action != null) {
            action.putValue(key, value);
        }
    }

    public void setEnabled(boolean b) {
        if (action != null) {
            action.setEnabled(b);
        }
    }

    public boolean isEnabled() {
        if (action != null) {
            return action.isEnabled();
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) {
        if (action != null) {
            action.actionPerformed(e);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (action != null) {
            action.addPropertyChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (action != null) {
            action.removePropertyChangeListener(listener);
        }
    }

    public boolean equals(Object obj) {
        if (action != null) {
            if (obj instanceof ProxyAction) {
                ProxyAction proxy = (ProxyAction) obj;
                return action.equals(proxy.action);
            }
            else if (obj instanceof Action) {
                return action.equals(obj);
            }
        }
        return false;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int hashCode() {
	return action.hashCode();
    }
}
