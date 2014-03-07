package com.vv.minerlamp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class BlueJLabel extends JLabel {
	public BlueJLabel() {
		// setBorder(BorderFactory.createEtchedBorder());
		setFont(new Font("TimesRoman", Font.BOLD, 12));
		setOpaque(true);
		setBackground(new Color(182, 230, 247));
		setPreferredSize(new Dimension(100, 30));
	}

	public BlueJLabel(String title) {

		super(title);
		// setBorder(BorderFactory.createEtchedBorder());
		setFont(new Font("TimesRoman", Font.BOLD, 12));
		setOpaque(true);
		setBackground(new Color(182, 230, 247));
		setPreferredSize(new Dimension(100, 30));

	}
}
