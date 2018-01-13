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

package com.smartg.swing.scroll.test;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;

import com.smartg.swing.gtable.GTableHorizontalValueVerifier;
import com.smartg.swing.gtable.GTableScrollBarToolTipTextSupplier;
import com.smartg.swing.gtable.GTableVerticalValueVerifier;
import com.smartg.swing.scroll.Actions;
import com.smartg.swing.scroll.GBoundedRangeModel;
import com.smartg.swing.scroll.GScrollPane;

/**
 * @author Andrey Kuznetsov
 */
public class GScrollPaneTest {

    public static void main(String[] args) {

        JTable table = new JTable(100, 100);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        final GScrollPane sp = new GScrollPane(table);

        sp.setToolTipTextProvider(new GTableScrollBarToolTipTextSupplier(sp, table));
        GBoundedRangeModel vmodel = (GBoundedRangeModel) sp.getVerticalScrollBar().getModel();
        GBoundedRangeModel hmodel = (GBoundedRangeModel) sp.getHorizontalScrollBar().getModel();
        vmodel.setVerifier(new GTableVerticalValueVerifier(table));
        hmodel.setVerifier(new GTableHorizontalValueVerifier(table));

        JMenu menu = new JMenu("Navigation");
        for (int i = 0; i < 5; i++) {
            menu.add(new JMenuItem("" + i));
        }

        final Action gsa = Actions.createGoStartAction();
        final Action gea = Actions.createGoEndAction();
        final Action pua = Actions.createPageUpAction();
        final Action pda = Actions.createPageDownAction();
        final Action a1 = Actions.createShowMenuAction();

        sp.addCustomAction(gsa);
        sp.addCustomAction(pua, true);
        sp.addCustomAction(a1);
        sp.addCustomAction(pda, true);
        sp.addCustomAction(gea);

        sp.setNavigationMenu(menu);

        sp.setContinuousScroll(true);

        JFrame frame = new JFrame("JTable with GScrollPane");
        frame.getContentPane().add(sp);
        frame.pack();
        frame.setVisible(true);
    }

}
