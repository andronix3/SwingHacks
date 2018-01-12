package com.smartg.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeConstraints;

/**
 * This class will allow to add JLabels with email address and a close button to a panel
 * 
 * @author User
 *
 */
public class EmailPanel extends JPanel implements Iterable<String> {
    private static final long serialVersionUID = 2427218884711848540L;

    private final JTextField textField = new JTextField(8);

    private Border labelBorder = new EtchedBorder();

    private Icon closeIcon = new SimpleCloseIcon();

    private static final String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
	    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Pattern pattern;
    private Matcher matcher;

    private final HashMap<String, JComponent> list = new HashMap<>();

    public EmailPanel() {
	super(new GFlowLayout());

	setBackground(Color.WHITE);

	pattern = Pattern.compile(emailPattern);

	add(textField);
	textField.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String text = textField.getText().trim();
		if (text.isEmpty()) {
		    textField.setText("");
		    return;
		}
		addEmail(text);
		textField.setText("");
	    }
	});

	addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseReleased(MouseEvent e) {
		textField.requestFocus();
	    }
	});

	setBorder(textField.getBorder());
	textField.setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    public void addEmail(String text) {
	if (list.get(text) != null) {
	    return;
	}
	if (validate(text)) {
	    JPanel box = new JPanel();
	    LayoutNode.HorizontalNode root = new LayoutNode.HorizontalNode("root");
	    root.setHgap(5);
	    root.setVgap(1);
	    box.setLayout(new JNodeLayout(box, root));

	    JLabel label = new JLabel(text);
	    NullMarginButton closeButton = new NullMarginButton(closeIcon);
	    closeButton.setContentAreaFilled(false);

	    box.add(label, new NodeConstraints("root"));
	    box.add(closeButton, new NodeConstraints("root"));

	    label.setBorder(null);
	    label.setOpaque(true);
	    list.put(text, box);
	    box.setBorder(labelBorder);
	    add(box, null, getComponentCount() - 1);
	    revalidate();
	    closeButton.addActionListener(e -> {
		remove(box);
		list.remove(label.getText());
		revalidate();
	    });
	} else {
	    Logger.getGlobal().warning("Validate failed for " + text);
	}
    }

    public boolean removeEmail(String email) {
	JComponent label = list.remove(email);
	if (label != null) {
	    remove(label);
	    return true;
	}
	return false;
    }

    @Override
	public Iterator<String> iterator() {
	return list.keySet().iterator();
    }

    private boolean validate(final String hex) {
	matcher = pattern.matcher(hex);
	return matcher.matches();
    }
}
