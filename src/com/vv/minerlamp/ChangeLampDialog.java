package com.vv.minerlamp;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXDatePicker;

import com.vv.minerlamp.dao.LampUnitDAO;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

class ChangeLampDialog extends JDialog {
	public ChangeLampDialog(final StaffInfoDialog owner, final Long rackId,
			final Long lampNo) {
		super(owner, "矿灯使用度设置", true);
		setIconImage(SysConfiguration.sysIcon);
		lampUnitDAO = new LampUnitDAO();
		setLayout(new GridBagLayout());
		JPanel panel = new JPanel();
		add(panel,
				new GBC(0, 0).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));
		panel.setLayout(new GridBagLayout());
		panel.add(new JPanel(), new GBC(0, 0, GridBagConstraints.REMAINDER, 1)
				.setFill(GridBagConstraints.HORIZONTAL).setWeight(100, 0));

		panel.add(new JLabel("  已使用次数："), new GBC(0, 1).setWeight(100, 0)
				.setAnchor(GridBagConstraints.EAST));
		panel.add(new JLabel("开始使用时间："), new GBC(0, 2).setWeight(100, 0)
				.setAnchor(GridBagConstraints.EAST));
		initCountTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
		initCountTxt.setValue(0);

		initCountTxt.setPreferredSize(new Dimension(185, 20));
		initTimeTxt = new JXDatePicker(new Date());
		initTimeTxt.setFormats(Util.dateFormat);
		initTimeTxt.setPreferredSize(new Dimension(185, 20));
		ActionListener loginActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Integer initCount = new Integer(initCountTxt.getText());
				Date initTime = new Date();
				initTime = initTimeTxt.getDate();
				lampUnitDAO.changeLamp(rackId, lampNo, initCount, initTime.getTime());
				JOptionPane.showMessageDialog(ChangeLampDialog.this,
						"矿灯使用度设置成功", "提示", JOptionPane.INFORMATION_MESSAGE);
				owner.refreshChargingLabel();
				dispose();

			}
		};

		panel.add(initCountTxt, new GBC(1, 1, 2, 1).setWeight(100, 0)
				.setAnchor(GridBagConstraints.WEST).setInsets(5));
		panel.add(
				initTimeTxt,
				new GBC(1, 2, 2, 1).setWeight(100, 0)
						.setAnchor(GridBagConstraints.WEST).setInsets(5));
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(loginActionListener);
		JButton cancelBtn = new JButton("取消");
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// setVisible(false);
				dispose();
			}
		});
		panel.add(okBtn, new GBC(1, 3).setInsets(5, 5, 5, 0));
		panel.add(cancelBtn, new GBC(2, 3).setInsets(5, 0, 5, 5));

		setResizable(false);
		setSize(320, 200);
		setLocation(Util.calculatePosition(owner, this));
		pack();
	}

	private JFormattedTextField initCountTxt;
	private JXDatePicker initTimeTxt;
	private LampUnitDAO lampUnitDAO;
}
