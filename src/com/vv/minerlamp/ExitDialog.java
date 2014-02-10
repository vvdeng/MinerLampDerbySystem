package com.vv.minerlamp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.xml.soap.Text;

import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

class ExitDialog extends JDialog {
	public ExitDialog(final JFrame owner) {
		super(owner, "退出确认", true);
		setIconImage(SysConfiguration.sysIcon);
		setLayout(new GridBagLayout());
		JPanel panel = new JPanel();
		add(panel,
				new GBC(0, 0).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));
		panel.setLayout(new GridBagLayout());
		panel.add(
				new JLabel(new ImageIcon("resources/logout.png")),
				new GBC(0, 0, GridBagConstraints.REMAINDER, 1).setFill(
						GridBagConstraints.HORIZONTAL).setWeight(100, 0));

		panel.add(new JLabel("  密码："), new GBC(0, 1).setWeight(100, 0)
				.setAnchor(GridBagConstraints.EAST));
		ActionListener exitActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (((MinerLampFrame) owner).getCurrentUser() == null
						|| (pwdTxt.getText() != null && pwdTxt.getText()
								.equals(((MinerLampFrame) owner)
										.getCurrentUser().getPwd()))) {
					System.exit(0);
				} else {
					JOptionPane.showMessageDialog(owner, "密码输入不正确");
				}
				dispose();

			}
		};
		pwdTxt = new JPasswordField();
		// pwdTxt.setText("       ");
		pwdTxt.setPreferredSize(new Dimension(185, 20));
		pwdTxt.registerKeyboardAction(exitActionListener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
				JComponent.WHEN_FOCUSED);
		panel.add(
				pwdTxt,
				new GBC(1, 1, 2, 1).setWeight(100, 0)
						.setAnchor(GridBagConstraints.WEST).setInsets(5));
		JButton logoutBtn = new JButton("退出");
		logoutBtn.addActionListener(exitActionListener);
		JButton exitBtn = new JButton("取消");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// setVisible(false);
				ExitDialog.this.dispose();
			}
		});
		panel.add(logoutBtn, new GBC(1, 3).setInsets(5, 5, 5, 0));
		panel.add(exitBtn, new GBC(2, 3).setInsets(5, 0, 5, 5));

		setResizable(false);
		setSize(320, 200);
		setLocation(Util.calculatePosition(owner, this));
		pack();
	}

	private JPasswordField pwdTxt;
}
