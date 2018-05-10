package com.smartg.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.util.logging.Level;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.smartg.java.util.StackTraceUtil;
import com.smartg.swing.NullMarginButton;
import com.smartg.swing.layout.GridPanel;
import com.smartg.swing.layout.NodeAlignment;

public class TableCellEditorWithButton extends DefaultCellEditor {

    private static final long serialVersionUID = 9197754785781921417L;
    private GridPanel panel = new GridPanel(10);
    private JButton button = new NullMarginButton();
    private JTextField textField;
    
	private final RendererFunctionFactory functionFactory = new RendererFunctionFactory();

    public TableCellEditorWithButton(JTextField textField) {
        this(textField, SwingConstants.RIGHT);
    }

    public TableCellEditorWithButton(JTextField textField, int buttonAlignment) {
        this(textField, buttonAlignment, false);
    }

    public TableCellEditorWithButton(JTextField textField, int buttonAlignment, boolean editable) {
        super(textField);
        this.textField = textField;
        textField.setEditable(editable);
        panel.setHgap(0);
        panel.setVgap(0);
        panel.add(textField, 9);
        panel.add(button, 1);
        panel.setHorizontalAlignment(NodeAlignment.STRETCHED);

//        LayoutNode.GridNode root = (LayoutNode.GridNode) panel.getLayout().getRoot();
        //root.setMaxCellWidth(9, 50);

        textField.setHorizontalAlignment(SwingConstants.LEFT);

        this.editorComponent = this.panel;
        this.delegate = new MyEditorDelegate(textField);

        this.button.addActionListener(this.delegate);
        this.button.setRequestFocusEnabled(false);

        setClickCountToStart(1);		

        //textField.addFocusListener(new TableCellEditorWithButton.2(this, textField));
    }

    @Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.delegate.setValue(value);
        String string = String.valueOf(value);
        
		CellRendererParams params = new CellRendererParams().setTable(table).setValue(value).setSelected(isSelected)
				.setHasFocus(true).setRow(row).setColumn(column);


        this.button.setContentAreaFilled(isButtonContentAreaFilled(params));
        this.button.setBorderPainted(isButtonBorderPainted(params));

        Icon buttonIcon = getButtonIcon(params);
        if (buttonIcon != null) {
            this.button.setIcon(buttonIcon);
        } else {
            String buttonText = getButtonText(params);
            if ((buttonText != null) && (!buttonText.isEmpty())) {
                this.button.setText(buttonText);
            } else {
                this.button.setText("...");
            }
        }
        this.button.setVisible(showButton(params));
        this.textField.setEditable(getTextFieldEditable(params));
        if (isUseValueForButton(params)) {
            this.textField.setVisible(false);
            this.button.setText(string);
        } else {
            this.textField.setText(string);
            this.textField.setVisible(true);
        }
        if (isSelected) {
            this.panel.setBackground(table.getSelectionBackground());
        } else {
            this.panel.setBackground(table.getBackground());
        }
        return this.panel;
    }

	public RendererFunctionFactory getFunctionFactory() {
		return functionFactory;
	}

	public Color getBackgroundColor(CellRendererParams t) {
		return this.functionFactory.getBackgroundcolor().apply(t);
	}

	public boolean isUseValueForButton(CellRendererParams t) {
		return this.functionFactory.getUseValueForButton().apply(t);
	}

	public String getButtonText(CellRendererParams t) {
		return this.functionFactory.getButtonText().apply(t);
	}

	public Icon getButtonIcon(CellRendererParams t) {
		return this.functionFactory.getButtonIcon().apply(t);
	}

	public boolean isButtonContentAreaFilled(CellRendererParams t) {
		return this.functionFactory.getButtonContentAreaFilled().apply(t);
	}

	public boolean isButtonBorderPainted(CellRendererParams t) {
		return this.functionFactory.getButtonBorderPainted().apply(t);
	}

	public boolean showButton(CellRendererParams t) {
		return this.functionFactory.getShowButton().apply(t);
	}

    public boolean getTextFieldEditable(CellRendererParams t) {
    	return this.functionFactory.getTextFieldEditable().apply(t);
    }

    public JButton getButton() {
        return this.button;
    }

    class MyEditorDelegate extends DefaultCellEditor.EditorDelegate {

        private static final long serialVersionUID = 7028332554446975905L;

        MyEditorDelegate(JTextField textField) {
            value = textField;
        }

        @Override
		public void setValue(Object value) {
            textField.setText(value != null ? value.toString() : "");
        }

        @Override
		public Object getCellEditorValue() {
            if ((textField instanceof JFormattedTextField)) {
                try {
                    ((JFormattedTextField) textField).commitEdit();
                } catch (ParseException e) {
                	StackTraceUtil.log(Level.WARNING, e.getMessage());
                }
                Object value = ((JFormattedTextField) textField).getValue();
                if (value != null) {
                    return value;
                }
                return "";
            }
            return textField.getText();
        }
    }
}
