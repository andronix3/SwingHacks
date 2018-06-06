package com.smartg.swing.combobox;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;

public class GTextEditor extends GComboBoxEditor {

    private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();

    public GTextEditor(GTextEditorPanel ep) {
        super(ep);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public ListCellRenderer getRenderer() {
        return renderer;
    }
}