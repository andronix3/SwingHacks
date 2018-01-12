package com.smartg.swing.taskpane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.smartg.swing.RainLayout;

/**
 * @author Andrey Kuznetsov
 */
public class TaskPaneContainer extends JPanel {

	private static final long serialVersionUID = -4727423410369718689L;
	private Color first /* = new Color(0xFF7BA2E7) */;
	private Color second /* = new Color(0xFF6476D6) */;

	private TaskPane expanded;

	boolean gradientValid;
	private GradientPaint paint;

	boolean animated;

	boolean showRollEffect;
	boolean fadeOut;

	boolean autoCollapse;
	boolean stretch;

	public TaskPaneContainer(Color first, Color second) {
		super(new RainLayout(RainLayout.Y_AXIS, 0, 0));
		this.first = first;
		this.second = second;
		setBorder(new EmptyBorder(10, 10, 10, 10));

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				gradientValid = false;
			}
		});
	}

	public TaskPaneContainer() {
		this(new Color(0xFF7BA2E7), new Color(0xFF6476D6));
	}

	protected GradientPaint getPaint() {
		if (paint == null || !gradientValid) {
			paint = new GradientPaint(0, 0, first, 0, getHeight(), second);
		}
		return paint;
	}

	public boolean isFadeOut() {
		return fadeOut;
	}

	public void setFadeOut(boolean fadeOut) {
		this.fadeOut = fadeOut;
	}

	public void addtask(TaskPane taskPane) {
		super.addImpl(taskPane, null, -1);
		taskPane.container = this;
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		throw new RuntimeException("use addTask(TaskPane)");
	}

	public void removeTask(TaskPane taskPane) {
		super.remove(taskPane);
		if (taskPane.container == this) {
			taskPane.container = null;
		}
	}

	public void removeTask(int i) {
		TaskPane tp = getTask(i);
		if (tp.container == this) {
			tp.container = null;
		}
		super.remove(i);
	}

	@Override
	public void remove(Component comp) {
		// throw new RuntimeException("use removeTask(TaskPane)");
	}

	@Override
	public void remove(int index) {
		// throw new RuntimeException("use removeTask(int index)");
	}

	public TaskPane getTask(int index) {
		return (TaskPane) getComponent(index);
	}

	public int getTaskCount() {
		return getComponentCount();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			Paint p = getPaint();
			Graphics2D g2d = (Graphics2D) g;
			g2d.setPaint(p);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	public boolean isShowRollEffect() {
		return showRollEffect;
	}

	public void setShowRollEffect(boolean showRollEffect) {
		this.showRollEffect = showRollEffect;
	}

	public boolean isStretch() {
		return stretch;
	}

	public void setStretch(boolean stretch) {
		this.stretch = stretch;
	}

	public boolean isAutoCollapse() {
		return autoCollapse;
	}

	public void setAutoCollapse(boolean autoCollapse) {
		this.autoCollapse = autoCollapse;
		if (autoCollapse) {
			checkSizes();
			int count = getTaskCount();
			int cnt = 0;
			for (int i = 0; i < count; i++) {
				TaskPane tp = getTask(i);
				if (tp.expanded && tp != expanded) {
					cnt++;
				}
			}
			TaskPane[] tps = new TaskPane[cnt];
			cnt = 0;
			for (int i = 0; i < count; i++) {
				TaskPane tp = getTask(i);
				if (tp.expanded && tp != expanded) {
					tps[cnt++] = tp;
				}
			}
			change(new TaskPane[0], tps);
		}
	}

	protected void checkSizes() {
		int count = getTaskCount();
		int max = 0;
		if (autoCollapse) {
			if (!stretch) {
				for (int i = 0; i < count; i++) {
					TaskPane tp = getTask(i);
					max = Math.max(tp.getPreferredHeight(), max);
				}
			} else {
				Dimension d = getSize();
				Insets insets = getInsets();
				d.height -= insets.top + insets.bottom;
				for (int i = 0; i < count; i++) {
					TaskPane tp = getTask(i);
					Dimension d0 = tp.getTitleBar().getPreferredSize();
					Insets insets0 = tp.getInsets();
					d.height -= d0.height + insets0.top + insets0.bottom;
				}
				max = Math.max(d.height, 0);
			}
		}

		for (int i = 0; i < count; i++) {
			TaskPane tp = getTask(i);
			tp.setExpandedHeight(max);
		}
	}

	public void collapse(TaskPane tp) {
		checkSizes();
		if (!autoCollapse || expanded == null || expanded != tp) {
			if (animated) {
				tp.prepareToCollapse();
				tp.doCollapse();
			}
			tp.finishCollapse();
		} else {
			int count = getTaskCount();
			for (int i = 0; i < count; i++) {
				if (getTask(i) == tp) {
					if (i == count - 1) {
						expand(getTask(i - 1));
					} else {
						expand(getTask(i + 1));
					}
				}
			}
		}
	}

	public void expand(TaskPane tp) {
		checkSizes();
		if (animated) {
			if (!autoCollapse || expanded == null || expanded == tp) {
				tp.prepareToExpand();
				tp.doExpand();
				tp.finishExpand();
			} else {
				change(new TaskPane[] { tp }, new TaskPane[] { expanded });
			}
			expanded = tp;
		} else {
			tp.contentPane.setPreferredSize(null);
			tp.contentPane.setVisible(true);
			tp.finishExpand();

			if (autoCollapse && expanded != null && expanded != tp) {
				expanded.finishCollapse();
			}
			doLayout();
			expanded = tp;
		}
	}

	public void change(TaskPane[] panesToExpand, TaskPane[] panesToCollapse) {
		checkSizes();
		int count = 0;
		for (TaskPane element : panesToExpand) {
			count = Math.max(count, element.prepareToExpand());
		}
		for (TaskPane element : panesToCollapse) {
			count = Math.max(count, element.prepareToCollapse());
		}
		for (int j = 0; j < count; j++) {
			for (TaskPane element : panesToExpand) {
				element.doExpandStep();
			}
			for (TaskPane element : panesToCollapse) {
				element.doCollapseStep();
			}
			doLayout();

			for (TaskPane element : panesToCollapse) {
				element.doLayout();
			}
			paintImmediately(0, 0, getWidth(), getHeight());
		}
		for (TaskPane element : panesToExpand) {
			element.finishExpand();
			this.expanded = element;
		}
		for (TaskPane element : panesToCollapse) {
			element.finishCollapse();
		}
	}

	public void setAnimated(boolean b) {
		this.animated = b;
		int count = getTaskCount();
		for (int i = 0; i < count; i++) {
			TaskPane tp = getTask(i);
			tp.setAnimated(b);
		}
	}

	public boolean isAnimated() {
		return animated;
	}
}
