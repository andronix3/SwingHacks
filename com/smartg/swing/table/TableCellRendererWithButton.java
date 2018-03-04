package com.smartg.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.smartg.swing.NullMarginButton;
import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;
import com.smartg.swing.layout.NodeConstraints;

public abstract class TableCellRendererWithButton implements TableCellRenderer {

    private final JComponent panel = new JLabel();
    private final StealthButton button = new StealthButton();
    private boolean buttonContentAreaFilled = true;
    private boolean buttonBorderPainted = true;
    private TableCellRenderer renderer;
    private final JComponent rendererPanel = new Box(BoxLayout.LINE_AXIS);
    private boolean useValueForButton;

    public TableCellRendererWithButton(TableCellRenderer renderer) {
        this(renderer, 4);
    }

    public TableCellRendererWithButton(TableCellRenderer renderer, int buttonAlignment) {
        this.renderer = renderer;
        LayoutNode.HorizontalNode root = new LayoutNode.HorizontalNode("root");
        JNodeLayout layout = new JNodeLayout(this.panel, root);

        this.panel.setLayout(layout);
        this.panel.setOpaque(true);
        if (buttonAlignment == 4) {
            this.panel.add(this.rendererPanel, new NodeConstraints("root"));
            LayoutNode.HorizontalNode buttonNode = new LayoutNode.HorizontalNode("button");
            buttonNode.setHorizontalAlignment(NodeAlignment.RIGHT);
            root.add(buttonNode);
            this.panel.add(this.button, new NodeConstraints("button"));
            layout.setHorizontalAlignment(this.rendererPanel, NodeAlignment.RIGHT);
            root.setHorizontalAlignment(NodeAlignment.RIGHT);
            layout.setHorizontalAlignment(this.button, NodeAlignment.RIGHT);
        } else {
            LayoutNode.HorizontalNode buttonNode = new LayoutNode.HorizontalNode("button");
            buttonNode.setHorizontalAlignment(NodeAlignment.LEFT);
            root.add(buttonNode);
            this.panel.add(this.button, new NodeConstraints("button"));

            this.panel.add(this.rendererPanel, new NodeConstraints("root"));
            layout.setHorizontalAlignment(this.rendererPanel, NodeAlignment.LEFT);
            root.setHorizontalAlignment(NodeAlignment.LEFT);
            layout.setHorizontalAlignment(this.button, NodeAlignment.LEFT);
        }
        layout.setHorizontalAlignment(this.button, NodeAlignment.RIGHT);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        String string = "";
        if (value != null) {
            string = value.toString();
        }
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
        this.button.setStealthMode(!showButton(table, string, isSelected, hasFocus, row, column));
        JLabel comp;
        if (isUseValueForButton(table, value, isSelected, row, column)) {
            comp = (JLabel) this.renderer.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            this.button.setText(string);
            comp.setVisible(false);
        } else {
            comp = (JLabel) this.renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
            comp.setVisible(true);
        }
        comp.setHorizontalTextPosition(4);
        comp.setHorizontalAlignment(4);

        comp.setOpaque(true);
        if (isSelected) {
            this.panel.setBackground(table.getSelectionBackground());
            comp.setBackground(table.getSelectionBackground());
            this.rendererPanel.setBackground(table.getSelectionBackground());
            comp.setBackground(table.getSelectionBackground());
        } else {
            comp.setBackground(table.getBackground());
            this.panel.setBackground(table.getBackground());
            this.rendererPanel.setBackground(table.getBackground());
            comp.setBackground(table.getBackground());
        }
        this.rendererPanel.add(comp, "Center");
        
        return this.panel;
    }

    public boolean isUseValueForButton(JTable table, Object value, boolean isSelected, int row, int column) {
        return this.useValueForButton;
    }

    public void setUseValueForButton(boolean useValueForButton) {
        this.useValueForButton = useValueForButton;
    }

    protected String getButtonText() {
        return null;
    }

    protected Icon getButtonIcon() {
        return null;
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

    protected abstract boolean showButton(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int col);

    public JButton getButton() {
        return this.button;
    }

    static class StealthButton extends NullMarginButton {

        private static final long serialVersionUID = -9008065882485328141L;
        private boolean stealthMode;

        public void paint(Graphics g) {
            if (!this.stealthMode) {
                super.paint(g);
            }
        }

        public boolean isStealthMode() {
            return this.stealthMode;
        }

        public void setStealthMode(boolean stealthMode) {
            this.stealthMode = stealthMode;
        }
    }

}
