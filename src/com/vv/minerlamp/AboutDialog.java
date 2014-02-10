package com.vv.minerlamp;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

class AboutDialog extends JDialog {
	public AboutDialog(JFrame owner) {
		super(owner, "关于", true);
		setIconImage(SysConfiguration.sysIcon);
		// add HTML label to center
		setLayout(new GridBagLayout());
		add(new JLabel(new ImageIcon("resources/about.png")), new GBC(0, 0));
		add(new JLabel(
				"<html><h3><i>充电架智能管理系统</i></h3><hr>&nbsp;&nbsp;&nbsp;&nbsp;渭南光电科技有限公司</html>"),
				new GBC(1, 0));

		JButton ok = new JButton("确定");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// setVisible(false);
				dispose();
			}
		});

		JPanel panel = new JPanel();
		panel.add(ok);
		add(panel, new GBC(0, 1, 2, 1).setAnchor(GridBagConstraints.CENTER));

		setResizable(false);
		setSize(300, 150);
		setLocation(Util.calculatePosition(owner, this));
	}
}
