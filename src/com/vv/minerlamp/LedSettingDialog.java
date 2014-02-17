package com.vv.minerlamp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vv.minerlamp.comm.CommCmdObject;
import com.vv.minerlamp.comm.SerialComm;
import com.vv.minerlamp.dao.LedSettingDAO;
import com.vv.minerlamp.entity.LedSetting;
import com.vv.minerlamp.util.CommUtil;
import com.vv.minerlamp.util.GlobalData;
import com.vv.minerlamp.util.PropertiesUtil;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

class LedSettingDialog extends JDialog {
	public LedSettingDialog(final MinerLampFrame owner) {
		super(owner, "LED设置", true);
		setIconImage(SysConfiguration.sysIcon);
		ledSettingDAO = new LedSettingDAO();
		JLabel discLabel = new JLabel(new ImageIcon(
				"resources/ledSettingTitle.png"));

		discLabel.setBackground(Color.BLUE);
		add(discLabel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createTitledBorder("欢迎词设置"));
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.add(new JLabel(" 1 "), new GBC(0, 0).setWeight(0, 0));
		mainPanel.add(new JLabel(" 2 "), new GBC(0, 1).setWeight(0, 0));
		mainPanel.add(new JLabel(" 3 "), new GBC(0, 2).setWeight(0, 0));
		mainPanel.add(new JLabel(" 4 "), new GBC(0, 3).setWeight(0, 0));
		mainPanel.add(new JLabel(" 5 "), new GBC(0, 4).setWeight(0, 0));
		mainPanel.add(new JLabel(" 6 "), new GBC(0, 5).setWeight(0, 0));
		List<LedSetting> ledSettingList = ledSettingDAO.listLedSettings();
		for (int i = 0; i < SETTGING_LENGTH; i++) {

			welcomeWordTxt[i] = new JTextField();
			mainPanel.add(welcomeWordTxt[i], new GBC(1, i).setWeight(100, 0)
					.setFill(GridBagConstraints.HORIZONTAL).setInsets(5));
			welcomeWordCheckbox[i] = new JCheckBox("显示");
			mainPanel.add(welcomeWordCheckbox[i], new GBC(2, i).setWeight(0, 0)
					.setInsets(5));
			if (ledSettingList.size() > i) {
				welcomeWordTxt[i].setText(ledSettingList.get(i).getContent());
				welcomeWordCheckbox[i].setSelected(ledSettingList.get(i)
						.getDisplay() == LedSetting.DISPLAY);
			}
		}

		panel.add(
				mainPanel,
				new GBC(0, 0).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		panel.add(
				bottomPanel,
				new GBC(0, 1).setWeight(100, 0).setFill(
						GridBagConstraints.HORIZONTAL));
		bottomPanel.add(
				new JLabel("提示：每条信息长度不超过15个汉字，超出部分不显示。"),
				new GBC(0, 0, GridBagConstraints.REMAINDER, 1)
						.setFill(GridBagConstraints.HORIZONTAL)
						.setWeight(100, 0).setInsets(5));

		bottomPanel.add(new JLabel("内容切换时间："), new GBC(0, 1).setWeight(0, 0)
				.setInsets(5));
		displayTimeTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
		displayTimeTxt.setValue(SysConfiguration.ledContentChangeTime);
		displayTimeTxt.setPreferredSize(new Dimension(185, 20));

		bottomPanel.add(displayTimeTxt, new GBC(1, 1).setWeight(0, 0)
				.setAnchor(GridBagConstraints.WEST).setInsets(5));

		JButton saveBtn = Util.makePrivButton(
				new ButtonAction("保存设置并退出", null) {

					@Override
					public void actionPerformed(ActionEvent e) {
						int selection = JOptionPane.showConfirmDialog(
								LedSettingDialog.this, "确定要保存吗", "提示",
								JOptionPane.YES_NO_OPTION);
						if (selection == JOptionPane.YES_OPTION) {
							if (new Integer(displayTimeTxt.getText()) < 10
									|| new Integer(displayTimeTxt.getText()) > 256) {
								JOptionPane.showMessageDialog(
										LedSettingDialog.this,
										"切换时间应设置在10-256秒之间");
								return;
							}
							List<LedSetting> messageList = new ArrayList<LedSetting>();
							for (int i = 0; i < SETTGING_LENGTH; i++) {
								LedSetting ledSetting = makeLedSetting(
										new Long(i + 1), welcomeWordTxt[i],
										welcomeWordCheckbox[i]);
								ledSettingDAO.save(ledSetting);
								if (ledSetting.getDisplay() == LedSetting.DISPLAY) {
									messageList.add(ledSetting);
								}
							}
							Integer ledContentChangeTime = new Integer(
									displayTimeTxt.getText());
							SysConfiguration.ledContentChangeTime = ledContentChangeTime;
							PropertiesUtil.writeProperties(
									SysConfiguration.FILE_PATH,
									"ledContentChangeTime",
									ledContentChangeTime.toString());
							CommCmdObject co=new CommCmdObject(CommCmdObject.COMM_CMD_BRORAD_MSG);
							co.setDat(CommUtil.getAllLedMessagesBytes(messageList, ledContentChangeTime));
							GlobalData.cmdQueue.add(co);
							JOptionPane.showMessageDialog(
									LedSettingDialog.this, "保存成功");
							dispose();
						}
						;

					}
				}, owner.getCurrentUser());

		bottomPanel.add(
				saveBtn,
				new GBC(2, 1).setWeight(100, 0)
						.setAnchor(GridBagConstraints.EAST)
						.setInsets(5, 5, 5, 5));

		add(panel, BorderLayout.CENTER);
		setResizable(false);
		setSize(560, 480);
		setLocation(Util.calculatePosition(owner, this));
		pack();
	}

	public LedSetting makeLedSetting(Long ledSettingId,
			JTextField welcomeWordTxt, JCheckBox welcomeWordCheckbox) {
		LedSetting ledSetting = new LedSetting();
		String content = welcomeWordTxt.getText();
		Integer display = welcomeWordCheckbox.isSelected() ? LedSetting.DISPLAY
				: LedSetting.NO_DISPLAY;
		ledSetting.setId(ledSettingId);
		ledSetting.setContent(content);
		ledSetting.setDisplay(display);
		return ledSetting;
	}

	private JFormattedTextField displayTimeTxt;
	private JTextField[] welcomeWordTxt = new JTextField[SETTGING_LENGTH];

	private JCheckBox[] welcomeWordCheckbox = new JCheckBox[SETTGING_LENGTH];

	private LedSettingDAO ledSettingDAO;

	public static final int SETTGING_LENGTH = 6;
}
