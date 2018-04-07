package com.smartg.swing.table;

import java.awt.Component;
import java.text.ParseException;
import java.util.logging.Level;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.smartg.java.util.StackTraceUtil;
import com.smartg.swing.NullMarginButton;
import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeConstraints;

public abstract class TableCellEditorWithButton extends DefaultCellEditor {

    private static final long serialVersionUID = 9197754785781921417L;
    private JPanel panel = new JPanel();
    private JButton button = new NullMarginButton();
    private JTextField textField;
    private boolean buttonContentAreaFilled = true;
    private boolean buttonBorderPainted = true;
    private boolean useValueForButton;

    public TableCellEditorWithButton(JTextField textField) {
        this(textField, 4);
    }

    public TableCellEditorWithButton(JTextField textField, int buttonAlignment) {
        this(textField, buttonAlignment, false);
    }

    public TableCellEditorWithButton(JTextField textField, int buttonAlignment, boolean editable) {
        super(textField);
        this.textField = textField;
        textField.setEditable(editable);
        LayoutNode.HorizontalNode root = new LayoutNode.HorizontalNode("root");
        JNodeLayout layout = new JNodeLayout(this.panel, root);

        this.panel.setLayout(layout);
        if (buttonAlignment == 4) {
            this.panel.add(textField, new NodeConstraints("root"));
            LayoutNode.HorizontalNode buttonNode = new LayoutNode.HorizontalNode("button");
            buttonNode.setHorizontalAlignment(NodeAlignment.RIGHT);
            root.add(buttonNode);
            this.panel.add(this.button, new NodeConstraints("button"));

            root.setHorizontalAlignment(NodeAlignment.RIGHT);
            layout.setHorizontalAlignment(textField, NodeAlignment.RIGHT);
            layout.setHorizontalAlignment(this.button, NodeAlignment.RIGHT);
        } else {
            LayoutNode.HorizontalNode buttonNode = new LayoutNode.HorizontalNode("button");
            buttonNode.setHorizontalAlignment(NodeAlignment.LEFT);
            root.add(buttonNode);
            this.panel.add(this.button, new NodeConstraints("button"));
            this.panel.add(textField, new NodeConstraints("root"));

            root.setHorizontalAlignment(NodeAlignment.LEFT);
            layout.setHorizontalAlignment(textField, NodeAlignment.LEFT);
            layout.setHorizontalAlignment(this.button, NodeAlignment.LEFT);
        }
        textField.setHorizontalAlignment(4);

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

        this.button.setContentAreaFilled(isButtonContentAreaFilled());
        this.button.setBorderPainted(isButtonBorderPainted());

        Icon buttonIcon = getButtonIcon();
        if (buttonIcon != null) {
            this.button.setIcon(buttonIcon);
        } else {
            String buttonText = getButtonText();
            if ((buttonText != null) && (!buttonText.isEmpty())) {
                this.button.setText(buttonText);
            } else {
                this.button.setText("...");
            }
        }
        this.button.setVisible(showButton(table, value, isSelected, row, column));
        if (isUseValueForButton(table, value, isSelected, row, column)) {
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

    public boolean isUseValueForButton(JTable table, Object value, boolean isSelected, int row, int column) {
        return this.useValueForButton;
    }

    public void setUseValueForButton(boolean useValueForButton) {
        this.useValueForButton = useValueForButton;
    }

    public boolean isButtonContentAreaFilled() {
        return this.buttonContentAreaFilled;
    }

    public boolean isButtonBorderPainted() {
        return this.buttonBorderPainted;
    }

    public void setButtonContentAreaFilled(boolean buttonContentAreaFilled) {
        this.buttonContentAreaFilled = buttonContentAreaFilled;
    }

    public void setButtonBorderPainted(boolean buttonBorderPainted) {
        this.buttonBorderPainted = buttonBorderPainted;
    }

    protected String getButtonText() {
        return null;
    }

    protected Icon getButtonIcon() {
        return null;
    }

    protected abstract boolean showButton(JTable table, Object value, boolean isSelected, int row,
            int column);

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
