package com.vv.minerlamp;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
public abstract class ButtonAction extends AbstractAction {
	public ButtonAction(String name, Icon icon) {
		putValue(Action.NAME, name);
		putValue(Action.SMALL_ICON, icon);
		putValue(Action.SHORT_DESCRIPTION, name);
	}

}