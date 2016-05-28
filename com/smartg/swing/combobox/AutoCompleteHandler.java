package com.smartg.swing.combobox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * Adds AutoCompletion to your JComboBox.
 *
 * What is different? AutoCompleteHandler will work with dynamic data (e.g. from
 * database)
 *
 * @author andro
 * @param <E>
 */
public class AutoCompleteHandler<E> {

    private final JComboBox<E> comboBox;
    private final KeyHandler keyHandler = new KeyHandler();
    private boolean ignoreAction;
    private List<E> elements = new ArrayList<>();
    private final Function<String, List<E>> provider;
    private final ActionListener listener;
    private int count;

    private int minTextSize = 3;

    public AutoCompleteHandler(JComboBox<E> comboBox, Function<String, List<E>> provider, ActionListener listener) {
        this.comboBox = Objects.requireNonNull(comboBox);
        this.provider = Objects.requireNonNull(provider);
        this.listener = Objects.requireNonNull(listener);

        this.comboBox.addActionListener((ActionEvent e) -> {
            if (!ignoreAction) {
                this.listener.actionPerformed(e);
            }
        });
        comboBox.setEditable(true);
        comboBox.setMaximumRowCount(7);
        comboBox.getEditor().getEditorComponent().addKeyListener(keyHandler);
    }

    private void updateComboBox(String s) {
        ignoreAction = true;
        comboBox.removeAllItems();

        elements.stream().forEach((E custInfo) -> {
            comboBox.addItem(custInfo);
        });

        int size = elements.size();
        if (size >= minTextSize && count < minTextSize) {
            comboBox.setPopupVisible(false);
        }
        count = size;

        comboBox.setPopupVisible(true);
        ((JTextField) comboBox.getEditor().getEditorComponent()).setText(s);
        ignoreAction = false;
    }

    public void reload() {
        if(keyHandler.value.length() >= minTextSize) {
            elements = provider.apply(keyHandler.value);
        }
        else {
            elements.clear();
        }
        updateComboBox(keyHandler.value);
    }

    public int getMinTextSize() {
        return minTextSize;
    }

    /**
     * Set minimum text length. Before minimum text length is reached,
     * AutoCompleteHandler will not start to search for entries.
     *
     * @param minTextSize
     */
    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
    }

    private class KeyHandler extends KeyAdapter {

        private String value = "";

        @Override
        public void keyReleased(KeyEvent e) {
            String text = ((JTextField) comboBox.getEditor().getEditorComponent()).getText();
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                comboBox.setPopupVisible(false);
                if(text.isEmpty()) {
                    comboBox.setSelectedIndex(-1);
                }
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                comboBox.setPopupVisible(false);
                return;
            }
            if (text.equalsIgnoreCase(value)) {
                return;
            }
            value = text;
            reload();
        }
    }

    public static void main(String[] args) {
        final JComboBox<String> comboBox = new JComboBox<>();
        final List<String> list = Arrays.asList(new String[]{"sim", "simpanova", "simpatisch", "simple", "one", "one night", "one night stand"});
        ActionListener listener = (ActionEvent e) -> {
            System.out.println(e.getActionCommand());
        };
        Function<String, List<String>> supplier = (String text) -> {
            List<String> res = list.stream().filter((String t) -> t.toLowerCase().contains(text)).collect(Collectors.toList());
            return res;
        };

        @SuppressWarnings("unused")
		AutoCompleteHandler<String> handler = new AutoCompleteHandler<>(comboBox, supplier, listener);

        comboBox.addActionListener((ActionEvent e) -> {
            System.out.println("***** " + e.getActionCommand());
        });

        comboBox.addItemListener((ItemEvent e) -> {
            System.out.println("-----" + e.getItem() + " " + Arrays.asList(e.getItemSelectable().getSelectedObjects()));
        });

        UIManager.put("ComboBox.noActionOnKeyNavigation", true);

        JFrame frame = new JFrame();
        frame.getContentPane().add(comboBox);
        frame.pack();
        frame.setVisible(true);

    }
}
