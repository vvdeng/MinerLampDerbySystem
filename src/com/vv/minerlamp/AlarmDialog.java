package com.vv.minerlamp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.vv.minerlamp.dao.InfoItemDAO;
import com.vv.minerlamp.dao.StaffDAO;
import com.vv.minerlamp.entity.FunctionSetting;
import com.vv.minerlamp.entity.InfoItem;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.TableUtil;
import com.vv.minerlamp.util.Util;

class AlarmDialog extends JDialog {
	public AlarmDialog(final MinerLampFrame owner) {

		super(owner, "告警信息", true);
		infoItemDAO = new InfoItemDAO();
		staffDAO = new StaffDAO();
		setIconImage(SysConfiguration.sysIcon);
		JLabel discLabel = new JLabel(new ImageIcon("resources/alarmTitle.png"));

		add(discLabel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		add(panel, BorderLayout.CENTER);

		JPanel queryPanel = new JPanel();
		queryPanel.setBorder(BorderFactory.createEtchedBorder());
		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
		flowLayout.setHgap(20);
		queryPanel.setLayout(flowLayout);

		queryPanel.add(new JLabel("队组超时查询："));
		departmentCombo = new JComboBox(infoItemDAO.qeueryByType(
				InfoItem.DEPARTMENT_ITEM).toArray());

		departmentCombo.setPreferredSize(new Dimension(100, 18));
		queryPanel.add(departmentCombo);
		queryPanel.add(new JButton(new ButtonAction("超时查询", null) {

			@Override
			public void actionPerformed(ActionEvent e) {
				InfoItem selInfoItem = (InfoItem) departmentCombo
						.getSelectedItem();
				alarmTable.setModel(TableUtil.getModel(staffDAO
						.queryTimeoutModelByDepartmentId(selInfoItem.getId()),
						alarmColumnNames));

			}
		}));

		alarmTable = new JTable(new Object[][] {}, alarmColumnNames);

		JButton exit = new JButton("退出");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				dispose();
			}
		});

		JPanel functionPanel = new JPanel();
		functionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		functionPanel.add(new JButton(new ButtonAction("参数设置", null) {

			@Override
			public void actionPerformed(ActionEvent e) {
				SysSettingDialog dialog = new SysSettingDialog(owner,
						FunctionSetting.TIMEOUT_NO);

			}
		}));
		functionPanel.add(new JButton(new ButtonAction("报表导出", null) {

			@Override
			public void actionPerformed(ActionEvent e) {
				Util.createExcelFile(TableUtil.ALARM_FILENAME,
						TableUtil.ALARM_TITLE, alarmTable.getModel());

			}
		}));
		functionPanel.add(new JButton(new ButtonAction("确定", null) {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();

			}
		}));
		panel.add(
				queryPanel,
				new GBC(0, 0).setWeight(0, 0).setFill(
						GridBagConstraints.HORIZONTAL));
		panel.add(new JScrollPane(alarmTable), new GBC(0, 1)
				.setWeight(100, 100).setFill(GridBagConstraints.BOTH));
		panel.add(
				functionPanel,
				new GBC(0, 2).setWeight(0, 0).setFill(
						GridBagConstraints.HORIZONTAL));

		setResizable(false);
		setSize(560, 320);
		setLocation(Util.calculatePosition(owner, this));
	}

	private String[] alarmColumnNames = { "工号", "姓名", "队组", "下井时间" };
	private JComboBox departmentCombo;
	private JTable alarmTable;
	private InfoItemDAO infoItemDAO;
	private StaffDAO staffDAO;

}
