package com.vv.minerlamp;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.StackLayout;

import com.vv.minerlamp.comm.SerialComm;
import com.vv.minerlamp.dao.ChargingLogDAO;
import com.vv.minerlamp.dao.InfoItemDAO;
import com.vv.minerlamp.dao.LampRackDAO;
import com.vv.minerlamp.dao.LampUnitDAO;
import com.vv.minerlamp.dao.StaffDAO;
import com.vv.minerlamp.dao.UserDAO;
import com.vv.minerlamp.entity.InfoItem;
import com.vv.minerlamp.entity.User;
import com.vv.minerlamp.util.HibernateUtil;
import com.vv.minerlamp.util.PropertiesUtil;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

public class SysSettingDialog extends JDialog {
	public SysSettingDialog(MinerLampFrame owner) {
		this(owner, 0);
	}

	public SysSettingDialog(MinerLampFrame owner, int selectedPanelNo) {
		super(owner, "设置", true);
		setIconImage(SysConfiguration.sysIcon);
		this.owner = owner;
		setLayout(new GridBagLayout());
		JLabel descLabel = new JLabel(new ImageIcon(
				"resources/settingTitle.png"));
		add(descLabel,
				new GBC(0, 0).setWeight(100, 0).setFill(
						GridBagConstraints.HORIZONTAL));
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		List nodeList = new ArrayList();
		DefaultMutableTreeNode commNode = new DefaultMutableTreeNode("串口通信设置");
		nodeList.add(commNode);
	//	DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode("数据库设置");
	//	nodeList.add(dbNode);
		DefaultMutableTreeNode rackNode = new DefaultMutableTreeNode("充电架设置");
		nodeList.add(rackNode);
		DefaultMutableTreeNode userNode = new DefaultMutableTreeNode("用户设置");
		nodeList.add(userNode);
		DefaultMutableTreeNode useNode = new DefaultMutableTreeNode("充电架使用设置");
		nodeList.add(useNode);
		DefaultMutableTreeNode positionNode = new DefaultMutableTreeNode("岗位设置");
		nodeList.add(positionNode);
		DefaultMutableTreeNode timeoutNode = new DefaultMutableTreeNode("超时设置");
		nodeList.add(timeoutNode);
		final JTree functionTree = new JTree(nodeList.toArray());
		functionTree.setRowHeight(30);

		// functionTree.setBackground(Color.white);
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		functionTree.getSelectionModel().setSelectionMode(mode);

		functionTree.setPreferredSize(new Dimension(150, 400));
		leftPanel.add(
				functionTree,
				new GBC(0, 0).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new StackLayout());
		final JPanel commPanel = new CommPanel();
		final JPanel dbPanel = new Dbanel();
		dbPanel.setVisible(false);
		final JPanel rackPanel = new RackPanel();
		rackPanel.setVisible(false);
		final JPanel userPanel = new UserPanel();
		userPanel.setVisible(false);
		final JPanel usePanel = new UsePanel();
		usePanel.setVisible(false);
		final JPanel positionPanel = new PositionPanel();
		positionPanel.setVisible(false);
		final JPanel timeoutPanel = new TimeoutPanel();
		timeoutPanel.setVisible(false);
		rightPanel.add(commPanel, StackLayout.TOP);
	//	rightPanel.add(dbPanel, StackLayout.TOP);
		rightPanel.add(rackPanel, StackLayout.TOP);
		rightPanel.add(userPanel, StackLayout.TOP);
		rightPanel.add(usePanel, StackLayout.TOP);
		rightPanel.add(positionPanel, StackLayout.TOP);
		rightPanel.add(timeoutPanel, StackLayout.TOP);

		functionTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = functionTree.getSelectionPath();
				if (path == null) {
					return;
				}
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				String desc = (selectedNode.toString());
				if (desc.equals("串口通信设置")) {
					commPanel.setVisible(true);
					dbPanel.setVisible(false);
					rackPanel.setVisible(false);
					userPanel.setVisible(false);
					usePanel.setVisible(false);
					positionPanel.setVisible(false);
					timeoutPanel.setVisible(false);

				} else if (desc.equals("数据库设置")) {
					commPanel.setVisible(false);
					dbPanel.setVisible(true);
					rackPanel.setVisible(false);
					userPanel.setVisible(false);
					usePanel.setVisible(false);
					positionPanel.setVisible(false);
					timeoutPanel.setVisible(false);

				} else if (desc.equals("充电架设置")) {
					commPanel.setVisible(false);
					dbPanel.setVisible(false);
					rackPanel.setVisible(true);
					userPanel.setVisible(false);
					usePanel.setVisible(false);
					positionPanel.setVisible(false);
					timeoutPanel.setVisible(false);

				} else if (desc.equals("用户设置")) {
					commPanel.setVisible(false);
					dbPanel.setVisible(false);
					rackPanel.setVisible(false);
					userPanel.setVisible(true);
					usePanel.setVisible(false);
					positionPanel.setVisible(false);
					timeoutPanel.setVisible(false);

				} else if (desc.equals("充电架使用设置")) {
					commPanel.setVisible(false);
					dbPanel.setVisible(false);
					rackPanel.setVisible(false);
					userPanel.setVisible(false);
					usePanel.setVisible(true);
					positionPanel.setVisible(false);
					timeoutPanel.setVisible(false);

				} else if (desc.equals("岗位设置")) {
					commPanel.setVisible(false);
					dbPanel.setVisible(false);
					rackPanel.setVisible(false);
					userPanel.setVisible(false);
					usePanel.setVisible(false);
					positionPanel.setVisible(true);
					timeoutPanel.setVisible(false);

				} else if (desc.equals("超时设置")) {
					commPanel.setVisible(false);
					dbPanel.setVisible(false);
					rackPanel.setVisible(false);
					userPanel.setVisible(false);
					usePanel.setVisible(false);
					positionPanel.setVisible(false);
					timeoutPanel.setVisible(true);

				}

			}
		});
		functionTree.setSelectionRow(selectedPanelNo);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				false, leftPanel, rightPanel);
		add(splitPane,
				new GBC(0, 1).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));

		setResizable(false);
		setSize(480, 400);
		setLocation(Util.calculatePosition(owner, this));
		setVisible(true);
		splitPane.setDividerLocation(0.30);
	}

	class CommPanel extends JPanel {
		public CommPanel() {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("通信设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("选择串口："), new GBC(0, 0).setWeight(0, 0));
			panel.add(new JLabel("  波特率："), new GBC(0, 1).setWeight(0, 0));
			commNoCombo = new JComboBox(SerialComm.portNameList.toArray());
			commNoCombo.setSelectedItem(SysConfiguration.commPort);
			commNoCombo.setPreferredSize(new Dimension(180, 20));
			rateCombo = new JComboBox(new Integer[] { 2400, 4800, 9600 });
			rateCombo.setSelectedItem(SysConfiguration.baudRate);
			rateCombo.setPreferredSize(new Dimension(180, 20));
			panel.add(commNoCombo,
					new GBC(1, 0).setWeight(100, 0).setInsets(0, 3, 0, 0));
			panel.add(rateCombo,
					new GBC(1, 1).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(Util.makePrivButton(new ButtonAction("保存", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						boolean portChanged=false;
						String commPort = (String) commNoCombo
								.getSelectedItem();
						Integer baudRate = (Integer) rateCombo
								.getSelectedItem();
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "commPort",
								commPort.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "baudRate",
								baudRate.toString());
						if(SysConfiguration.commPort!=commPort||SysConfiguration.baudRate!=baudRate){
							portChanged=true;
						}
						SysConfiguration.commPort = commPort;
						SysConfiguration.baudRate = baudRate;
						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
						if(portChanged){
							JOptionPane.showMessageDialog(SysSettingDialog.this, "通信配置已更改，请重启智能管理系统");
							System.exit(0);
						}
					}

				}
			}, owner.getCurrentUser()), new GBC(0, 2, 2, 1).setWeight(100, 100)
					.setAnchor(GridBagConstraints.NORTHEAST).setInsets(25));
			add(panel, Util.fillParentPanel());
		}

		private JComboBox commNoCombo;
		private JComboBox rateCombo;
	}

	class Dbanel extends JPanel {
		public Dbanel() {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("数据库设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("数据库IP："), new GBC(0, 0).setWeight(0, 0));
			panel.add(new JLabel("    端口："), new GBC(0, 1).setWeight(0, 0));
			panel.add(new JLabel("  用户名："), new GBC(0, 2).setWeight(0, 0));
			panel.add(new JLabel("    密码："), new GBC(0, 3).setWeight(0, 0));
			ipTxt = new JTextField();
			ipTxt.setText(SysConfiguration.dbIp);
			ipTxt.setPreferredSize(new Dimension(180, 20));
			portTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
			portTxt.setValue(SysConfiguration.dbPort);
			portTxt.setPreferredSize(new Dimension(180, 20));
			userTxt = new JTextField();
			userTxt.setText(SysConfiguration.dbUserName);
			userTxt.setPreferredSize(new Dimension(180, 20));
			pwdTxt = new JPasswordField();
			pwdTxt.setText(SysConfiguration.dbPwd);
			pwdTxt.setPreferredSize(new Dimension(180, 20));
			panel.add(ipTxt,
					new GBC(1, 0).setWeight(100, 0).setInsets(0, 3, 0, 0));
			panel.add(portTxt,
					new GBC(1, 1).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(userTxt,
					new GBC(1, 2).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(pwdTxt,
					new GBC(1, 3).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(Util.makePrivButton(new ButtonAction("保存", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						String dbIp = ipTxt.getText();
						Integer dbPort = new Integer(portTxt.getText());
						String dbUserName = userTxt.getText();
						String dbPwd = pwdTxt.getText();
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.url",
								SysConfiguration.makeDbUrl(dbIp, dbPort));
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.username", dbUserName);
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.password", dbPwd);

						SysConfiguration.dbIp = dbIp;
						SysConfiguration.dbPort = dbPort;
						SysConfiguration.dbUserName = dbUserName;
						SysConfiguration.dbPwd = dbPwd;

						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
					}

				}
			}, owner.getCurrentUser()), new GBC(0, 4, 2, 1).setWeight(100, 100)
					.setAnchor(GridBagConstraints.NORTHEAST).setInsets(25));
			add(panel, Util.fillParentPanel());
		}

		private JTextField ipTxt;
		private JFormattedTextField portTxt;
		private JTextField userTxt;
		private JPasswordField pwdTxt;

	}

	class RackPanel extends JPanel {
		public RackPanel() {

			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("充电架设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("        充电架数："), new GBC(0, 0)
					.setWeight(0, 0).setAnchor(GridBagConstraints.WEST));
			panel.add(new JLabel("充电架充电座行数："), new GBC(0, 1).setWeight(0, 0)
					.setAnchor(GridBagConstraints.WEST));
			panel.add(new JLabel("充电架充电座列数："), new GBC(0, 2).setWeight(0, 0)
					.setAnchor(GridBagConstraints.WEST));
			panel.add(new JLabel("初始化进度："), new GBC(0, 4).setWeight(100, 0)
					.setAnchor(GridBagConstraints.WEST));
			rackCountTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());
			rackCountTxt.setValue(SysConfiguration.rackCount);
			rackCountTxt.setPreferredSize(new Dimension(180, 20));
			unitRowCountTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());
			unitRowCountTxt.setValue(SysConfiguration.rackRow);
			unitRowCountTxt.setPreferredSize(new Dimension(180, 20));
			unitColumnCountTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());
			unitColumnCountTxt.setValue(SysConfiguration.rackColumn);
			unitColumnCountTxt.setPreferredSize(new Dimension(180, 20));

			panel.add(rackCountTxt,
					new GBC(1, 0).setWeight(100, 0).setInsets(0, 3, 0, 0));
			panel.add(unitRowCountTxt, new GBC(1, 1).setWeight(100, 0)
					.setInsets(5, 3, 0, 0));
			panel.add(unitColumnCountTxt, new GBC(1, 2).setWeight(100, 0)
					.setInsets(5, 3, 0, 0));
			panel.add(Util.makePrivButton(new ButtonAction("保存设置", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						Integer rackCount = new Integer(rackCountTxt.getText());

						Integer rackRow = new Integer(unitRowCountTxt.getText());
						Integer rackColumn = new Integer(unitColumnCountTxt
								.getText());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "rackCount",
								rackCount.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "rackRow",
								rackRow.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "rackColumn",
								rackColumn.toString());

						SysConfiguration.rackCount = rackCount;
						SysConfiguration.rackRow = rackRow;
						SysConfiguration.rackColumn = rackColumn;

						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
					}
				}
			}, owner.getCurrentUser()), new GBC(0, 3, 2, 1).setWeight(100, 0)
					.setAnchor(GridBagConstraints.EAST).setInsets(5, 3, 0, 5));
			initProgressBar = new JProgressBar(0, 1);

			initProgressBar.setMinimum(0);
			initProgressBar.setMaximum(100);
			initProgressBar.setValue(0);
			initProgressBar.setStringPainted(true);

			panel.add(initProgressBar,
					new GBC(0, 5, 3, 1).setWeight(100, 0).setInsets(5, 3, 0, 0)
							.setFill(GridBagConstraints.HORIZONTAL));
			panel.add(Util.makePrivButton(new ButtonAction("初始化", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要初始化吗？该操作会清空数据库", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						activity = new SimulatedActivity(100);
						activity.execute();
						SysConfiguration.sysState=SysConfiguration.SYS_STATE_REBOOT;
					}
				}
			}, owner.getCurrentUser()), new GBC(0, 6, 3, 1).setWeight(100, 0)
					.setAnchor(GridBagConstraints.EAST).setInsets(5, 3, 0, 5));
			JPanel rackNamingPanel = new JPanel();
			rackNamingPanel
					.setBorder(BorderFactory.createTitledBorder("充电架命名"));
			rackNamingPanel.setLayout(new GridBagLayout());
			rackNamingPanel.add(new JLabel("充电架序号："),
					new GBC(0, 0).setWeight(0, 0));
			rackNamingPanel.add(new JLabel("充电架编号："),
					new GBC(0, 1).setWeight(0, 1));
			sequenceNoTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());
			sequenceNoTxt.setValue(SysConfiguration.rackSequence);
			sequenceNoTxt.setPreferredSize(new Dimension(180, 20));
			serialNoTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
			serialNoTxt.setValue(SysConfiguration.rackSerial);
			serialNoTxt.setPreferredSize(new Dimension(180, 20));
			rackNamingPanel.add(sequenceNoTxt, new GBC(1, 0).setWeight(100, 0)
					.setInsets(0, 1, 0, 0));
			rackNamingPanel.add(serialNoTxt, new GBC(1, 1).setWeight(100, 0)
					.setInsets(0, 1, 0, 0));
			rackNamingPanel.add(
					Util.makePrivButton(new ButtonAction("保存", null) {

						@Override
						public void actionPerformed(ActionEvent e) {
							int selection = JOptionPane.showConfirmDialog(
									SysSettingDialog.this, "确定要保存吗", "提示",
									JOptionPane.YES_NO_OPTION);
							if (selection == JOptionPane.YES_OPTION) {
								Integer rackSequence = new Integer(
										sequenceNoTxt.getText());

								Integer rackSerial = new Integer(serialNoTxt
										.getText());

								PropertiesUtil.writeProperties(
										SysConfiguration.FILE_PATH,
										"rackSequence", rackSequence.toString());
								PropertiesUtil.writeProperties(
										SysConfiguration.FILE_PATH,
										"rackSerial", rackSerial.toString());

								SysConfiguration.rackSequence = rackSequence;
								SysConfiguration.rackSerial = rackSerial;

								JOptionPane.showMessageDialog(
										SysSettingDialog.this, "保存成功");
							}

						}
					}, owner.getCurrentUser()),
					new GBC(0, 2, 2, 1).setWeight(100, 100)
							.setAnchor(GridBagConstraints.NORTHEAST)
							.setInsets(5, 3, 0, 5));
			//TODO 充电架命名无意义，不显示，用空面板占位
		//	panel.add(rackNamingPanel, new GBC(0, 7, 3, 1).setWeight(100, 100)
		//			.setFill(GridBagConstraints.BOTH));
			panel.add(new JPanel(), new GBC(0, 7, 3, 1).setWeight(100, 100)
								.setFill(GridBagConstraints.BOTH));
			add(panel, Util.fillParentPanel());
		}

		class SimulatedActivity extends SwingWorker<Void, Integer> {

			public SimulatedActivity(int t) {
				current = 0;
				target = t;
			}

			protected Void doInBackground() throws Exception {

				// while (current < target) {
				// Thread.sleep(100);
				// current++;
				// publish(current);
				// }
				int rackCount = new Integer(rackCountTxt.getText());
				int unitCount = new Integer(unitRowCountTxt.getText())
						* new Integer(unitColumnCountTxt.getText());
				LampUnitDAO lampUnitDAO = new LampUnitDAO();
				LampRackDAO lampRackDAO = new LampRackDAO();
				StaffDAO staffDAO = new StaffDAO();
				ChargingLogDAO chargingLogDAO = new ChargingLogDAO();

				Session session = HibernateUtil.getSessionFactory()
						.openSession();
				Transaction transaction = null;
				try {
					Thread.sleep(100);
					current += 20;
					publish(current);

					transaction = session.beginTransaction();
					lampRackDAO.batchSaveInCurrentSession(rackCount, session);
					Thread.sleep(100);
					current += 30;
					publish(current);
					lampUnitDAO.batchSaveInCurrentSession(rackCount, unitCount,
							session);
					staffDAO.deleteAllInCurrentSession(session);
					chargingLogDAO.deleteAllInCurrentSession(session);
					Thread.sleep(100);
					current += 50;
					publish(current);
					transaction.commit();

				} catch (HibernateException hibernateException) {
					transaction.rollback();
					hibernateException.printStackTrace();
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				} finally {
					session.close();
				}

				return null;
			}

			protected void process(List<Integer> chunks) {
				for (Integer chunk : chunks) {

					initProgressBar.setValue(chunk);
				}
			}

			protected void done() {
				JOptionPane.showMessageDialog(SysSettingDialog.this,
						"初始化成功，请重启管理系统");
				System.exit(0);
			}

			private int current;
			private int target;
		}

		private JFormattedTextField rackCountTxt;
		private JFormattedTextField unitRowCountTxt;
		private JFormattedTextField unitColumnCountTxt;
		private JProgressBar initProgressBar;

		private JFormattedTextField sequenceNoTxt;
		private JFormattedTextField serialNoTxt;
		private SimulatedActivity activity;
	}

	class UserPanel extends JPanel {
		public UserPanel() {
			userDAO = new UserDAO();
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("用户设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("      用户名："), new GBC(0, 0).setWeight(0, 0));
			panel.add(new JLabel("        口令："), new GBC(0, 1).setWeight(0, 0));
			panel.add(new JLabel("    重复口令："), new GBC(0, 2).setWeight(0, 0));
			panel.add(new JLabel("    用户类型："), new GBC(0, 3).setWeight(0, 0));
			panel.add(new JLabel("当前用户列表："), new GBC(0, 5, 2, 1)
					.setWeight(0, 0).setAnchor(GridBagConstraints.WEST));
			userTxt = new JTextField();
			userTxt.setPreferredSize(new Dimension(180, 20));
			pwdTxt = new JPasswordField();
			pwdTxt.setPreferredSize(new Dimension(180, 20));
			rpwdTxt = new JPasswordField();
			rpwdTxt.setPreferredSize(new Dimension(180, 20));
			typeCombo = new JComboBox(new String[] { "普通用户", "管理员" });
			typeCombo.setPreferredSize(new Dimension(180, 20));

			panel.add(
					userTxt,
					new GBC(1, 0).setWeight(100, 0)
							.setFill(GridBagConstraints.HORIZONTAL)
							.setInsets(0, 3, 0, 20));
			panel.add(
					pwdTxt,
					new GBC(1, 1).setWeight(100, 0)
							.setFill(GridBagConstraints.HORIZONTAL)
							.setInsets(5, 3, 0, 20));
			panel.add(
					rpwdTxt,
					new GBC(1, 2).setWeight(100, 0)
							.setFill(GridBagConstraints.HORIZONTAL)
							.setInsets(5, 3, 0, 20));
			panel.add(
					typeCombo,
					new GBC(1, 3).setWeight(100, 0)
							.setFill(GridBagConstraints.HORIZONTAL)
							.setInsets(5, 3, 0, 20));
			JPanel buttonPanel = new JPanel();
			FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
			layout.setVgap(10);
			buttonPanel.setLayout(layout);
			buttonPanel.add(Util.makePrivButton(new ButtonAction("新增", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					userId = null;

					userTxt.setText("");
					userTxt.setEnabled(true);
					pwdTxt.setText("");
					rpwdTxt.setText("");
					typeCombo.setSelectedIndex(0);
				}
			}, owner.getCurrentUser()));
			buttonPanel.add(Util.makePrivButton(new ButtonAction("删除用户", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要删除吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {

						User selectedUser = (User) userList.getSelectedValue();
						if (selectedUser == null) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "请先选择要删除的用户");
							return;
						}
						if (selectedUser.getName().equals("admin")) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this,
									"admin为系统保留用户，不允许被删除");
							return;
						}
						userDAO.deleteUser(selectedUser.getId());
						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"删除成功");
						userList.setListData(userDAO.listUsers().toArray());
					}
				}
			}, owner.getCurrentUser()));
			buttonPanel.add(Util.makePrivButton(new ButtonAction("保存修改", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {

						if (userId == null
								&& userDAO.queryByName(userTxt.getText()) != null) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "该用户名已存在");
							return;
						}
						if (pwdTxt.getText().startsWith(" ")
								|| pwdTxt.getText().endsWith(" ")) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "密码首尾请不要包含空格");
							return;
						}
						if (pwdTxt.getText().trim() != rpwdTxt.getText().trim()) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "两次输入密码不一致");
							return;
						}
						userDAO.save(userId, userTxt.getText(),
								pwdTxt.getText(), typeCombo.getSelectedIndex());
						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
						if (userId != null) {
							owner.getCurrentUser().setPwd(pwdTxt.getText());
						}
						userList.setListData(userDAO.listUsers().toArray());
					}
				}
			}, owner.getCurrentUser()));
			panel.add(buttonPanel, new GBC(0, 4, 2, 1).setWeight(100, 0)
					.setAnchor(GridBagConstraints.EAST).setInsets(5));

			JPanel userListPanel = new JPanel();
			userListPanel.setLayout(new GridBagLayout());
			userList = new JList(userDAO.listUsers().toArray());
			userList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					User selectUser = (User) userList.getSelectedValue();
					if (selectUser != null) {
						userId = selectUser.getId();
						userTxt.setText(selectUser.getName());
						userTxt.setEnabled(false);
						pwdTxt.setText(selectUser.getPwd());
						typeCombo.setSelectedIndex(selectUser.getPriv());
					}

				}
			});

			userListPanel
					.add(new JScrollPane(userList), Util.fillParentPanel());
			panel.add(userListPanel, new GBC(0, 6, 2, 1).setWeight(100, 100)
					.setFill(GridBagConstraints.BOTH));
			add(panel, Util.fillParentPanel());

		}

		private Long userId;
		private JTextField userTxt;
		private JPasswordField pwdTxt;
		private JPasswordField rpwdTxt;
		private JComboBox typeCombo;
		private JList userList;
		private UserDAO userDAO;
	}

	class UsePanel extends JPanel {
		public UsePanel() {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("充电架使用设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("充电次数限制："), new GBC(0, 1).setWeight(0, 0));
			panel.add(new JLabel("使用时长限制："), new GBC(0, 2).setWeight(0, 0));
			panel.add(new JLabel("次  "), new GBC(2, 1).setWeight(0, 0)
					.setInsets(5, 0, 0, 0));
			panel.add(new JLabel("小时"), new GBC(2, 2).setWeight(0, 0)
					.setInsets(5, 0, 0, 0));
			ButtonGroup buttonGroup = new ButtonGroup();
			
			countLimitRadioButton = new JRadioButton("限制使用次数");
		//	countLimitRadioButton.setEnabled(false);
			countLimitRadioButton
					.setSelected(SysConfiguration.isCountLimited == 0);
			buttonGroup.add(countLimitRadioButton);

			timeLimitRadioButton = new JRadioButton("限制使用时长");
	//		timeLimitRadioButton.setEnabled(false);
			timeLimitRadioButton
					.setSelected(SysConfiguration.isCountLimited == 1);
			buttonGroup.add(timeLimitRadioButton);

			countLimitTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());
			countLimitTxt.setValue(SysConfiguration.limitCount);
			countLimitTxt.setPreferredSize(new Dimension(150, 20));
			timeLimitTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());
		//	timeLimitTxt.setEnabled(false);
			timeLimitTxt.setValue(SysConfiguration.limitTime);
			timeLimitTxt.setPreferredSize(new Dimension(150, 20));

			panel.add(countLimitRadioButton, new GBC(0, 0).setWeight(0, 0)
					.setInsets(0, 0, 0, 5));
			panel.add(timeLimitRadioButton, new GBC(1, 0).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			panel.add(countLimitTxt,
					new GBC(1, 1).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(timeLimitTxt,
					new GBC(1, 2).setWeight(100, 0).setInsets(5, 3, 0, 0));

			panel.add(Util.makePrivButton(new ButtonAction("保存设置", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						Integer isCountLimited = countLimitRadioButton
								.isSelected() ? 0 : 1;
						Integer limitCount = new Integer(countLimitTxt
								.getText());
						Integer limitTime = new Integer(timeLimitTxt.getText());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "isCountLimited",
								isCountLimited.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "limitCount",
								limitCount.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "limitTime",
								limitTime.toString());
						SysConfiguration.isCountLimited = isCountLimited;
						SysConfiguration.limitCount = limitCount;
						SysConfiguration.limitTime = limitTime;
						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
					}

				}
			}, owner.getCurrentUser()), new GBC(0, 3, 3, 1).setWeight(100, 100)
					.setAnchor(GridBagConstraints.NORTHEAST).setInsets(25));
			add(panel, Util.fillParentPanel());
		}

		private JRadioButton countLimitRadioButton;
		private JRadioButton timeLimitRadioButton;
		private JFormattedTextField countLimitTxt;
		private JFormattedTextField timeLimitTxt;
	}

	class PositionPanel extends JPanel {
		public PositionPanel() {
			infoItemDAO = new InfoItemDAO();
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("岗位设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("工种列表："), new GBC(0, 0).setWeight(100, 0)
					.setAnchor(GridBagConstraints.WEST));
			workTypeList = new JList(infoItemDAO.qeueryByType(
					InfoItem.WORK_TYPE_ITEM).toArray());

			panel.add(new JScrollPane(workTypeList), new GBC(0, 1, 1, 2)
					.setWeight(100, 100).setFill(GridBagConstraints.BOTH));
			panel.add(Util.makePrivButton(new ButtonAction("添加工种", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					String input = JOptionPane.showInputDialog(
							SysSettingDialog.this, "添加工种");
					infoItemDAO.save(InfoItem.WORK_TYPE_ITEM_DESC,
							InfoItem.WORK_TYPE_ITEM, input);
					workTypeList.setListData(infoItemDAO.qeueryByType(
							InfoItem.WORK_TYPE_ITEM).toArray());
					JOptionPane
							.showMessageDialog(SysSettingDialog.this, "添加成功");

				}
			}, owner.getCurrentUser()), new GBC(1, 1).setWeight(0, 100)
					.setAnchor(GridBagConstraints.SOUTH).setInsets(5));
			panel.add(Util.makePrivButton(new ButtonAction("删除工种", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要删除吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						InfoItem selInfoItem = (InfoItem) workTypeList
								.getSelectedValue();
						if (selInfoItem == null) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "请先选择部门");
							return;
						}
						infoItemDAO.deleteInfoItem(selInfoItem.getId());
						workTypeList.setListData(infoItemDAO.qeueryByType(
								InfoItem.WORK_TYPE_ITEM).toArray());
					}

				}
			}, owner.getCurrentUser()), new GBC(1, 2).setWeight(0, 0)
					.setAnchor(GridBagConstraints.SOUTH).setInsets(5));

			panel.add(new JLabel("当期队组列表："), new GBC(0, 3).setWeight(100, 0)
					.setAnchor(GridBagConstraints.WEST));
			departmentList = new JList(infoItemDAO.qeueryByType(
					InfoItem.DEPARTMENT_ITEM).toArray());

			panel.add(new JScrollPane(departmentList), new GBC(0, 4, 1, 2)
					.setWeight(100, 100).setFill(GridBagConstraints.BOTH));
			panel.add(Util.makePrivButton(new ButtonAction("添加队组", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					String input = JOptionPane.showInputDialog(
							SysSettingDialog.this, "添加队组");
					infoItemDAO.save(InfoItem.DEPARTMENT_ITEM_DESC,
							InfoItem.DEPARTMENT_ITEM, input);
					departmentList.setListData(infoItemDAO.qeueryByType(
							InfoItem.DEPARTMENT_ITEM).toArray());
					JOptionPane
							.showMessageDialog(SysSettingDialog.this, "添加成功");
				}
			}, owner.getCurrentUser()), new GBC(1, 4).setWeight(0, 100)
					.setAnchor(GridBagConstraints.SOUTH).setInsets(5));
			panel.add(Util.makePrivButton(new ButtonAction("删除队组", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要删除吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						InfoItem selInfoItem = (InfoItem) departmentList
								.getSelectedValue();
						if (selInfoItem == null) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "请先选择队组");
							return;
						}
						infoItemDAO.deleteInfoItem(selInfoItem.getId());
						departmentList.setListData(infoItemDAO.qeueryByType(
								InfoItem.DEPARTMENT_ITEM).toArray());
					}
				}
			}, owner.getCurrentUser()), new GBC(1, 5).setWeight(0, 0)
					.setAnchor(GridBagConstraints.SOUTH).setInsets(5));

			add(panel, Util.fillParentPanel());
		}

		private JList workTypeList;
		private JList departmentList;
		private InfoItemDAO infoItemDAO;
	}

	class TimeoutPanel extends JPanel {
		public TimeoutPanel() {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("超时设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("超时时间长度："), new GBC(0, 0).setWeight(0, 0));

			panel.add(new JLabel("小时"), new GBC(2, 0).setWeight(0, 0)
					.setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 0, 0));

			timeoutTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
			timeoutTxt.setValue(SysConfiguration.timeOutTime);
			timeoutTxt.setPreferredSize(new Dimension(150, 20));

			panel.add(timeoutTxt,
					new GBC(1, 0).setWeight(100, 0).setInsets(5, 3, 0, 0));

			panel.add(Util.makePrivButton(new ButtonAction("保存设置", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						Integer timeOutTime = new Integer(timeoutTxt.getText());

						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "timeOutTime",
								timeOutTime.toString());

						SysConfiguration.timeOutTime = timeOutTime;

						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
					}
				}
			}, owner.getCurrentUser()), new GBC(0, 2, 3, 1).setWeight(100, 100)
					.setAnchor(GridBagConstraints.NORTHEAST).setInsets(25));
			add(panel, Util.fillParentPanel());
		}

		private JFormattedTextField timeoutTxt;
	}

	private MinerLampFrame owner;
}