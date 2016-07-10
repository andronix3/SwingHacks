package com.smartg.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.ComboBoxUI;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;

/**
 * 
 * MultiSelectionBox - selected items are moved from combo to panel. 
 * Items removed from panel appears back in comboBox.
 *  
 * @author andro
 *
 * @param <T>
 */
public class MultiSelectionBox<T> extends JPanel {

    /**
     * FlowLayout returns incorrect preferredSize, because it ignores width of
     * target Container, so targets parent can't compute correct height.
     * 
     * @author andro
     *
     */
    static class FlowL extends FlowLayout {
    
        private static final long serialVersionUID = 6046127399548228005L;
    
        public FlowL(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }
    
        @Override
        public Dimension preferredLayoutSize(Container target) {
            Dimension ps = super.preferredLayoutSize(target);
            Insets insets = target.getInsets();
            int w = target.getWidth();
            w -= insets.left + insets.right;
            float m = ps.width / (float) w;
            if (m > 1) {
        	ps.width = w;
        	ps.height = ps.height * Math.round(m + 0.5f);
            }
            return ps;
        }
    }

    static final class SimpleCloseIcon implements Icon {
        private final Font font = new Font("Dialog", Font.BOLD, 12);
    
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Font fnt = g.getFont();
            g.setFont(font);
            String s = "x";
            int sw = g.getFontMetrics().stringWidth(s);
            int sh = g.getFontMetrics().getHeight();
            g.drawString(s, x + (getIconWidth() - sw) / 2, y + (getIconHeight() + sh / 2) / 2);
            g.setFont(fnt);
        }
    
        @Override
        public int getIconWidth() {
            return 15;
        }
    
        @Override
        public int getIconHeight() {
            return 15;
        }
    }

    private static final long serialVersionUID = -5751711899713905904L;

    private EtchedBorder labelBorder = new EtchedBorder();

    private Icon closeIcon = new MultiSelectionBox.SimpleCloseIcon();
    private final HashMap<T, JLabel> map = new HashMap<>();

    private final ArrayList<T> selectedItems = new ArrayList<>();
    private final XListModel<T> model = new XListModel<>();
    final JComboBox<T> comboBox = new JComboBox<>(model);

    private final JViewport viewport;

    private JButton arrowButton;

    @SuppressWarnings("unchecked")
    public MultiSelectionBox() {
        setLayout(new MultiSelectionBox.FlowL(FlowLayout.LEFT, 5, 10));
        viewport = new JViewport();
        viewport.setView(comboBox);
        viewport.setPreferredSize(new Dimension(20, 20));
        comboBox.addActionListener(new ActionListener() {
    	@Override
    	public void actionPerformed(ActionEvent e) {
    	    viewport.setViewPosition(new Point(comboBox.getWidth() - arrowButton.getWidth(), 0));
    	}
        });

        add(viewport);

        try {
    	ComboBoxUI ui = comboBox.getUI();
    	Class<? extends ComboBoxUI> cls = ui.getClass();
    	while (!cls.getName().endsWith("BasicComboBoxUI")) {
    	    cls = (Class<? extends ComboBoxUI>) cls.getSuperclass();
    	}

    	Field listField = cls.getDeclaredField("listBox");
    	listField.setAccessible(true);
    	JList<T> list = (JList<T>) listField.get(ui);

    	list.addMouseListener(new MouseAdapter() {
    	    @Override
    	    public void mouseReleased(MouseEvent e) {
    		int index = list.locationToIndex(e.getPoint());
    		ArrayList<T> lst = new ArrayList<>();
    		T elem = model.getElementAt(index);
    		lst.add(elem);
    		model.setVisible(false, lst);
    		map.get(elem).setVisible(true);
    	    }
    	});

    	Field arrowButtonField = cls.getDeclaredField("arrowButton");
    	arrowButtonField.setAccessible(true);
    	arrowButton = (JButton) arrowButtonField.get(ui);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
    	Logger.getLogger(getClass().getName()).log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    private void addItem(T item) {
        JLabel label = new JLabel(String.valueOf(item), closeIcon, SwingConstants.LEADING);
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        label.setBorder(labelBorder);
        map.put(item, label);
        comboBox.addItem(item);
        add(label);
        label.setVisible(false);
        label.addMouseListener(new MouseAdapter() {
    	@Override
    	public void mouseReleased(MouseEvent e) {
    	    if (closeIcon == null) {
    		ArrayList<T> list = new ArrayList<>();
    		list.add(item);
    		model.setVisible(true, list);
    		label.setVisible(false);
    		selectedItems.remove(item);
    	    } else if (e.getX() > (label.getWidth() - (closeIcon.getIconWidth() + getInsets().right))) {
    		ArrayList<T> list = new ArrayList<>();
    		list.add(item);
    		model.setVisible(true, list);
    		label.setVisible(false);
    		selectedItems.remove(item);
    	    }
    	}
        });
    }

    public void addElements(List<T> list) {
        list.stream().forEach(t -> {
    	addItem(t);
        });
    }

    public void setElements(List<T> list) {
        reset();
        addElements(list);
    }

    public Icon getCloseIcon() {
        return closeIcon;
    }

    public void setCloseIcon(Icon closeIcon) {
        this.closeIcon = closeIcon;
    }

    public EtchedBorder getLabelBorder() {
        return labelBorder;
    }

    public void setLabelBorder(EtchedBorder labelBorder) {
        this.labelBorder = labelBorder;
    }

    private void reset() {
        removeAll();
        add(viewport);
    }

    public List<T> getSelectedItems() {
        return Collections.unmodifiableList(selectedItems);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = Math.max(30, d.height);
        d.width += 10;
        return d;
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        AcrylLookAndFeel lnf = new com.jtattoo.plaf.acryl.AcrylLookAndFeel();
        UIManager.setLookAndFeel(lnf);
    
        String[] labels = { "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten" };
    
        JList<String> list = new JList<>();
        ArrayList<String> elements = new ArrayList<>();
        for (String s : labels) {
            elements.add(s);
        }
        XListModel<String> model = new XListModel<>(elements);
        list.setModel(model);
    
        JComboBox<String> comboBox = new JComboBox<>(model);
    
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
        	if (e.getClickCount() > 1) {
        	    int index = list.locationToIndex(e.getPoint());
        	    ArrayList<String> list = new ArrayList<>();
        	    list.add(model.getElementAt(index));
        	    model.setVisible(false, list);
        	}
            }
        });
        {
            JFrame frame2 = new JFrame();
            frame2.getContentPane().setLayout(new BorderLayout());
            frame2.getContentPane().add(list);
            frame2.getContentPane().add(comboBox, BorderLayout.SOUTH);
    
            frame2.pack();
            frame2.setVisible(true);
        }
    
        MultiSelectionBox<String> msbox = new MultiSelectionBox<>();
        msbox.addElements(Arrays.asList(labels));
    
        {
            JFrame frame2 = new JFrame();
            frame2.getContentPane().setLayout(new BorderLayout());
            frame2.getContentPane().add(msbox);
    
            frame2.pack();
            frame2.setVisible(true);
    
        }
        msbox.comboBox.setSelectedIndex(0);
    }
}