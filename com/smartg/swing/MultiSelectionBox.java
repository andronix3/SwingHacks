package com.smartg.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;

/**
 * Really minimal implementation. No ListModel, no SelectionModel. Just add a
 * few items to it and then get List with selected items. Thats all.
 * 
 * @author andro
 *
 * @param <T>
 */
public class MultiSelectionBox<T> extends JPanel {

	private EtchedBorder labelBorder = new EtchedBorder();

	private static final long serialVersionUID = -5302561691114454984L;

	private final JPopupMenu popupMenu = new JPopupMenu();
	private Icon closeIcon;
	private boolean popupVisible;
	private final ArrayList<T> selectedItems = new ArrayList<>();

	public MultiSelectionBox() {
		super(new FlowL(FlowLayout.LEFT, 5, 10));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!popupVisible) {
					// reset preferredSize first
					popupMenu.setPreferredSize(null);
					Dimension preferredSize = popupMenu.getPreferredSize();
					popupMenu.setPopupSize(getWidth(), preferredSize.height);
					popupMenu.show(MultiSelectionBox.this, 0, getHeight());
				}
				popupVisible = !popupVisible;
			}
		});
	}

	public void addElements(List<T> list) {
		list.stream().forEach(t -> addItem(t));
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
		popupMenu.removeAll();
		removeAll();
	}

	public List<T> getSelectedItems() {
		return Collections.unmodifiableList(selectedItems);
	}

	private void addItem(T item) {
		JLabel label = new JLabel(String.valueOf(item), closeIcon, SwingConstants.LEADING);
		label.setHorizontalTextPosition(SwingConstants.LEFT);
		label.setBorder(labelBorder);
		ActionMenuItem menuItem = new ActionMenuItem(label, item);
		popupMenu.add(menuItem);
		add(label);
		label.setVisible(false);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (closeIcon == null) {
					menuItem.setVisible(true);
					label.setVisible(false);
					selectedItems.remove(item);
				} else if (e.getX() > (label.getWidth() - closeIcon.getIconWidth())) {
					menuItem.setVisible(true);
					label.setVisible(false);
					selectedItems.remove(item);
				}
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		d.height = Math.max(30, d.height);
		d.width += 10;
		return d;
	}

	private static final class SimpleCloseIcon implements Icon {
		private final Font font = new Font("Dialog", Font.BOLD, 12);
		
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Font fnt = g.getFont();
			g.setFont(font);
			g.drawString("x", x, y + 10);
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

	class ActionMenuItem extends JMenuItem {

		private static final long serialVersionUID = 4448429416616668679L;

		public ActionMenuItem(JLabel label, T item) {
			super(label.getText());
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					label.setVisible(true);
					popupVisible = false;
					selectedItems.add(item);
				}
			});
		}
	}

	/**
	 * FlowLayout returns incorrect preferredSize, because it ignores width of
	 * target Container, so targets parent can't compute correct height.
	 * 
	 * @author andro
	 *
	 */
	private static class FlowL extends FlowLayout {

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

	public static void main(String[] args) {
		String[] labels = { "One", "Two", "Three", "Four", "Five", "Six", "Seven" };
		MultiSelectionBox<String> msb = new MultiSelectionBox<>();
		msb.setBackground(Color.WHITE);
		msb.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		msb.setCloseIcon(new SimpleCloseIcon());
		msb.setElements(Arrays.asList(labels));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel box = new JPanel();
		LayoutNode.HorizontalNode root = new LayoutNode.HorizontalNode("root");
		box.setLayout(new JNodeLayout(box, root));
		root.setHorizontalAlignment(NodeAlignment.STRETCHED);
		root.setVerticalAlignment(NodeAlignment.TOP);
		root.add(msb);
		frame.getContentPane().add(box);
		frame.pack();
		frame.setVisible(true);
	}
}
