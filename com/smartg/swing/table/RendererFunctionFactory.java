package com.smartg.swing.table;

import java.awt.Color;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.Icon;

public class RendererFunctionFactory {

	private Function<CellRendererParams, Boolean> buttonContentAreaFilled = t -> true;
	private Function<CellRendererParams, Boolean> buttonBorderPainted = t -> true;
	private Function<CellRendererParams, Boolean> useValueForButton = t -> false;
	private Function<CellRendererParams, Boolean> showButton = t -> false;
	private Function<CellRendererParams, Boolean> textFieldEditable = t -> false;
	private Function<CellRendererParams, String> buttonText = t -> null;
	private Function<CellRendererParams, Icon> buttonIcon = t -> null;

	private Function<CellRendererParams, Color> backgroundcolor = t -> {
		if (t.isSelected()) {
			return t.getTable().getSelectionBackground();
		} else {
			return t.getTable().getBackground();
		}
	};

	public Function<CellRendererParams, Boolean> getTextFieldEditable() {
		return textFieldEditable;
	}

	public void setTextFieldEditable(Function<CellRendererParams, Boolean> textFieldEditable) {
		this.textFieldEditable = textFieldEditable;
	}

	public Function<CellRendererParams, Boolean> getShowButton() {
		return showButton;
	}

	public void setShowButton(Function<CellRendererParams, Boolean> showButton) {
		this.showButton = showButton;
	}

	public Function<CellRendererParams, Color> getBackgroundcolor() {
		return backgroundcolor;
	}

	public Function<CellRendererParams, Boolean> getButtonContentAreaFilled() {
		return buttonContentAreaFilled;
	}

	public Function<CellRendererParams, Boolean> getButtonBorderPainted() {
		return buttonBorderPainted;
	}

	public Function<CellRendererParams, Boolean> getUseValueForButton() {
		return useValueForButton;
	}

	public Function<CellRendererParams, String> getButtonText() {
		return buttonText;
	}

	public Function<CellRendererParams, Icon> getButtonIcon() {
		return buttonIcon;
	}

	public void setBackgroundcolor(Function<CellRendererParams, Color> f) {
		this.backgroundcolor = Objects.requireNonNull(f);
	}

	public void setUseValueForButton(Function<CellRendererParams, Boolean> f) {
		this.useValueForButton = Objects.requireNonNull(f);
	}

	public void setButtonText(Function<CellRendererParams, String> f) {
		this.buttonText = Objects.requireNonNull(f);
	}

	public void setButtonIcon(Function<CellRendererParams, Icon> f) {
		this.buttonIcon = Objects.requireNonNull(f);
	}

	public void setButtonContentAreaFilled(Function<CellRendererParams, Boolean> f) {
		this.buttonContentAreaFilled = Objects.requireNonNull(f);
	}

	public void setButtonBorderPainted(Function<CellRendererParams, Boolean> f) {
		this.buttonBorderPainted = Objects.requireNonNull(f);
	}

}
