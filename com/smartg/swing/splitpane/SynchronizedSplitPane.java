package com.smartg.swing.splitpane;

import java.awt.Component;
import java.awt.Container;

/**
 * @author Andrey Kuznetsov
 */
public abstract class SynchronizedSplitPane extends GSplitPane {

    public static Container createLightweightSplitPane(Component[] comps) {

        if (comps.length != 4) {
            throw new IllegalArgumentException("array length should be 4!");
        }

        for (int i = 0; i < comps.length; i++) {
            if (comps[i] == null) {
                throw new NullPointerException();
            }
        }

        SynchronizedSplitPane gsp = new SynchronizedSplitPane() {
            protected void createContentPane() {
                container = new LightweightSplitPanel();
                ((LightweightSplitPanel) container).msp = this;
            }
        };
        gsp.getContainer().add(comps[0], new SplitPaneConstraints(null, SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));
        gsp.getContainer().add(comps[1], new SplitPaneConstraints(null, SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));
        gsp.getContainer().add(comps[2], new SplitPaneConstraints(comps[0], SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));
        gsp.getContainer().add(comps[3], new SplitPaneConstraints(comps[1], SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));

        try {
            gsp.getNode(null).setSynchronized(true);
        } catch (SynchronizationException ex) {
            ex.printStackTrace();
        }
        return gsp.getContainer();
    }

    public static Container createHeawyweightSplitPane(Component[] comps) {
        if (comps.length != 4) {
            throw new IllegalArgumentException("array length should be 4!");
        }

        for (int i = 0; i < comps.length; i++) {
            if (comps[i] == null) {
                throw new NullPointerException();
            }
        }

        SynchronizedSplitPane gsp = new SynchronizedSplitPane() {
            protected void createContentPane() {
                container = new HeavyweightSplitPanel();
                ((HeavyweightSplitPanel) container).msp = this;
            }
        };
        gsp.getContainer().add(comps[0], new SplitPaneConstraints(null, SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));
        gsp.getContainer().add(comps[1], new SplitPaneConstraints(null, SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));
        gsp.getContainer().add(comps[2], new SplitPaneConstraints(comps[0], SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));
        gsp.getContainer().add(comps[3], new SplitPaneConstraints(comps[1], SplitConstants.HORIZONTAL_SPLIT, SplitConstants.ALIGN_LEFT));

        try {
            gsp.getNode(null).setSynchronized(true);
        } catch (SynchronizationException ex) {
            ex.printStackTrace();
        }
        return gsp.getContainer();
    }
}
