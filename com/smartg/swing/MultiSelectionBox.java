package com.smartg.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComboBoxUI;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;

/**
 * 
 * MultiSelectionBox - selected items are moved from combo to panel. Items
 * removed from panel appears back in comboBox.
 * 
 * @author andro
 *
 * @param <T>
 */
public class MultiSelectionBox<T> extends JPanel {

    public static <T> void init(JScrollPane jsp, JComboBox<T> box, JList<T> list, ListCellRenderer<T> renderer,
	    List<T> elements, List<T> selectedItems) {
	XListModel<T> boxModel = new XListModel<>();
	box.setModel(boxModel);

	XListModel<T> listModel = new XListModel<>();
	list.setModel(listModel);

	if (renderer != null) {
	    box.setRenderer(renderer);
	    list.setCellRenderer(renderer);
	}
	try {
	    getListFromComboBox(box).addMouseListener(new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
		    @SuppressWarnings("unchecked")
		    int index = ((JList<T>) e.getSource()).locationToIndex(e.getPoint());
		    T elem = boxModel.getElementAt(index);
		    boxModel.setVisible(false, elem);
		    listModel.setVisible(true, elem);
		    list.setSelectedValue(elem, true);
		    selectedItems.add(elem);
		    box.setSelectedIndex(-1);
		}
	    });
	} catch (NoSuchFieldException | IllegalAccessException ex) {
	    java.util.logging.Logger.getLogger(MultiSelectionBox.class.getName()).log(java.util.logging.Level.WARNING,
		    ex.getMessage(), ex);
	}

	list.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 2) {
		    int index = list.locationToIndex(e.getPoint());
		    T elem = listModel.getElementAt(index);
		    boxModel.setVisible(true, elem);
		    listModel.setVisible(false, elem);
		    list.clearSelection();
		    selectedItems.remove(elem);
		}
	    }
	});
	jsp.setColumnHeaderView(box);
	jsp.setViewportView(list);

	elements.stream().forEach(t -> {
	    boxModel.addElement(t);
	    listModel.addElement(t);
	    listModel.setVisible(false, t);
	});
    }

    private static final long serialVersionUID = -5751711899713905904L;

    private Border labelBorder = new EtchedBorder();

    private Icon closeIcon = new SimpleCloseIcon();
    private final HashMap<T, JLabel> map = new HashMap<>();

    private final ArrayList<T> selectedItems = new ArrayList<>();
    private final XListModel<T> comboModel = new XListModel<>();
    private final JComboBox<T> comboBox = new JComboBox<>(comboModel);

    private final JViewport viewport;

    private JButton arrowButton;

    public MultiSelectionBox() {
	setLayout(new GFlowLayout(FlowLayout.LEFT, 5, 10));
	viewport = new JViewport();
	viewport.setView(comboBox);
	viewport.setPreferredSize(new Dimension(20, 20));
	comboBox.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		viewport.setViewPosition(new Point(comboBox.getWidth() - arrowButton.getWidth(), 0));
	    }
	});

	addAncestorListener(new AncestorListener() {
	    @Override
	    public void ancestorRemoved(AncestorEvent e) {
		viewport.setViewPosition(new Point(comboBox.getWidth() - arrowButton.getWidth(), 0));
	    }

	    @Override
	    public void ancestorMoved(AncestorEvent e) {
		viewport.setViewPosition(new Point(comboBox.getWidth() - arrowButton.getWidth(), 0));
	    }

	    @Override
	    public void ancestorAdded(AncestorEvent e) {
		viewport.setViewPosition(new Point(comboBox.getWidth() - arrowButton.getWidth(), 0));
	    }
	});

	add(viewport);

	try {
	    JList<T> list = getListFromComboBox(comboBox);

	    list.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
		    int index = list.locationToIndex(e.getPoint());
		    T elem = comboModel.getElementAt(index);
		    comboModel.setVisible(false, elem);
		    map.get(elem).setVisible(true);
		    selectedItems.add(elem);
		}
	    });

	    Field arrowButtonField = getBasicComboBoxUI_Class(comboBox.getUI()).getDeclaredField("arrowButton");
	    arrowButtonField.setAccessible(true);
	    arrowButton = (JButton) arrowButtonField.get(comboBox.getUI());
	} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
	    Logger.getLogger(getClass().getName()).log(Level.WARNING, ex.getMessage(), ex);
	}
    }

    private static <T> JList<T> getListFromComboBox(JComboBox<T> comboBox)
	    throws NoSuchFieldException, IllegalAccessException {
	ComboBoxUI ui = comboBox.getUI();
	Class<? extends ComboBoxUI> cls = getBasicComboBoxUI_Class(ui);

	Field listField = cls.getDeclaredField("listBox");
	listField.setAccessible(true);
	@SuppressWarnings("unchecked")
	JList<T> list = (JList<T>) listField.get(ui);
	return list;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends ComboBoxUI> getBasicComboBoxUI_Class(ComboBoxUI ui) {
	Class<? extends ComboBoxUI> cls = ui.getClass();
	while (!cls.getName().endsWith("BasicComboBoxUI")) {
	    cls = (Class<? extends ComboBoxUI>) cls.getSuperclass();
	}
	return cls;
    }

    public void addItem(T item) {
	JLabel label = createLabel(item);
	add(label);
	map.put(item, label);
	comboBox.addItem(item);
    }

    private JLabel createLabel(T item) {
	JLabel label = new JLabel(String.valueOf(item), closeIcon, SwingConstants.LEADING);
	label.setHorizontalTextPosition(SwingConstants.LEFT);
	label.setBorder(labelBorder);
	label.setVisible(false);
	label.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseReleased(MouseEvent e) {
		if (isEnabled()) {
		    if (closeIcon == null) {
			comboModel.setVisible(true, item);
			label.setVisible(false);
			selectedItems.remove(item);
		    } else if (e.getX() > (label.getWidth() - (closeIcon.getIconWidth() + getInsets().right))) {
			comboModel.setVisible(true, item);
			label.setVisible(false);
			selectedItems.remove(item);
		    }
		}
	    }
	});
	return label;
    }

    @Override
    public void setEnabled(boolean enabled) {
	super.setEnabled(enabled);
	comboBox.setEnabled(enabled);
	Component[] cmps = getComponents();
	for (Component c : cmps) {
	    c.setEnabled(enabled);
	}
    }

    public void removeItem(T item) {
	JLabel label = map.get(item);
	comboBox.removeItem(item);
	remove(label);
	selectedItems.remove(item);
    }

    public void setCellRenderer(ListCellRenderer<T> renderer) {
	comboBox.setRenderer(renderer);
    }

    public void clear() {
	removeAll();
	add(viewport);
	selectedItems.clear();
	comboBox.removeAll();
	map.clear();
    }

    public void addElements(List<T> list) {
	int index = comboBox.getSelectedIndex();
	if (index < 0) {
	    index = 0;
	}
	list.stream().forEach(t -> {
	    addItem(t);
	});
	comboBox.setSelectedIndex(index);
    }

    public Icon getCloseIcon() {
	return closeIcon;
    }

    public void setCloseIcon(Icon closeIcon) {
	this.closeIcon = closeIcon;
    }

    public Border getLabelBorder() {
	return labelBorder;
    }

    public void setLabelBorder(Border labelBorder) {
	this.labelBorder = labelBorder;
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

	// JList<String> list = new JList<>();
	ArrayList<String> elements = new ArrayList<>();
	for (String s : labels) {
	    elements.add(s);
	}
	// XListModel<String> model = new XListModel<>(elements);
	// list.setModel(model);
	//
	// JComboBox<String> comboBox = new JComboBox<>(model);
	//
	// list.addMouseListener(new MouseAdapter() {
	// @Override
	// public void mouseReleased(MouseEvent e) {
	// if (e.getClickCount() > 1) {
	// int index = list.locationToIndex(e.getPoint());
	// ArrayList<String> list = new ArrayList<>();
	// list.add(model.getElementAt(index));
	// model.setVisible(false, list);
	// }
	// }
	// });
	// {
	// JFrame frame2 = new JFrame();
	// frame2.getContentPane().setLayout(new BorderLayout());
	// frame2.getContentPane().add(list);
	// frame2.getContentPane().add(comboBox, BorderLayout.SOUTH);
	//
	// frame2.pack();
	// frame2.setVisible(true);
	// }

	EmailPanel emailPanel = new EmailPanel();
	String[] email = { "andronix@gmx.net", "bobr@reedphoto.com", "craigf@reedphoto.com", "cyndys@reedphoto.com",
		"dairad@reedphoto.com", "daniel.arguello@reedphoto.com", "danr@reedphoto.com", "danw@reedphoto.com",
		"david.arguello@reedphoto.com", "garyr@reedphoto.com", "greg.bishop@reedphoto.com",
		"jodya@reedphoto.com", "johnheneman@yahoo.com", "justin.key@reedphoto.com", "kellyr@reedphoto.com",
		"kevanvalenzuela@gmail.com", "kimr@kkdrealestate.com", "steve.higgins@reedphoto.com",
		"thetrev@comcast.net", "victor.gloria@jeffmitchum.com" };

	{
	    JFrame frame2 = new JFrame();
	    frame2.getContentPane().add(emailPanel);

	    frame2.setSize(200, 200);
	    for (String s : email) {
		emailPanel.addEmail(s);
	    }
	    frame2.pack();
	    frame2.setVisible(true);

	}

	MultiSelectionBox<String> msbox = new MultiSelectionBox<>();
	List<String> asList = Arrays.asList(labels);
	msbox.addElements(asList);

	{
	    JFrame frame2 = new JFrame();
	    frame2.getContentPane().setLayout(new BorderLayout());
	    frame2.getContentPane().add(msbox);

	    frame2.pack();
	    frame2.setVisible(true);

	}

	JScrollPane jsp = new JScrollPane();
	JComboBox<String> comboBox = new JComboBox<String>();
	JList<String> list = new JList<String>();
	init(jsp, comboBox, list, null, asList, new ArrayList<>());
	JFrame frame = new JFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(jsp);
	frame.pack();
	frame.setVisible(true);

    }
}