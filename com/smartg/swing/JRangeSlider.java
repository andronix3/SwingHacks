package com.smartg.swing;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.function.Function;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import com.smartg.java.util.EventListenerListIterator;

/**
 * JRangeSlider. This class implements slider with two values. Second value is
 * equals to first value plus extent, so I just reused BoundedRangeModel.
 * JRangeSlider will look correct on all platforms (using appropriate SliderUI).
 * 
 * @author andronix
 *
 */
public class JRangeSlider extends JPanel {

	private final class MouseHandler extends MouseAdapter {
		private int cursorType;
		private int pressX, pressY;
		private int firstValue;
		private int secondValue;
		private int modelExtent;

		@Override
		public void mouseMoved(MouseEvent e) {
			int value = model.getValue() - model.getMinimum();
			int secondValue = value + model.getExtent();
			switch (slider.getOrientation()) {
			case SwingConstants.HORIZONTAL:
				int x = ((int) (e.getX() * scaleX));
				if (Math.abs(x - secondValue) < 3) {
					cursorType = Cursor.E_RESIZE_CURSOR;
				} else if (Math.abs(x - value) < 3) {
					cursorType = Cursor.W_RESIZE_CURSOR;
				} else if (x > value && x < secondValue) {
					cursorType = Cursor.MOVE_CURSOR;
				} else {
					cursorType = Cursor.DEFAULT_CURSOR;
				}
				setCursor(Cursor.getPredefinedCursor(cursorType));
				break;
			case SwingConstants.VERTICAL:
				int y = ((int) ((getHeight() - e.getY()) * scaleY));
				if (Math.abs(y - secondValue) < 3) {
					cursorType = Cursor.N_RESIZE_CURSOR;
				} else if (Math.abs(y - value) < 3) {
					cursorType = Cursor.S_RESIZE_CURSOR;
				} else if (y > value && y < secondValue) {
					cursorType = Cursor.MOVE_CURSOR;
				} else {
					cursorType = Cursor.DEFAULT_CURSOR;
				}
				setCursor(Cursor.getPredefinedCursor(cursorType));
				break;
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int delta;
			int value;
			switch (cursorType) {
			case Cursor.DEFAULT_CURSOR:
				break;
			case Cursor.MOVE_CURSOR:
				if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
					delta = Math.round((pressX - e.getX()) * scaleX);
					value = firstValue - delta;
					model.setValue((int) value);
				} else {
					delta = Math.round(-(pressY - e.getY()) * scaleY);
					value = firstValue - delta;
					model.setValue((int) value);
				}
				repaint();
				break;

			case Cursor.E_RESIZE_CURSOR:
				delta = Math.round((pressX - e.getX()) * scaleX);
				int extent = (int) (modelExtent - delta);
				if (extent < 0) {
					model.setValue(firstValue + extent);
					model.setExtent(0);
				} else {
					model.setExtent(extent);
				}
				repaint();
				break;

			case Cursor.W_RESIZE_CURSOR:
				delta = Math.round((pressX - e.getX()) * scaleX);
				if (delta > firstValue) {
					delta = firstValue;
				}
				value = firstValue - delta;
				if(value < model.getMinimum()) {
					value = model.getMinimum();
				}
				model.setValue(value);
				model.setExtent(secondValue - value);
				repaint();
				break;

			case Cursor.N_RESIZE_CURSOR:
				delta = Math.round(-(pressY - e.getY()) * scaleY);
				extent = (int) (modelExtent - delta);
				if (extent < 0) {
					model.setValue(firstValue + extent);
					model.setExtent(0);
				} else {
					model.setExtent(extent);
				}
				repaint();
				break;

			case Cursor.S_RESIZE_CURSOR:
				delta = Math.round(-(pressY - e.getY()) * scaleY);
				if (delta > firstValue) {
					delta = firstValue;
				}
				value = firstValue - delta;
				if(value < model.getMinimum()) {
					value = model.getMinimum();
				}
				model.setValue(value);
				model.setExtent(secondValue - value);
				repaint();
				break;
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			pressX = e.getX();
			pressY = e.getY();
			firstValue = model.getValue();
			modelExtent = model.getExtent();
			secondValue = model.getValue() + model.getExtent();
		}
	}

	private static final long serialVersionUID = -4923076507643832793L;

	private BoundedRangeModel model;

	private MouseHandler mouseHandler = new MouseHandler();
	private float scaleX, scaleY;

	private JSlider slider = new JSlider();

	private Function<Integer, Integer> function = new Function<Integer, Integer>() {
		public Integer apply(Integer t) {
			return t;
		}
	};

	private Function<Integer, Float> floatFunction = new Function<Integer, Float>() {
		public Float apply(Integer t) {
			return t + 0f;
		}
	};

	public JRangeSlider() {
		this(0, 100, 0, 10);
	}

	public JRangeSlider(int min, int max, int value, int extent) {
		this(new DefaultBoundedRangeModel(value, extent, min, max));
	}

	public JRangeSlider(BoundedRangeModel model) {
		this.model = model;

		slider.setMinimum(model.getMinimum());
		slider.setMaximum(model.getMaximum());

		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				computeScaleX();
				computeScaleY();
			}
		});
		setBorder(new EmptyBorder(1, 1, 1, 1));
		model.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				fireChangeEvent();
				repaint();
			}
		});
	}

	public int getValue() {
		return getFunction().apply(model.getValue());
	}

	public void setValue(int i) {
		i = clamp(i);
		int v = model.getValue();
		int e = model.getExtent();
		model.setRangeProperties(i, v + e - i, model.getMinimum(), model.getMaximum(), false);
	}

	private int clamp(int i) {
		int max = model.getMaximum();
		if (i > max) {
			i = max;
		}
		int min = model.getMinimum();
		if (i < min) {
			i = min;
		}
		return i;
	}

	public int getSecondValue() {
		return getFunction().apply(model.getValue() + model.getExtent());
	}

	public void setSecondValue(int i) {
		i = clamp(i);

		int v = model.getValue();
		model.setExtent(i - v);
	}

	public Function<Integer, Integer> getFunction() {
		return function;
	}

	public void setFunction(Function<Integer, Integer> function) {
		if (function != null) {
			this.function = function;
		} else {
			this.function = new Function<Integer, Integer>() {
				public Integer apply(Integer t) {
					return t;
				}
			};
		}
	}

	public Function<Integer, Float> getFloatFunction() {
		return floatFunction;
	}

	public void setFloatFunction(Function<Integer, Float> floatFunction) {
		if (floatFunction != null) {
			this.floatFunction = floatFunction;
		} else {
			this.floatFunction = new Function<Integer, Float>() {
				public Float apply(Integer t) {
					return t + 0f;
				}
			};
		}
	}

	public float getFloatValue() {
		return floatFunction.apply(getValue());
	}

	public float getFloatSecondValue() {
		return floatFunction.apply(getSecondValue());
	}

	private void fireChangeEvent() {
		EventListenerListIterator<ChangeListener> iter = new EventListenerListIterator<ChangeListener>(
				ChangeListener.class, listenerList);
		ChangeEvent e = new ChangeEvent(this);
		while (iter.hasNext()) {
			ChangeListener next = iter.next();
			next.stateChanged(e);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		slider.setMinimum(model.getMinimum());
		slider.setMaximum(model.getMaximum());
		
		slider.setBounds(getBounds());

		slider.setValue(model.getMinimum());
		BasicSliderUI ui = (BasicSliderUI) slider.getUI();
		if (getPaintTrack()) {
			ui.paintTrack(g);
		}

		slider.setValue(getSecondValue());

		Rectangle clip = g.getClipBounds();

		if (getOrientation() == SwingConstants.HORIZONTAL) {
			Rectangle r = new Rectangle((int) ((model.getValue() - model.getMinimum()) / scaleX), 0, getWidth(), getHeight());
			r = r.intersection(clip);
			g.setClip(r.x, r.y, r.width, r.height);
		}

		slider.paint(g);

		g.setClip(clip.x, clip.y, clip.width, clip.height);

		if (getPaintLabels()) {
			ui.paintLabels(g);
		}
		if (getPaintTicks()) {
			ui.paintTicks(g);
		}

		slider.setValue(getValue());
		ui.paintThumb(g);
	}

	private void computeScaleX() {
		float width = getWidth();
		Insets ins = getInsets();
		width -= ins.left + ins.right;

		int min = model.getMinimum();
		int max = model.getMaximum();

		float size = max - min;
		scaleX = size / width;
	}

	private void computeScaleY() {
		float height = getHeight();
		Insets ins = getInsets();
		height -= ins.top + ins.bottom;

		int min = model.getMinimum();
		int max = model.getMaximum();

		float size = max - min;
		scaleY = size / height;
	}

	// all following methods just forwarding calls to/from JSlider

	@SuppressWarnings("rawtypes")
	public Dictionary getLabelTable() {
		return slider.getLabelTable();
	}

	@SuppressWarnings("rawtypes")
	public void setLabelTable(Dictionary labels) {
		slider.setLabelTable(labels);
	}

	public boolean getPaintLabels() {
		return slider.getPaintLabels();
	}

	public void setPaintLabels(boolean b) {
		slider.setPaintLabels(b);
	}

	public boolean getPaintTrack() {
		return slider.getPaintTrack();
	}

	public void setPaintTrack(boolean b) {
		slider.setPaintTrack(b);
	}

	public boolean getPaintTicks() {
		return slider.getPaintTicks();
	}

	public void setPaintTicks(boolean b) {
		slider.setPaintTicks(b);
	}

	public boolean getSnapToTicks() {
		return slider.getSnapToTicks();
	}

	public void setSnapToTicks(boolean b) {
		slider.setSnapToTicks(b);
	}

	public int getMinorTickSpacing() {
		return slider.getMinorTickSpacing();
	}

	public void setMinorTickSpacing(int n) {
		slider.setMinorTickSpacing(n);
	}

	public int getMajorTickSpacing() {
		return slider.getMajorTickSpacing();
	}

	public void setMajorTickSpacing(int n) {
		slider.setMajorTickSpacing(n);
	}

	public boolean getInverted() {
		return slider.getInverted();
	}

	public void setInverted(boolean b) {
		slider.setInverted(b);
	}

	public void setFont(Font font) {
		if (slider != null) {
			slider.setFont(font);
		}
	}

	@SuppressWarnings("rawtypes")
	public Hashtable createStandardLabels(int increment, int start) {
		return slider.createStandardLabels(increment, start);
	}

	@SuppressWarnings("rawtypes")
	public Hashtable createStandardLabels(int increment) {
		return slider.createStandardLabels(increment);
	}

	@Override
	public Dimension getPreferredSize() {
		return slider.getPreferredSize();
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		slider.setPreferredSize(preferredSize);
	}

	public int getOrientation() {
		return slider.getOrientation();
	}

	public void setOrientation(int orientation) {
		slider.setOrientation(orientation);
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	public boolean getValueIsAdjusting() {
		return slider.getValueIsAdjusting();
	}

	public void setValueIsAdjusting(boolean b) {
		slider.setValueIsAdjusting(b);
	}

	public int getMaximum() {
		return slider.getMaximum();
	}

	public void setMaximum(int maximum) {
		model.setMaximum(maximum);
		slider.setMaximum(maximum);
	}

	public int getMinimum() {
		return slider.getMinimum();
	}

	public void setMinimum(int minimum) {
		model.setMinimum(minimum);
		slider.setMinimum(minimum);
	}

	public BoundedRangeModel getModel() {
		return model;
	}

	public void setModel(BoundedRangeModel newModel) {
		this.model = newModel;
		slider.setMinimum(model.getMinimum());
		slider.setMaximum(model.getMaximum());
	}

	public ChangeListener[] getChangeListeners() {
		return listenerList.getListeners(ChangeListener.class);
	}

	public static void main(String... s) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		final JRangeSlider jrs = new JRangeSlider(0, 100, 20, 30);
		jrs.setFunction(new Function<Integer, Integer>() {
			public Integer apply(Integer t) {
				return (t / 2) * 2 + 1;
			}
		});
		jrs.setOrientation(SwingConstants.VERTICAL);

		final JToggleButton jtb = new JToggleButton("ChangeValue");
		jtb.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (jtb.isSelected()) {
					jrs.setValue(30);
				} else {
					jrs.setValue(70);
				}
			}
		});
		frame.getContentPane().add(jrs);
		frame.getContentPane().add(jtb);
		frame.getContentPane().add(new JSlider());

		frame.pack();
		frame.setVisible(true);
	}
}
