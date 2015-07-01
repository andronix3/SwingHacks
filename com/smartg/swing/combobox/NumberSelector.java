package com.smartg.swing.combobox;

import javax.swing.DefaultComboBoxModel;

public class NumberSelector extends GComboBox2D_DataEditor<String> {

    public NumberSelector() {
	super(labels, labels, new DefaultComboBoxModel<String>(create()));
    }

    static final String[] labels = { "1", "2", "3", "4", "5", "6", "7" };

    private static String[] create() {
	int w = labels.length;
	String[] cards = new String[w * w];
	int k = 1;
	for (int y = 0; y < w; y++) {
	    for (int x = 0; x < w; x++) {
		cards[x + y * w] = "" + k++;
	    }
	}
	return cards;
    }
}
