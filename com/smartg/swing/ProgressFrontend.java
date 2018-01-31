package com.smartg.swing;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.imagero.java.beans.HandlerList;
import com.imagero.java.beans.SimplePropertyHandler;

/**
 *
 * This class will show JFrame or JDialog with JProgressBar for any long
 * background operation. You may control it either direct through methods
 * setTitle(), setString(), setPrefix(), setValue(), setMinimum(), setMaximum()
 * or set it as PropertyChangeListener and fire PropertyChangeEvents with
 * appropriate property names: title, string, prefix, value, min, max. It is
 * possible to add user defined properties.
 *
 * Example:
 * 
 * <pre>
 *
 * public class MyProcessor implements Runnable {
 * 		private final PropertyChangeSupport support = new PropertyChangeSupport(this);
 *
 * 		public void addPropertyChangeListener(PropertyChangeListener pl) {
 * 			support.addPropertyChangeListener(pl);
 * 		}
 *
 * 		public void run() {
 * 			...
 * 		}
 * }
 *
 * public static void main(String [] args) {
 *
 * 		final ProgressFrontend frontend = new ProgressFrontend(null);
 *
 * 		try {
 * 			MyProcessor processor = new MyProcessor();
 *
 * 			processor.addPropertyChangeListener(frontend);
 *
 *           new Thread() {
 *               &#64;Override
 *               public void run() {
 *                   processor.run();
 *               }
 *           }.start();
 *
 *           frontend.setExitOnClose(true);
 *           frontend.showProgressBar(600, 200);
 *       } catch (IOException ex) {
 *           frontend.showProgressBar(600, 200);
 *           frontend.setFinished("Exception during processing : " + ex.getMessage() + ". Exiting.");
 *           StackTraceUtil.severe(ex, 20);
 *       }
 * }
 *
 *
 *
 * </pre>
 *
 * @author User
 */
public class ProgressFrontend implements PropertyChangeListener {

	private final Window frame;
	private Window owner;
	private BoundedRangeModel model = new DefaultBoundedRangeModel();
	private JProgressBar progressBar;
	private String prefix;
	private long startPoint;
	private final HandlerList handlerList = new HandlerList();
	private boolean exitOnClose;
	private boolean askBeforeClosing = true;
	private String unitName = "unit";

	public ProgressFrontend(Component c) {
		if (c == null) {
			frame = new JFrame();
		} else {
			owner = SwingUtilities.getWindowAncestor(c);
			frame = new JDialog(owner);
		}
		this.model = Objects.requireNonNull(model);
		progressBar = new JProgressBar(model);
		progressBar.setStringPainted(true);
		progressBar.setString("Please wait...");
		model.addChangeListener(t -> {
			long elapsed = System.currentTimeMillis() - startPoint;
			int value = model.getValue();
			int count = model.getValue() - model.getMinimum();
			if (count == 0) {
				startPoint = System.currentTimeMillis();
				count = 1;
			}
			long timePerRow = elapsed / count;
			progressBar.setString(
					prefix + " " + value + " of " + model.getMaximum() + " (" + timePerRow + " ms/" + unitName + ")");
		});

		setHandler("title", t -> setTitle("" + t));
		setHandler("string", t -> setString("" + t));
		setHandler("prefix", t -> setPrefix("" + t));
		setHandler("value", t -> setValue((Integer) t));
		setHandler("min", t -> setMinimum((Integer) t));
		setHandler("max", t -> setMaximum((Integer) t));
		setHandler("finished", t -> setFinished());
		setHandler("unit", t -> setUnitName((String) t));
	}

	/**
	 * set PropertyHandler for particular property
	 *
	 * @param key
	 *            property name
	 * @param handler
	 *            handler for given property
	 */
	public final void setHandler(String key, SimplePropertyHandler handler) {
		handlerList.setHandler(key, handler);
	}

	/**
	 * set title of a top level window
	 *
	 * @param title
	 */
	public void setTitle(String title) {
		if (frame instanceof Frame) {
			((Frame) frame).setTitle(title);
		} else if (frame instanceof Dialog) {
			((Dialog) frame).setTitle(title);
		}
	}

	/**
	 * get unit name 
	 * @return
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * set unit name (used to show speed in ms per unit)
	 * @param unitName
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * set prefix for generated string in JProgressBar
	 *
	 * @param s
	 *            prefix
	 */
	public void setPrefix(String s) {
		this.prefix = s;
	}

	/**
	 * set string for JProgressBar
	 *
	 * @param s
	 */
	public void setString(String s) {
		progressBar.setString(s);
	}

	/**
	 * set value for BoundedRangeModel
	 *
	 * @param value
	 */
	public void setValue(int value) {
		model.setValue(value);
	}

	/**
	 * set minimum value for BoundedRangeModel
	 *
	 * @param value
	 */
	public void setMinimum(int value) {
		model.setMinimum(value);
	}

	public void setFinished() {
		setFinished("Processing finished");
	}

	public void setFinished(String message) throws HeadlessException {
		JOptionPane.showMessageDialog(frame, message);
		askBeforeClosing = false;
		closeFrame();
	}

	/**
	 * set maximum value for BoundedRangeModel
	 *
	 * @param value
	 */
	public void setMaximum(int value) {
		model.setMaximum(value);
	}

	public boolean isExitOnClose() {
		return exitOnClose;
	}

	public void setExitOnClose(boolean exitOnClose) {
		this.exitOnClose = exitOnClose;
	}

	/**
	 * show JProgreddBar in a JFrame or JDialog
	 *
	 */
	public void showProgressBar(int width, int height) {
		if (frame instanceof JDialog) {
			((JDialog) frame).getContentPane().add(progressBar);
		} else if (frame instanceof JFrame) {
			((JFrame) frame).getContentPane().add(progressBar);
		} else {
			frame.add(progressBar);
		}
		if (frame instanceof JFrame) {
			((JFrame) frame).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeFrame();
			}
		});
		frame.pack();
		if (owner == null) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
		} else {
			frame.setSize(width, height);
			frame.setLocationRelativeTo(owner);
		}
		frame.setVisible(true);
	}

	private void closeFrame() throws HeadlessException {
		boolean confirmed = !askBeforeClosing || JOptionPane.showConfirmDialog(frame, "Exit Program?", "Please Confirm",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION;
		if (confirmed) {
			if (exitOnClose) {
				System.exit(0);
			} else {
				frame.dispose();
			}
		}
	}

	@Override

	public void propertyChange(PropertyChangeEvent e) {
		handlerList.propertyChange(e.getPropertyName(), e.getNewValue());
	}
}
