package com.smartg.swing.combobox;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.smartg.swing.layout.GridHelper;
import com.smartg.swing.layout.JNodeLayout;
import com.smartg.swing.layout.LayoutNode;
import com.smartg.swing.layout.NodeAlignment;

public class GTextEditorPanel extends GComboBoxEditorPanel<String> {

	private static final long serialVersionUID = 1370058775147021663L;
	private final JTextArea textArea = new JTextArea(20, 30);

	public GTextEditorPanel() {
		JNodeLayout layout = new JNodeLayout(this, new LayoutNode.GridNode("root"));
		setLayout(layout);
		GridHelper helper = new GridHelper(this, "root", 10);

		layout.setHgap(10);
		layout.setVgap(10);

		helper.add(new JScrollPane(textArea), 10);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> {
			finishEdit();
			fireChange();
		});
		helper.skip(7);
		helper.add(closeButton, 3);
		layout.setHorizontalAlignment("root", NodeAlignment.STRETCHED);
	}

	public JTextArea getTextArea() {
		return textArea;
	}

}