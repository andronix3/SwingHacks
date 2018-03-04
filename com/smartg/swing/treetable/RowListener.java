package com.smartg.swing.treetable;

import java.util.EventListener;

public interface RowListener extends EventListener {
	void rowChanged(RowEvent e);
}
