package com.smartg.swing.taskpane;

import java.awt.Color;

import com.smartg.swing.NullMarginButton;
import com.smartg.swing.taskpane.DoubleArrawIcon.Direction;

public class DoubleArrowButton extends NullMarginButton {

	private static final long serialVersionUID = 7407384488920221850L;

	DoubleArrawIcon icon, rollover, pressed;
	Direction direction;

	public DoubleArrowButton(Direction d) {
		createIcons(d);
		setRolloverEnabled(true);
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
	}

	private void createIcons(Direction d) {
		this.icon = new DoubleArrawIcon(d);
		this.rollover = new DoubleArrawIcon(d);
		this.rollover.setForeground(Color.LIGHT_GRAY);
		this.pressed = new DoubleArrawIcon(d);
		this.pressed.setBackground(Color.LIGHT_GRAY);

		setIcon(this.icon);
		setRolloverIcon(this.rollover);
		setPressedIcon(this.pressed);

		this.direction = d;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction d) {
		this.direction = d;
		this.icon.setDirection(d);
		this.rollover.setDirection(d);
		this.rollover.setDirection(d);
	}
}
