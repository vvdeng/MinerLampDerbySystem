package com.vv.minerlamp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.StackLayout;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.CremeSkin;

import com.vv.minerlamp.comm.CommCmdObject;
import com.vv.minerlamp.comm.SerialComm;
import com.vv.minerlamp.dao.ChargingLogDAO;
import com.vv.minerlamp.dao.InfoItemDAO;
import com.vv.minerlamp.dao.LampRackDAO;
import com.vv.minerlamp.dao.LampUnitDAO;
import com.vv.minerlamp.dao.StaffDAO;
import com.vv.minerlamp.dao.SysInfDAO;
import com.vv.minerlamp.entity.FunctionSetting;
import com.vv.minerlamp.entity.InfoItem;
import com.vv.minerlamp.entity.LampRack;
import com.vv.minerlamp.entity.LampUnit;
import com.vv.minerlamp.entity.RackStatistics;
import com.vv.minerlamp.entity.Staff;
import com.vv.minerlamp.entity.SysInf;
import com.vv.minerlamp.entity.User;
import com.vv.minerlamp.util.CommDataState;
import com.vv.minerlamp.util.CommUtil;
import com.vv.minerlamp.util.GlobalData;
import com.vv.minerlamp.util.StaffAction;
import com.vv.minerlamp.util.StaffState;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.TableUtil;
import com.vv.minerlamp.util.Util;

public class MinerLampFrame extends JFrame {
	public MinerLampFrame() {

		init();
		checkRegist();
		setTitle("矿灯充电架智能管理系统----" + titleExtra);
		setIconImage(SysConfiguration.sysIcon);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle bounds = new Rectangle(screenSize);
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				getGraphicsConfiguration());
		bounds.x += insets.left;
		bounds.y += insets.top;
		bounds.width -= insets.left + insets.right;
		bounds.height -= insets.top + insets.bottom;
		setBounds(bounds);
		// setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		// 设置界面风格
		// try {
		// UIManager.setLookAndFeel(DEFALUT_LOOKANDFEEL);
		// } catch (ClassNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (UnsupportedLookAndFeelException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		UIManager.getDefaults().put("TreeUI", BasicTreeUI.class.getName());
		SubstanceLookAndFeel.setSkin(new CremeSkin());

		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.updateComponentTreeUI(this);
		// 工具栏动作

		Action loginAction = new ToolAction("用户登录", new ImageIcon(
				"resources/1-1.png"), ToolActionContants.LOGIN) {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new LoginDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		};
		Action logoutAction = new ToolAction("用户注销", new ImageIcon(
				"resources/1-1.png"), ToolActionContants.LOGOUT) {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new LogoutDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		};
		Action dataBackupAction = new ToolAction("数据备份", new ImageIcon(
				"resources/2-0.png"), ToolActionContants.DATA_BACKUP) {
			@Override
			public void actionPerformed(ActionEvent event) {
				showDataBackUpDialog();
			}
		};

		Action lampStateAction = new ToolAction("使用状态", new ImageIcon(
				"resources/3-0.png"), ToolActionContants.DATA_BACKUP) {
			@Override
			public void actionPerformed(ActionEvent event) {
				mainPanel.setSelectedIndex(0);
			}
		};
		Action checkinginQueryAction = new ToolAction("考勤查询", new ImageIcon(
				"resources/4-0.png"), ToolActionContants.CHECKIN_QUERY) {
			@Override
			public void actionPerformed(ActionEvent event) {
				mainPanel.setSelectedIndex(2);
			}
		};
		Action lampStatisticsAction = new ToolAction("使用统计", new ImageIcon(
				"resources/5-0.png"), ToolActionContants.lAMP_STATISTIC) {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new LampStatisticsDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		};
		Action sysSettingAction = new ToolAction("系统设置", new ImageIcon(
				"resources/6-0.png"), ToolActionContants.SYS_SETTTING) {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new SysSettingDialog(MinerLampFrame.this);
				// dialog.setVisible(true);
			}
		};
		Action timeoutAction = new ToolAction("超时告警", new ImageIcon(
				"resources/7-0.png"), ToolActionContants.TIMEOUT) {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new AlarmDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		};
		Action ledSettingAction = new ToolAction("LED设置", new ImageIcon(
				"resources/8-0.png"), ToolActionContants.LED_SETTING) {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new LedSettingDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		};
		// Action commAction = new ToolAction("串口通信", new ImageIcon(
		// "resources/save.jpg"), ToolActionContants.COMM) {
		// @Override
		// public void actionPerformed(ActionEvent event) {
		//
		// }
		// };

		// 工具栏
		loginButton = createToolBarButton(loginAction);
		logoutButton = createToolBarButton(logoutAction);

		dataBackupButton = createToolBarButton(dataBackupAction);
		lampStateButton = createToolBarButton(lampStateAction);
		checkinginQueryButton = createToolBarButton(checkinginQueryAction);
		lampStatisticsButton = createToolBarButton(lampStatisticsAction);
		sysSettingButton = createToolBarButton(sysSettingAction);
		timeoutButton = createToolBarButton(timeoutAction);
		ledSettingButton = createToolBarButton(ledSettingAction);
		// JButton commButton = createToolBarButton(commAction);
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);
		bar.setOpaque(true);
		bar.add(loginButton);
		bar.add(logoutButton);
		// bar.add(dataBackupButton);
		bar.add(lampStateButton);
		bar.add(checkinginQueryButton);
		bar.add(lampStatisticsButton);
		bar.add(sysSettingButton);
		bar.add(timeoutButton);
		bar.add(ledSettingButton);
		// bar.add(commButton);

		add(bar, BorderLayout.NORTH);

		// 菜单栏

		sysMenu = new JMenu("系统管理");
		// JMenuItem loginItem = sysMenu.add(new MenuItemAction("登录") {
		// public void actionPerformed(ActionEvent event) {
		// JDialog dialog = new LoginDialog(MinerLampFrame.this);
		// dialog.setVisible(true);
		// }
		// });
		JMenuItem logoutItem = sysMenu.add(new AbstractAction("注销") {
			public void actionPerformed(ActionEvent event) {
				if (currentUser == null) {
					return;
				}
				JDialog dialog = new LogoutDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		});
		sysMenu.addSeparator();
		JMenuItem settingItem = sysMenu.add(new AbstractAction("系统设置") {
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new SysSettingDialog(MinerLampFrame.this);
			}
		});
		JMenuItem backupItem = sysMenu.add(new AbstractAction("数据备份") {
			@Override
			public void actionPerformed(ActionEvent event) {
				showDataBackUpDialog();
			}
		});
		JMenuItem clearTempItem = sysMenu.add(new AbstractAction("清理临时文件") {
			public void actionPerformed(ActionEvent event) {
				File filePath = new File(SysConfiguration.tempDir);
				if (!filePath.exists() || !filePath.isDirectory()) {
					return;
				}
				File[] fileList = filePath.listFiles();
				for (File file : fileList) {
					file.delete();
				}
				JOptionPane.showMessageDialog(MinerLampFrame.this, "清理完毕");
			}
		});
		sysMenu.addSeparator();
		JMenuItem exitItem = sysMenu.add(new AbstractAction("退出系统") {
			public void actionPerformed(ActionEvent event) {
				JDialog exitDialog = new ExitDialog(MinerLampFrame.this);
				exitDialog.setVisible(true);
			}
		});

		lampMenu = new JMenu("矿灯管理");
		JMenuItem lampSettingMenu = lampMenu.add(new AbstractAction("矿灯使用设置") {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new SysSettingDialog(MinerLampFrame.this,
						FunctionSetting.USE_NO);
			}
		});
		JMenuItem lampStateMenu = lampMenu.add(new AbstractAction("矿灯状态显示") {
			@Override
			public void actionPerformed(ActionEvent event) {
				mainPanel.setSelectedIndex(0);
			}
		});
		lampMenu.addSeparator();
		JMenuItem lampStaticsMenu = lampMenu.add(new AbstractAction("使用统计") {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new LampStatisticsDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		});
		JMenuItem lampChangeMenu = lampMenu.add(new AbstractAction("更换记录") {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new LampStatisticsDialog(MinerLampFrame.this,
						1);
				dialog.setVisible(true);
			}
		});

		staffMenu = new JMenu("员工管理");
		JMenuItem basicInfoMenu = staffMenu.add(new AbstractAction("员工基本信息") {
			@Override
			public void actionPerformed(ActionEvent event) {
				mainPanel.setSelectedIndex(1);
			}
		});
		JMenuItem checkinginMenu = staffMenu.add(new AbstractAction("考勤信息") {
			@Override
			public void actionPerformed(ActionEvent event) {
				mainPanel.setSelectedIndex(2);
			}
		});
		JMenuItem undergroundMenu = staffMenu.add(new AbstractAction("下井人员信息") {
			@Override
			public void actionPerformed(ActionEvent event) {
				mainPanel.setSelectedIndex(3);
			}
		});
		JMenuItem positiongSettingMenu = staffMenu
				.add(new AbstractAction("岗位") {
					@Override
					public void actionPerformed(ActionEvent event) {
						JDialog dialog = new SysSettingDialog(
								MinerLampFrame.this,
								FunctionSetting.POSITION_NO);
					}
				});

		alarmMenu = new JMenu("安全警报");
		JMenuItem timeoutItem = alarmMenu.add(new AbstractAction("超时告警") {
			@Override
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new AlarmDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		});

		helpMenu = new JMenu("帮助");
		JMenuItem aboutItem = helpMenu.add(new AbstractAction("关于") {
			public void actionPerformed(ActionEvent event) {
				JDialog dialog = new AboutDialog(MinerLampFrame.this);
				dialog.setVisible(true);
			}
		});

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(sysMenu);
		menuBar.add(lampMenu);
		menuBar.add(staffMenu);
		menuBar.add(alarmMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
		addRackBtn = new JButton("增加充电架数");// 提前至次数，为调用toolbarAndMenuEnabled(false);
		toolbarAndMenuEnabled(false);
		if (SerialComm.sysSerialComm.isSerialCommOk()) {
			commStateLabel = new BlueJLabel("串口通信正常");
		} else {
			if (SerialComm.sysSerialComm.isSerialCommBusy()) {
				commStateLabel = new BlueJLabel("串口被占用");
			} else {
				commStateLabel = new BlueJLabel("串口未连接");
			}

		}
		commStateLabel.setPreferredSize(new Dimension(100, 20));
		commInfoLabel = new BlueJLabel("通信端口：" + SysConfiguration.commPort
				+ "         波特率：" + SysConfiguration.baudRate);
		commInfoLabel.setPreferredSize(new Dimension(230, 20));
		operatorLabel = new BlueJLabel("操作员:");
		operatorLabel.setPreferredSize(new Dimension(120, 20));
		emptyLabel = new BlueJLabel("     ");
		emptyLabel.setPreferredSize(new Dimension(20, 20));
		updateStateLabel = new BlueJLabel("系统运行中");
		updateStateLabel.setPreferredSize(new Dimension(230, 20));
		sysStateLabel = new BlueJLabel("系统空闲");
		// sysStateLabel.setPreferredSize(new Dimension(200, 20));
		statePanel = new JPanel();
		statePanel.setLayout(new GridBagLayout());
		statePanel.setBackground(new Color(182, 230, 247));
		statePanel.add(
				commStateLabel,
				new GBC(0, 0).setWeight(0, 100).setFill(
						GridBagConstraints.VERTICAL));
		statePanel.add(
				commInfoLabel,
				new GBC(1, 0).setWeight(0, 100).setFill(
						GridBagConstraints.VERTICAL));
		statePanel.add(
				operatorLabel,
				new GBC(2, 0).setWeight(0, 100).setFill(
						GridBagConstraints.VERTICAL));
		statePanel.add(
				emptyLabel,
				new GBC(3, 0).setWeight(0, 100).setFill(
						GridBagConstraints.VERTICAL));
		statePanel.add(updateStateLabel, new GBC(4, 0).setWeight(0, 100)
				.setFill(GridBagConstraints.VERTICAL));
		statePanel.add(sysStateLabel, new GBC(5, 0).setWeight(100, 100)
				.setFill(GridBagConstraints.BOTH));

		add(statePanel, BorderLayout.SOUTH);

		leftPanel = new JPanel();

		leftPanel.setLayout(new GridBagLayout());
		rackPanel = new JPanel();
		rackPanel.setBorder(BorderFactory.createEtchedBorder());
		rackPanel.setLayout(new BorderLayout());
		rackPanel.add(new BlueJLabel("充电架"), BorderLayout.NORTH);
		JScrollPane rackScrollPane = new JScrollPane(makeRackTree());
		rackPanel.add(rackScrollPane, BorderLayout.CENTER);
		JPanel rackBtnPanel = new JPanel();
		rackBtnPanel.setPreferredSize(new Dimension(100, 30));

		addRackBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				AddRackDialog addRackDialog = new AddRackDialog(
						MinerLampFrame.this);
				addRackDialog.setVisible(true);
			}
		});
		rackBtnPanel.add(addRackBtn);
		rackPanel.add(rackBtnPanel, BorderLayout.SOUTH);

		// 统计信息
		statisticPane = new JPanel();
		statisticPane.setBorder(BorderFactory.createEtchedBorder());
		statisticPane.setLayout(new BorderLayout());
		JLabel statisticsTitleLabel = new BlueJLabel("  统计信息");

		statisticPane.add(statisticsTitleLabel, BorderLayout.NORTH);
		JPanel statInfoPanel = new JPanel();
		statInfoPanel.setLayout(new GridBagLayout());
		unitTotalCountLabel = new JLabel("0", SwingConstants.LEFT);
		rackTotalCountLabel = new JLabel("0", SwingConstants.LEFT);
		underGroundStaffCountLabel = new JLabel("0", SwingConstants.LEFT);
		ChargingStaffCountLabel = new JLabel("0", SwingConstants.LEFT);
		statInfoPanel.add(new JLabel("充电单元总数："), new GBC(0, 0)
				.setWeight(100, 0).setInsets(20, 0, 5, 0));
		statInfoPanel.add(new JLabel("  充电架总数："),
				new GBC(0, 1).setWeight(100, 0).setInsets(0, 0, 5, 0));
		statInfoPanel.add(new JLabel("井下作业总数："), new GBC(0, 2)
				.setWeight(100, 0).setInsets(0, 0, 5, 0));
		statInfoPanel.add(new JLabel("矿灯充电人数："),
				new GBC(0, 3).setWeight(100, 100).setInsets(0, 0, 20, 0)
						.setAnchor(GridBagConstraints.NORTH));
		statInfoPanel.add(unitTotalCountLabel, new GBC(1, 0).setWeight(100, 0)
				.setInsets(20, 0, 5, 0));
		statInfoPanel.add(rackTotalCountLabel, new GBC(1, 1).setWeight(100, 0));
		statInfoPanel.add(underGroundStaffCountLabel,
				new GBC(1, 2).setWeight(100, 0));
		statInfoPanel.add(
				ChargingStaffCountLabel,
				new GBC(1, 3).setWeight(100, 100).setAnchor(
						GridBagConstraints.NORTH));

		statisticPane.add(statInfoPanel, BorderLayout.CENTER);

		leftPanel
				.add(rackPanel,
						new GBC(0, 0, 1, 5).setFill(GridBagConstraints.BOTH)
								.setAnchor(GridBagConstraints.NORTH)
								.setWeight(100, 60));
		leftPanel.add(
				statisticPane,
				new GBC(0, 5, 1, 1).setFill(GridBagConstraints.BOTH).setWeight(
						100, 40));
		rightStackPanel = new JPanel();
		rightStackPanel.setLayout(new StackLayout());
		beforeLoginPanel = new JPanel();
		beforeLoginPanel.setLayout(new BorderLayout());
		JLabel loginImage = new JLabel(new ImageIcon("resources/splash.png"));
		beforeLoginPanel.add(loginImage, BorderLayout.CENTER);
		rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());

		rightStackPanel.add(beforeLoginPanel, StackLayout.TOP);
		rightStackPanel.add(rightPanel, StackLayout.TOP);
		rightPanel.setVisible(false);
		// 充电信息
		unitsPanel = new UnitsPanel();
		unitPopupMenu = new UnitPopupMenu();

		// 员工基本信息

		mainPanel = new JTabbedPane();
		mainPanel.addTab("充电信息", unitsPanel);
		mainPanel.addTab("员工基本信息", new StaffInfoPanel());
		mainPanel.addTab("考勤信息查询与统计", new CheckingInPanel());
		mainPanel.addTab("下井人员信息", new UnderGroundStaffInfoPanel());
		mainPanel.addTab("未按时上井人员实时信息", new TimeoutInfoPanel());
		// mainPanel.addTab("串口调试", new CommDebugPanel());

		rightPanel.add(mainPanel, new GBC(0, 0)
				.setFill(GridBagConstraints.BOTH).setWeight(100, 100));
		splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false,
				leftPanel, rightStackPanel);
		rackTree.setSelectionRow(0);

		add(splitPane1, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (currentUser == null) {
					System.exit(0);
				}
				JDialog exitDialog = new ExitDialog(MinerLampFrame.this);
				exitDialog.setVisible(true);

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

		refreshRackStatisticsLabelContent();

		setVisible(true);
		splitPane1.setDividerLocation(0.20);
		Timer commTimer = new Timer(2000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (SerialComm.sysSerialComm.commDataState != CommDataState.NOTHING) {

				}
				if (!GlobalData.cmdQueue.isEmpty()) {
					CommCmdObject co = GlobalData.cmdQueue.poll();
					if (co != null) {
						SerialComm.sysSerialComm.writeList(co.getDat());
					}

				} else {
					if (GlobalData.refreshSelRack == false) {
						GlobalData.refreshSelRack = true;
						setCommBusy(true);
						SerialComm.sysSerialComm.reqNextRackUnitInfo();
					} else {
						GlobalData.refreshSelRack = false;
						SerialComm.sysSerialComm.reqSelRackUnitInfo();

					}
				}

			}
		});

		backgroundActivity = new BackgroundActivity();
		backgroundActivity.execute();
		commTimer.start();
//	 postFrameShow();
	}

	private void init() {
		loadConfig();

		lampRackDAO = new LampRackDAO();
		lampUnitDAO = new LampUnitDAO();
		staffDAO = new StaffDAO();
		infoItemDAO = new InfoItemDAO();
		chargingLogDAO = new ChargingLogDAO();
		SerialComm.sysSerialComm = new SerialComm(this);

	}

	private void checkRegist() {
		sysInfDAO = new SysInfDAO();
		List<SysInf> sysInfList = sysInfDAO.listAll();
		if (sysInfList == null || sysInfList.size() < SysInf.INIT_DATA_COUNT) { 
			JOptionPane.showMessageDialog(MinerLampFrame.this,
					"系统配置错误，请联系软件提供商");
			System.exit(0);
		}
		for (SysInf sysInf : sysInfList) {
			GlobalData.sysInfoMap.put(sysInf.getCat(), sysInf);
		}
		SysInf regist = GlobalData.sysInfoMap.get(SysInf.CAT_USER_REGIST);
		if (regist.getVal().equals(SysInf.VAL_REGISTED)) { // 已经注册
			titleExtra = "注册版";
			return;
		} else if (regist.getVal().equals(SysInf.VAL_TRY_COUNT)) {
			Integer totalCount = GlobalData.sysInfoMap.get(
					SysInf.CAT_LIMIT_COUNT).getVal();
			SysInf usedCountInf = GlobalData.sysInfoMap
					.get(SysInf.CAT_USED_COUNT);
			if (usedCountInf == null) {
				titleExtra = "剩余使用次数" + totalCount + "次";
				//用户登录后再减少试用次数
				/*
				SysInf inf = new SysInf();
				inf.setCat(SysInf.CAT_USED_COUNT);
				inf.setVal(1);
				sysInfDAO.save(inf);
				*/
			} else {
				titleExtra = "剩余使用次数" + (totalCount - usedCountInf.getVal())
						+ "次";
				if (totalCount - usedCountInf.getVal() <= 0) {
					reachLimited = true; // 达到使用次数限制
				}
				//用户登录后再减少试用次数
		/*		SysInf inf = new SysInf();
				inf.setCat(SysInf.CAT_USED_COUNT);
				inf.setVal(usedCountInf.getVal() + 1);
				sysInfDAO.update(inf);
			*/
			}

		} else if (regist.getVal().equals(SysInf.VAL_TRY_DAY)) {
			Integer totalDay = GlobalData.sysInfoMap.get(SysInf.CAT_LIMIT_DAY)
					.getVal();
		}

	}
	public void postFrameShow() {
	/*
		 int delay = 5 * 1000; // milliseconds
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				if (GlobalData.machineCode!=null&&GlobalData.machineCode.length()==GlobalData.MACHINE_CODE_LEN) {
					
					SysInf machineCodeInf=GlobalData.sysInfoMap.get(SysInf.CAT_MACHINE_CODE);
					//以下情况为新创建的数据库或者从其他机器上还原过来的数据库
					if(machineCodeInf==null){
						System.out.println("insert machineCode="+GlobalData.machineCode);
						SysInf inf=new SysInf();
						inf.setCat(SysInf.CAT_MACHINE_CODE);
						inf.setDescr(GlobalData.machineCode);
						sysInfDAO.save(inf);
						GlobalData.sysInfoMap.put(SysInf.CAT_MACHINE_CODE,inf);
						if(GlobalData.sysInfoMap.get(SysInf.CAT_USER_REGIST).equals(SysInf.VAL_REGISTED)){
							inf=new SysInf();
							inf.setCat(SysInf.CAT_USER_REGIST);
							inf.setVal(SysInf.VAL_TRY_COUNT);
							sysInfDAO.update(inf);
							return;
						}
						checkRegistTimer.stop();
					}
					else if(!GlobalData.machineCode.equals(machineCodeInf.getDescr())){
						System.out.println("update machineCode="+GlobalData.machineCode);
						SysInf inf=new SysInf();
						inf.setCat(SysInf.CAT_MACHINE_CODE);
						inf.setDescr(GlobalData.machineCode);
						sysInfDAO.update(inf);
						GlobalData.sysInfoMap.put(SysInf.CAT_MACHINE_CODE,inf);
						inf=new SysInf();
						inf.setCat(SysInf.CAT_MACHINE__OLD_CODE);
						inf.setDescr(GlobalData.machineCode);
						sysInfDAO.save(inf);
						
						if(GlobalData.sysInfoMap.get(SysInf.CAT_USER_REGIST).equals(SysInf.VAL_REGISTED)){
							inf=new SysInf();
							inf.setCat(SysInf.CAT_USER_REGIST);
							inf.setVal(SysInf.VAL_TRY_COUNT);
							sysInfDAO.update(inf);
							return;
						}
						checkRegistTimer.stop();
					}
					
				}
			}
		};
		
		checkRegistTimer=new Timer(delay, taskPerformer);
		checkRegistTimer.start();
	*/
	}
	/*
	private void postFrameShow() {
		if (SerialComm.sysSerialComm.isSerialCommOk()) {
			setCommBusy(true);
			SerialComm.sysSerialComm.reqUnitInfo();
		}
		// int delay = 500 * 60; // milliseconds
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (SerialComm.sysSerialComm.isSerialCommOk()) {
					setCommBusy(true);
					SerialComm.sysSerialComm.reqUnitInfo();
				}
			}
		};
		new Timer(SysConfiguration.delay, taskPerformer).start();
	}
	*/

	private void loadConfig() {
		SysConfiguration.init();
	}

	public JButton createToolBarButton(Action action) {
		JButton button = new JButton(action);
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setOpaque(true);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		return button;
	}

	public JPanel createUnitPanel(final UnitAction action) {
		JPanel unitPanel = new JPanel();
		unitPanel.setBorder(BorderFactory.createEtchedBorder());
		BorderLayout bLayout = new BorderLayout();
		unitPanel.setLayout(bLayout);
		unitPanel.add(createUnitButton(action), BorderLayout.CENTER);
		JLabel infoLabel = new JLabel();
		// infoLabel.setBackground(new Color(82, 230, 247));
		// infoLabel.setBackground(new Color(182, 230, 247));
		infoLabel.setBackground(new Color(239, 240, 234));
		infoLabel.setFont(new Font("TimesRoman", Font.BOLD, 12));
		infoLabel.setOpaque(true);

		infoLabel.setHorizontalAlignment(JLabel.CENTER);
		infoLabel.setText("<html><center><strong>"
				+ action.getValue(UnitAction.STAFF_NAME)
				+ "</strong></center><ceneter><strong>"
				+ action.getValue(UnitAction.STAFF_DEPART)
				+ "</strong></center></html>");
		unitPanel.add(infoLabel, BorderLayout.SOUTH);

		return unitPanel;
	}

	public JButton createUnitButton(final UnitAction action) {
		JButton button = new JButton(action);
		button.setVerticalTextPosition(JButton.TOP);
		button.setFocusPainted(false);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setBorder(null);
		button.setMargin(new Insets(0, 0, 30, 0));
		// button.setContentAreaFilled(true);
		// button.setContentAreaFilled(true);
		// button.setBorder(BorderFactory.createEtchedBorder());
		// button.setComponentPopupMenu(new UnitPopupMenu((Long) action
		// .getValue(UnitAction.RACK_ID), (Long) action
		// .getValue(UnitAction.UNIT_ID), (Integer) action
		// .getValue(UnitAction.UNIT_STATE)));

		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JButton button = (JButton) e.getSource();
					unitPopupMenu.setRackId((Long) button.getAction().getValue(
							UnitAction.RACK_ID));
					unitPopupMenu.setLampNo((Long) button.getAction().getValue(
							UnitAction.UNIT_ID));
					unitPopupMenu.setUnitState((Integer) button.getAction()
							.getValue(UnitAction.UNIT_STATE));
					unitPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if ((e.getClickCount() == 2)
						&& (e.getButton() == MouseEvent.BUTTON1)) {
					JButton button = (JButton) e.getSource();
					JDialog dialog = new StaffInfoDialog(MinerLampFrame.this,
							(Long) button.getAction().getValue(
									UnitAction.UNIT_ID), (Long) button
									.getAction().getValue(UnitAction.RACK_ID),
							(Integer) button.getAction().getValue(
									UnitAction.UNIT_STATE));
					dialog.setVisible(true);
				}
			}

		});
		return button;
	}

	public JLabel createMixedLabel(String text, ImageIcon icon) {
		JLabel label = new JLabel();
		label.setIcon(icon);
		label.setText(text);
		label.setVerticalTextPosition(JButton.BOTTOM);
		label.setHorizontalTextPosition(JButton.CENTER);

		return label;
	}

	public JTree makeRackTree() {

		final List<LampRack> lampRackList = lampRackDAO.getAll();

		// List nodeList = new ArrayList();
		// DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		// for (LampRack lampRack : lampRackList) {
		// DefaultMutableTreeNode node = new DefaultMutableTreeNode(lampRack);
		// nodeList.add(node);
		// rootNode.add(node);
		// }
		//
		// rackTreeModel = new DefaultTreeModel(rootNode);
		//
		// rackTree = new JTree(rackTreeModel);
		rackTree = new JTree(lampRackList.toArray());
		rackTree.setRowHeight(20);
		rackTree.setCellRenderer(new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {

				super.getTreeCellRendererComponent(tree, value, sel, expanded,
						leaf, row, hasFocus);

				setIcon(new ImageIcon(SysConfiguration.sysIcon));

				return this;
			}

			public Icon getClosedIcon() {
				// 返回你需要的图标
				return new ImageIcon(SysConfiguration.sysIcon);

			}
		});

		// rackTree.setRowHeight(25);

		// rackTree.setRootVisible(false);
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		rackTree.getSelectionModel().setSelectionMode(mode);
		rackTree.putClientProperty("JTree.lineStyle", "Angled");
		rackTree.setPreferredSize(new Dimension(180, 400));
		rackTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				refreshLampUnits();
				refreshRackStatisticsLabelContent();

			}
		});

		return rackTree;

	}

	public void refreshLampRackTree() {
		/*
		 * final List<LampRack> lampRackList = lampRackDAO.getAll(); List
		 * nodeList = new ArrayList(); DefaultMutableTreeNode rootNode = new
		 * DefaultMutableTreeNode(); for (LampRack lampRack : lampRackList) {
		 * DefaultMutableTreeNode node = new DefaultMutableTreeNode(lampRack);
		 * nodeList.add(node); rootNode.add(node); }
		 * 
		 * rackTreeModel = new DefaultTreeModel(rootNode);
		 * 
		 * rackTree = new JTree(rackTreeModel); rackTreeModel.reload();
		 * SwingUtilities.updateComponentTreeUI(rackTree);
		 */

	}

	public void refreshLampUnits() {

		refreshLampUnitsWithSelectRack(true);
	}

	public void refreshLampUnitsWithSelectRack(boolean flag) {

		LampRack selectedLampPack = getSelectedLampPack();
		System.out.println("selectedLampRack=" + selectedLampPack);
		if (selectedLampPack == null) {
			return;
		}
		GlobalData.selRackId = selectedLampPack.getId().intValue();
		if (flag) {
			// mainPanel.setSelectedIndex(0);
		}

		unitsPanel.refreshUnit(selectedLampPack.getId());
	}

	public LampRack getSelectedLampPack() {
		TreePath path = rackTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		LampRack selectedLampPack = (LampRack) selectedNode.getUserObject();
		return selectedLampPack;
	}

	public void refreshRackStatisticsLabelContent() {
		LampRack selectedLampRack = getSelectedLampPack();

		Integer rackUnitCount = SysConfiguration.rackRow
				* SysConfiguration.rackColumn;
		RackStatistics totalRackStatistics = new RackStatistics();
		RackStatistics rackStatistics = new RackStatistics();
		lampUnitDAO.queryRackStatisticsModel(
				selectedLampRack != null ? selectedLampRack.getId() : 0,
				totalRackStatistics, rackStatistics);
		if (selectedLampRack != null) {
			rackNoLabel.setText(selectedLampRack.getId().toString());
			idleUintCountLabel
					.setText(rackStatistics.getIdleCount().toString());

			chargingUnitCountLabel.setText(rackStatistics.getChargingCount()
					.toString());
			undergroundStaffLabel.setText(rackStatistics.getUndergroundCount()
					.toString());
			faultUnintLabel.setText(rackStatistics.getErrorCount().toString());
			fullUnitLabel.setText(rackStatistics.getFullCount().toString());
		}

		Integer uintTotalCount = SysConfiguration.rackCount * rackUnitCount;
		unitTotalCountLabel.setText(uintTotalCount.toString());
		rackTotalCountLabel.setText(SysConfiguration.rackCount.toString());
		underGroundStaffCountLabel.setText(totalRackStatistics
				.getUndergroundCount().toString());
		ChargingStaffCountLabel.setText(totalRackStatistics.getChargingCount()
				.toString());
	}

	public void showDataBackUpDialog() {
		JFileChooser pathChooser = new JFileChooser();
		pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = pathChooser.showSaveDialog(MinerLampFrame.this);
		if (flag == JFileChooser.APPROVE_OPTION) {
			int execResult = -1;
			try {
				String path = pathChooser.getSelectedFile().getAbsolutePath();
				if (!path.endsWith("\\")) {
					path += "\\";
				}
				path += SysConfiguration.backupFileName;
				execResult = Util.backup(SysConfiguration.dbUrl,
						SysConfiguration.dbUserName, SysConfiguration.dbPwd,
						SysConfiguration.dbName, path);
				if (execResult == 0) {
					JOptionPane.showMessageDialog(MinerLampFrame.this, "备份成功");
				} else {
					JOptionPane.showMessageDialog(MinerLampFrame.this,
							"备份失败，请重启系统后再次尝试");
				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(MinerLampFrame.this,
						"备份失败，请重启系统后再次尝试");
				ex.printStackTrace();
			} catch (InterruptedException ex) {
				JOptionPane.showMessageDialog(MinerLampFrame.this,
						"备份失败，请重启系统后再次尝试");
				ex.printStackTrace();
			}
		}
	}

	public void showRightPanel(boolean flag) {
		if (flag) {
			rightPanel.setVisible(true);
			beforeLoginPanel.setVisible(false);
		} else {
			rightPanel.setVisible(false);
			beforeLoginPanel.setVisible(true);
		}
	}

	public void toolbarAndMenuEnabled(boolean flag) {
		if (flag) {
			loginButton.setVisible(false);
			logoutButton.setVisible(true);
			dataBackupButton.setEnabled(true);
			lampStateButton.setEnabled(true);
			checkinginQueryButton.setEnabled(true);
			lampStatisticsButton.setEnabled(true);
			sysSettingButton.setEnabled(true);
			timeoutButton.setEnabled(true);
			ledSettingButton.setEnabled(true);
			sysMenu.setEnabled(true);
			lampMenu.setEnabled(true);
			staffMenu.setEnabled(true);
			alarmMenu.setEnabled(true);
			helpMenu.setEnabled(true);
			addRackBtn.setEnabled(true);
		} else {
			if (reachLimited == false) {
				loginButton.setVisible(true);
			} else {
				loginButton.setVisible(false);
			}
			logoutButton.setVisible(false);
			dataBackupButton.setEnabled(false);
			lampStateButton.setEnabled(false);
			checkinginQueryButton.setEnabled(false);
			lampStatisticsButton.setEnabled(false);
			sysSettingButton.setEnabled(false);
			timeoutButton.setEnabled(false);
			ledSettingButton.setEnabled(false);

			sysMenu.setEnabled(false);
			lampMenu.setEnabled(false);
			staffMenu.setEnabled(false);
			alarmMenu.setEnabled(false);
			helpMenu.setEnabled(false);
			addRackBtn.setEnabled(false);
		}
	}

	public void toolbarEnabled(boolean flag) {
		if (flag) {
			loginButton.setIcon(new ImageIcon("resources/1-0.png"));
			dataBackupButton.setIcon(new ImageIcon("resources/2-1.png"));
			lampStateButton.setIcon(new ImageIcon("resources/3-1.png"));
			checkinginQueryButton.setIcon(new ImageIcon("resources/4-1.png"));
			lampStatisticsButton.setIcon(new ImageIcon("resources/5-1.png"));
			sysSettingButton.setIcon(new ImageIcon("resources/6-1.png"));
			timeoutButton.setIcon(new ImageIcon("resources/7-1.png"));
			ledSettingButton.setIcon(new ImageIcon("resources/8-1.png"));

		} else {
			loginButton.setIcon(new ImageIcon("resources/1-1.png"));
			dataBackupButton.setIcon(new ImageIcon("resources/2-0.png"));
			lampStateButton.setIcon(new ImageIcon("resources/3-0.png"));
			checkinginQueryButton.setIcon(new ImageIcon("resources/4-0.png"));
			lampStatisticsButton.setIcon(new ImageIcon("resources/5-0.png"));
			sysSettingButton.setIcon(new ImageIcon("resources/6-0.png"));
			timeoutButton.setIcon(new ImageIcon("resources/7-0.png"));
			ledSettingButton.setIcon(new ImageIcon("resources/8-0.png"));

		}
	}

	public void notifyActivity() {
		synchronized (backgroundActivity) {
			backgroundActivity.notify();
		}
	}

	public int changeStateIfNecessaryWithReturnAction(LampUnit lampUnit,
			int newState) {
		int action = StaffAction.NOTHING_TO_DO;
		int oldState = lampUnit.getState();
		if (oldState == newState || newState == StaffState.LAMP_NULL) {
			return action;
		}
		if (oldState != StaffState.STAFF_UNDEFINED) {
			switch (newState) {
			case StaffState.LAMP_ERROR:
				action = StaffAction.ERROR_HAPPENS;
				break;
			case StaffState.LAMP_FULL:
				action = StaffAction.CHARGING_OK;
				/*
				 * if (oldState == StaffState.LAMP_CHARGING) { action =
				 * StaffAction.CHARGING_OK; } else if (oldState ==
				 * StaffState.STAFF_UNDERGROUND || oldState ==
				 * StaffState.STAFF_DEFINED) { action =
				 * StaffAction.BAT_ALREADY_FULL; }
				 */
				break;
			case StaffState.LAMP_CHARGING:
				if (oldState == StaffState.STAFF_UNDERGROUND
						|| oldState == StaffState.STAFF_DEFINED
						|| oldState == StaffState.LAMP_ERROR
						|| oldState == StaffState.LAMP_OFFLINE) {
					action = StaffAction.PUT_ON;
				} else if (oldState == StaffState.LAMP_FULL) {
					// action = StaffAction.FIRSTTIME_CHARGING;
				}
				break;
			case StaffState.STAFF_UNDERGROUND:
				action = StaffAction.TAKE_AWAY;
				/*
				 * if (oldState == StaffState.LAMP_CHARGING || oldState ==
				 * StaffState.LAMP_FULL) { action = StaffAction.TAKE_AWAY; }
				 * else if (oldState == StaffState.LAMP_ERROR) { action =
				 * StaffAction.ERROR_REPARED; } else if (oldState ==
				 * StaffState.LAMP_OFFLINE) { action = StaffAction.ONLINE; }
				 */
				break;
			case StaffState.LAMP_OFFLINE:
				action = StaffAction.OFFLINE;
				break;
			default:
				break;
			}
		}
		return action;
		/*
		 * if (oldState == StaffState.STAFF_UNDEFINED) { return false; } else if
		 * ((oldState == StaffState.STAFF_DEFINED || oldState ==
		 * StaffState.STAFF_UNDERGROUND) && newState == StaffState.LAMP_FULL) {
		 * return false; } else if ((oldState == StaffState.LAMP_FULL) &&
		 * newState == StaffState.LAMP_CHARGING) { return false; } else { //
		 * lampUnit.setState(newState); return true; }
		 */
	}

	public void setCommBusy(boolean busy) {
		if (busy) {
			// commStateLabel;
			// operatorLabel;
			// emptyLabel;
			updateStateLabel.setText("尝试更新灯架，请稍等。。。");
			sysStateLabel.setText("系统通信中");
			// refreshRackButton.setEnabled(false);

		} else {
			updateStateLabel.setText("灯架更新完毕");
			sysStateLabel.setText("系统空闲");
			// refreshRackButton.setEnabled(true);
		}
	}

	class ToolAction extends AbstractAction {
		public ToolAction(String name, Icon icon, int c) {
			putValue(Action.NAME, name);
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, name);
			putValue("action", c);
		}

		public void actionPerformed(ActionEvent event) {
			int actionType = (Integer) getValue("action");
		}
	}

	class UnitAction extends AbstractAction {
		public UnitAction(LampUnit rackUnit, int simualVal) {
			putValue(Action.NAME,
					rackUnit.getRackId() + "-" + rackUnit.getLampNo());
			putValue(Action.SMALL_ICON,
					Util.getIconFromState(rackUnit.getState()));
			putValue(Action.SHORT_DESCRIPTION, "模拟量：" + simualVal);
			putValue(UNIT_ID, rackUnit.getLampNo());
			putValue(RACK_ID, rackUnit.getRackId());

			putValue(UNIT_STATE, rackUnit.getState());
			putValue(STAFF_NAME, rackUnit.getStaffName());
			putValue(STAFF_DEPART, rackUnit.getStaffDepart());
			putValue(STAFF_WORKTYPE, rackUnit.getStaffWorkType());
		}

		public void actionPerformed(ActionEvent event) {

		}

		public static final String UNIT_ID = "id";
		public static final String RACK_ID = "rackId";
		public static final String UNIT_STATE = "unitState";
		public static final String STAFF_NAME = "staffName";
		public static final String STAFF_DEPART = "staffDepart";
		public static final String STAFF_WORKTYPE = "staffWorkType";
	}

	class UnitsPanel extends JPanel {
		public UnitsPanel() {
			setLayout(new GridBagLayout());
			unitsInfoPanel = new JPanel();
			GridLayout unitsInfoPanelLayout = new GridLayout(
					SysConfiguration.rackRow, SysConfiguration.rackColumn);
			unitsInfoPanelLayout.setVgap(15);
			unitsInfoPanelLayout.setHgap(2);
			unitsInfoPanel.setLayout(unitsInfoPanelLayout);
			// unitList = lampUintDAO.getUnitsByRackId(new Long(1));
			//
			// for (LampUnit lampUnit : unitList) {
			// unitsInfoPanel.add(createUnitButton(new UnitAction(lampUnit)));
			// }

			JPanel rackStatePanel = new JPanel();
			rackStatePanel.setLayout(new GridBagLayout());
			rackNoLabel = new JLabel("0");
			idleUintCountLabel = new JLabel("0");
			chargingUnitCountLabel = new JLabel("0");
			undergroundStaffLabel = new JLabel("0");
			faultUnintLabel = new JLabel("0");
			fullUnitLabel = new JLabel("0");

			refreshAllRackButton = Util.makeButton(new ButtonAction("全部刷新",
					null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (SerialComm.sysSerialComm.isSerialCommOk()) {
						setCommBusy(true);
						SerialComm.sysSerialComm.reqUnitInfo();
					}
				}
			});
			refreshRackButton = Util.makeButton(new ButtonAction("刷新", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (SerialComm.sysSerialComm.isSerialCommOk()) {
						setCommBusy(true);
						SerialComm.sysSerialComm
								.reqSingleUnitInfo(getSelectedLampPack()
										.getId());
					}
				}
			});
			JPanel figurePanel = new JPanel();
			figurePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			figurePanel.add(createMixedLabel("未关联", new ImageIcon(
					"resources/battery_notuse.png")));
			figurePanel.add(createMixedLabel("下井", new ImageIcon(
					"resources/battery_empty.png")));
			figurePanel.add(createMixedLabel("充电中", new ImageIcon(
					"resources/battery_charging.gif")));
			figurePanel.add(createMixedLabel("充满", new ImageIcon(
					"resources/battery_full.png")));
			figurePanel.add(createMixedLabel("故障", new ImageIcon(
					"resources/battery_error.png")));
			figurePanel.add(createMixedLabel("掉线", new ImageIcon(
					"resources/offline.gif")));

			rackStatePanel.add(new JLabel("充电架号："),
					new GBC(0, 0).setInsets(0, 20, 0, 0).setWeight(0, 0));
			rackStatePanel.add(new JLabel("空闲单元："),
					new GBC(2, 0).setWeight(0, 0));
			rackStatePanel.add(new JLabel("充电单元："),
					new GBC(4, 0).setWeight(0, 0));
			rackStatePanel.add(new JLabel("下井人数："),
					new GBC(0, 1).setInsets(0, 20, 0, 0).setWeight(0, 0));
			rackStatePanel.add(new JLabel("故障单元："),
					new GBC(2, 1).setWeight(0, 0));
			rackStatePanel.add(new JLabel("充满单元："),
					new GBC(4, 1).setWeight(0, 0));
			rackStatePanel.add(rackNoLabel, new GBC(1, 0).setWeight(0, 0)
					.setInsets(0, 0, 0, 20));
			rackStatePanel.add(idleUintCountLabel, new GBC(3, 0)
					.setWeight(0, 0).setInsets(0, 0, 0, 20));
			rackStatePanel.add(chargingUnitCountLabel,
					new GBC(5, 0).setWeight(0, 0));
			rackStatePanel.add(undergroundStaffLabel,
					new GBC(1, 1).setWeight(0, 0).setInsets(0, 0, 0, 20));
			rackStatePanel.add(faultUnintLabel, new GBC(3, 1).setWeight(0, 0)
					.setInsets(0, 0, 0, 20));
			rackStatePanel.add(fullUnitLabel, new GBC(5, 1).setWeight(0, 0));
			// rackStatePanel.add(refreshRackButton, new GBC(7, 1).setWeight(0,
			// 0)
			// .setInsets(0, 20, 0, 0));
			// rackStatePanel.add(refreshAllRackButton,
			// new GBC(6, 1).setWeight(0, 0).setInsets(0, 20, 0, 0));

			rackStatePanel.add(figurePanel, new GBC(8, 0,
					GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER)
					.setWeight(100, 100).setFill(GridBagConstraints.BOTH)
					.setInsets(20));

			add(new JScrollPane(unitsInfoPanel),
					new GBC(0, 0).setFill(GridBagConstraints.BOTH).setWeight(
							100, 90));
			add(rackStatePanel,
					new GBC(0, 1).setFill(GridBagConstraints.HORIZONTAL)
							.setWeight(100, 15));
		}

		public void refreshUnit(Long rackId) {
			System.out.println("refreshRack rackId=" + rackId);
			unitList = lampUnitDAO.getUnitsByRackId(rackId);
			if (unitsInfoPanel.getComponentCount() == SysConfiguration.rackRow
					* SysConfiguration.rackColumn) {

				for (int i = 0; i < unitsInfoPanel.getComponentCount(); i++) {
					JPanel uPanel = (JPanel) unitsInfoPanel.getComponent(i);
					JButton button = (JButton) uPanel.getComponent(0);
					JLabel label = (JLabel) uPanel.getComponent(1);
					LampUnit lampUnit = unitList.get(i);
					int simualVal = 0;
					if ((StaffState.rackPacket != null)
							&& (StaffState.rackPacket.getOriginData(rackId
									.intValue()) != null)) {
						simualVal = StaffState.rackPacket.getOriginData(rackId
								.intValue())[lampUnit.getLampNo().intValue()];

					}

					setRefreshAction(new UnitAction(lampUnit, simualVal),
							label, button);
				}

			} else {
				unitsInfoPanel.removeAll();

				for (LampUnit lampUnit : unitList) {
					unitsInfoPanel
							.add(createUnitPanel(new UnitAction(
									lampUnit,
									StaffState.rackPacket == null ? 0
											: StaffState.rackPacket
													.getOriginData(rackId
															.intValue())[lampUnit
													.getLampNo().intValue()])));
				}
			}
		}

		public void refreshUnit2(Long rackId) {
			System.out.println("refreshRack rackId=" + rackId);
			unitList = lampUnitDAO.getUnitsByRackId(rackId);
			if (unitsInfoPanel.getComponentCount() == SysConfiguration.rackRow
					* SysConfiguration.rackColumn) {

				for (int i = 0; i < unitsInfoPanel.getComponentCount(); i++) {
					JButton button = (JButton) unitsInfoPanel.getComponent(i);
					LampUnit lampUnit = unitList.get(i);
					int simualVal = 0;
					if ((StaffState.rackPacket != null)
							&& (StaffState.rackPacket.getOriginData(rackId
									.intValue()) != null)) {
						simualVal = StaffState.rackPacket.getOriginData(rackId
								.intValue())[lampUnit.getLampNo().intValue()];

					}
					button.setAction(new UnitAction(lampUnit, simualVal));
				}

			} else {
				unitsInfoPanel.removeAll();

				for (LampUnit lampUnit : unitList) {
					unitsInfoPanel
							.add(createUnitButton(new UnitAction(
									lampUnit,
									StaffState.rackPacket == null ? 0
											: StaffState.rackPacket
													.getOriginData(rackId
															.intValue())[lampUnit
													.getLampNo().intValue()])));
				}
			}
			unitsInfoPanel.updateUI();
			// unitsInfoPanel.repaint();
			// unitsInfoPanel.validate();
			// unitsInfoPanel.revalidate();

		}

		private void setRefreshAction(UnitAction action, JLabel label,
				JButton button) {
			label.setText("<html><center><strong>"
					+ action.getValue(UnitAction.STAFF_NAME)
					+ "</strong></center><ceneter><strong>"
					+ action.getValue(UnitAction.STAFF_DEPART)
					+ "</strong></center></html>");

			button.setAction(action);
		}

		private ActionMap makeActionMap(LampUnit lampUnit) {
			ActionMap actionMap = new ActionMap();

			return actionMap;
		}

		public void refreshUnitsPanel() {
			unitsInfoPanel.updateUI();
			// unitsInfoPanel.repaint();
			// unitsInfoPanel.validate();
			// unitsInfoPanel.revalidate();
		}

		private JPanel unitsInfoPanel;
		List<LampUnit> unitList;

	}

	// 员工基本信息
	class StaffInfoPanel extends JPanel {
		public StaffInfoPanel() {
			staffDAO = new StaffDAO();
			setLayout(new GridBagLayout());
			JPanel queryPanel = new JPanel();
			queryPanel.setBorder(BorderFactory.createTitledBorder("员工信息查询"));
			queryPanel.setLayout(new GridBagLayout());
			queryPanel.add(new JLabel("工号："), new GBC(0, 0).setWeight(0, 0));
			queryPanel.add(new JLabel("姓名："), new GBC(2, 0).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			queryPanel.add(new JLabel("矿灯号："), new GBC(4, 0).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			queryPanel.add(new JLabel("工种："), new GBC(0, 1).setWeight(0, 0));
			queryPanel.add(new JLabel("部门："), new GBC(2, 1).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			queryPanel.add(new JLabel("灯架号："), new GBC(4, 1).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			workIdTxt = new JTextField();
			workIdTxt.setPreferredSize(new Dimension(100, 20));
			nameTxt = new JTextField();
			nameTxt.setPreferredSize(new Dimension(100, 20));

			lampNoTxt = new JTextField();
			lampNoTxt.setPreferredSize(new Dimension(100, 20));
			List comboItem = new ArrayList(
					infoItemDAO.qeueryByType(InfoItem.WORK_TYPE_ITEM));
			comboItem.add(0, "请选择");
			workTypeCombo = new JComboBox(comboItem.toArray());
			workTypeCombo.setPreferredSize(new Dimension(100, 20));
			comboItem = new ArrayList(
					infoItemDAO.qeueryByType(InfoItem.DEPARTMENT_ITEM));
			comboItem.add(0, "请选择");
			departmentCombo = new JComboBox(comboItem.toArray());
			departmentCombo.setPreferredSize(new Dimension(100, 20));
			comboItem = new ArrayList(Util.makeArray(20L));
			comboItem.add(0, "请选择");
			rackIdCombo = new JComboBox(comboItem.toArray());
			rackIdCombo.setPreferredSize(new Dimension(100, 20));
			queryPanel.add(workIdTxt, new GBC(1, 0).setWeight(0, 0));
			queryPanel.add(nameTxt, new GBC(3, 0).setWeight(0, 0));
			queryPanel.add(lampNoTxt, new GBC(5, 0).setWeight(0, 0));
			queryPanel.add(workTypeCombo, new GBC(1, 1).setWeight(0, 0));
			queryPanel.add(departmentCombo, new GBC(3, 1).setWeight(0, 0));
			queryPanel.add(rackIdCombo, new GBC(5, 1).setWeight(0, 0));
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(new JButton(new ButtonAction("查询", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Long workId = null;
					if (!workIdTxt.getText().isEmpty()) {
						workId = new Long(workIdTxt.getText());
					}
					String name = nameTxt.getText();
					Long departmentId = null;
					if (departmentCombo.getSelectedItem() instanceof InfoItem) {
						departmentId = ((InfoItem) departmentCombo
								.getSelectedItem()).getId();
					}
					Long professionId = null;
					if (workTypeCombo.getSelectedItem() instanceof InfoItem) {
						professionId = ((InfoItem) workTypeCombo
								.getSelectedItem()).getId();
					}
					Long rackId = null;
					if (rackIdCombo.getSelectedItem() instanceof Long) {
						rackId = (Long) rackIdCombo.getSelectedItem();
					}
					Long lampNo = null;
					if (!lampNoTxt.getText().isEmpty()) {
						lampNo = new Long(lampNoTxt.getText());
					}
					table.setModel(TableUtil.getModel(staffDAO
							.queryStaffInfoModel(Staff.STAFF_MODEL_TYPE_1,
									workId, name, professionId, departmentId,
									rackId, lampNo), columnNames, dateColumnSet));

				}
			}));
			buttonPanel.add(new JButton(new ButtonAction("导出Excel", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Util.createExcelFile(TableUtil.STAFFBASICINFO_FILENAME,
							TableUtil.STAFFBASICINFO_TITLE, table.getModel());

				}
			}));
			queryPanel.add(buttonPanel, new GBC(6, 1).setWeight(100, 0));
			JPanel tablePanel = new JPanel();
			tablePanel.setBorder(BorderFactory.createTitledBorder("查询结果"));
			tablePanel.setLayout(new GridBagLayout());
			table = new JTable(new Object[][] {}, columnNames);
			table.setAutoCreateRowSorter(true);
			tablePanel.add(
					new JScrollPane(table),
					new GBC(0, 0).setWeight(100, 100).setFill(
							GridBagConstraints.BOTH));

			add(queryPanel,
					new GBC(0, 0).setWeight(100, 0).setFill(
							GridBagConstraints.HORIZONTAL));
			add(tablePanel,
					new GBC(0, 2).setWeight(100, 100).setFill(
							GridBagConstraints.BOTH));
		}

		private JTextField workIdTxt;
		private JTextField nameTxt;
		private JComboBox rackIdCombo;
		private JTextField lampNoTxt;
		private JComboBox workTypeCombo;
		private JComboBox departmentCombo;
		private StaffDAO staffDAO;
		private String[] columnNames = { "工号", "姓名", "灯架号", "矿灯号", "工种", "部门" };
		private HashSet<Integer> dateColumnSet = new HashSet<Integer>();
		private JTable table;
	}

	// 考勤信息查询与统计
	class CheckingInPanel extends JPanel {
		public CheckingInPanel() {
			setLayout(new GridBagLayout());
			JPanel functionPanel = new JPanel();
			functionPanel.setBorder(BorderFactory
					.createTitledBorder("考勤信息查询与统计"));
			functionPanel.setLayout(new GridBagLayout());

			JPanel queryPanel = new JPanel();
			queryPanel.setLayout(new GridBagLayout());
			queryPanel.add(new JLabel("历史考勤："), new GBC(0, 0).setWeight(0, 0));
			queryPanel.add(new JLabel("开始时间："), new GBC(1, 0).setWeight(0, 0)
					.setInsets(0, 30, 0, 0));
			queryPanel.add(new JLabel("结束时间："), new GBC(3, 0).setWeight(0, 0)
					.setInsets(0, 30, 0, 0));

			beginTimeTxt = new JXDatePicker();
			beginTimeTxt.setFormats(Util.dateFormat);

			beginTimeTxt.setPreferredSize(new Dimension(100, 20));
			endTimeTxt = new JXDatePicker();
			endTimeTxt.setFormats(Util.dateFormat);
			endTimeTxt.setPreferredSize(new Dimension(100, 20));

			queryPanel.add(beginTimeTxt, new GBC(2, 0).setWeight(0, 0));
			queryPanel.add(endTimeTxt, new GBC(4, 0).setWeight(0, 0));

			JPanel queryButtonPanel = new JPanel();
			queryButtonPanel.add(new JButton(new ButtonAction("历史查询", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					resultPanel.setSelectedIndex(0);
					Date beginTime = beginTimeTxt.getDate();
					Date endTime = endTimeTxt.getDate();
					historyTable.setModel(TableUtil.getModel(chargingLogDAO
							.queryHistoryLogModel(beginTime, endTime),
							historyColumnNames, historyDateColumnSet));

				}
			}));
			queryButtonPanel.add(new JButton(new ButtonAction("清空数据", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							MinerLampFrame.this,
							"   确定要数据吗？该操作会清空\n\"考勤信息查询与统计\"所依赖的数据", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						chargingLogDAO.deleteAll();
						JOptionPane.showMessageDialog(MinerLampFrame.this,
								"数据已清空");
					}
				}
			}));
			queryButtonPanel.add(new JButton(new ButtonAction("导出Excel", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Util.createExcelFile(TableUtil.HISTORY_FILENAME,
							TableUtil.HISTORY_TITLE, historyTable.getModel());

				}
			}));

			queryPanel.add(queryButtonPanel, new GBC(5, 0).setWeight(100, 0));

			JPanel statisticsPanel = new JPanel();
			statisticsPanel.setLayout(new GridBagLayout());
			statisticsPanel.add(new JLabel("单日考勤："),
					new GBC(0, 0).setWeight(0, 0).setInsets(0, 0, 0, 196));
			statisticsPanel.add(new JLabel("考勤统计日期："), new GBC(1, 0, 3, 1)
					.setWeight(0, 0).setAnchor(GridBagConstraints.EAST));
			statisticsTimeTxt = new JXDatePicker();
			statisticsTimeTxt.setFormats(Util.dateFormat);
			statisticsTimeTxt.setPreferredSize(new Dimension(100, 20));
			statisticsPanel.add(statisticsTimeTxt,
					new GBC(4, 0).setWeight(0, 0));
			JPanel statisticsButtonPanel = new JPanel();
			statisticsButtonPanel.add(new JButton(new ButtonAction("  统计  ",
					null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Date date = statisticsTimeTxt.getDate();
					if (date == null) {
						JOptionPane.showMessageDialog(MinerLampFrame.this,
								"请选择时间");
						return;
					}
					resultPanel.setSelectedIndex(1);
					singleDayTable.setModel(TableUtil.getModel(
							chargingLogDAO.querySingleDayStatisticsModel(date),
							singleDayColumnNames, singleDayDateColumnSet));

				}
			}));
			statisticsButtonPanel.add(new JButton(
					new ButtonAction("详情查询", null) {

						@Override
						public void actionPerformed(ActionEvent e) {
							Date date = statisticsTimeTxt.getDate();
							if (date == null) {
								JOptionPane.showMessageDialog(
										MinerLampFrame.this, "请选择时间");
								return;
							}
							resultPanel.setSelectedIndex(2);
							timeoutTable.setModel(TableUtil.getModel(
									chargingLogDAO
											.querySingleDayTimeoutModel(date),
									timeoutColumnNames, timeoutDateColumnSet));

						}
					}));
			statisticsButtonPanel.add(new JButton(new ButtonAction("导出Excel",
					null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Util.createExcelFile(TableUtil.SINGLEDAY_FILENAME,
							TableUtil.SINGLEDAY_TITLE, timeoutTable.getModel());

				}
			}));
			statisticsPanel.add(statisticsButtonPanel,
					new GBC(5, 0).setWeight(100, 0));

			functionPanel.add(queryPanel, new GBC(0, 0).setWeight(100, 0)
					.setFill(GridBagConstraints.HORIZONTAL));
			functionPanel.add(statisticsPanel, new GBC(0, 1).setWeight(100, 0)
					.setFill(GridBagConstraints.HORIZONTAL));
			resultPanel = new JTabbedPane();
			resultPanel.setBorder(BorderFactory.createTitledBorder("查询与统计结果"));

			JPanel historyTablePanel = new JPanel();
			historyTablePanel.setLayout(new GridBagLayout());
			historyTable = new JTable(new Object[][] {}, historyColumnNames);
			historyTable.setAutoCreateRowSorter(true);
			historyTablePanel.add(new JScrollPane(historyTable), new GBC(0, 0)
					.setWeight(100, 100).setFill(GridBagConstraints.BOTH));
			resultPanel.addTab("员工历史考勤信息", new JScrollPane(historyTablePanel));

			JPanel singleDayTablePanel = new JPanel();
			singleDayTablePanel.setLayout(new GridBagLayout());
			singleDayTable = new JTable(new Object[][] {}, singleDayColumnNames);
			singleDayTable.setAutoCreateRowSorter(true);
			singleDayTablePanel.add(new JScrollPane(singleDayTable), new GBC(0,
					0).setWeight(100, 100).setFill(GridBagConstraints.BOTH));
			resultPanel
					.addTab("单日考勤信息统计", new JScrollPane(singleDayTablePanel));

			JPanel timeoutTablePanel = new JPanel();
			timeoutTablePanel.setLayout(new GridBagLayout());
			timeoutTable = new JTable(new Object[][] {}, timeoutColumnNames);
			timeoutTable.setAutoCreateRowSorter(true);
			timeoutTablePanel.add(new JScrollPane(timeoutTable), new GBC(0, 0)
					.setWeight(100, 100).setFill(GridBagConstraints.BOTH));
			resultPanel.addTab("单日考勤未按时上井信息详情", new JScrollPane(
					timeoutTablePanel));

			add(functionPanel,
					new GBC(0, 0).setWeight(100, 0).setFill(
							GridBagConstraints.HORIZONTAL));
			add(resultPanel,
					new GBC(0, 1).setWeight(100, 100).setFill(
							GridBagConstraints.BOTH));
		}

		private JXDatePicker beginTimeTxt;
		private JXDatePicker endTimeTxt;
		private JXDatePicker statisticsTimeTxt;
		private String[] historyColumnNames = { "工号", "姓名", "灯架号", "矿灯号", "工种",
				"队组", "时间", "事件" };
		private HashSet<Integer> historyDateColumnSet = new HashSet<Integer>(
				Arrays.asList(6));
		private String[] singleDayColumnNames = { "灯架号", "0点班下井人数", "0点班未上井人数",
				"8点班下井人数", "8点班未上井人数", "16点班下井人数", "16点班未上井人数" };
		private HashSet<Integer> singleDayDateColumnSet = new HashSet<Integer>();
		private String[] timeoutColumnNames = { "倒班次", "工号", "姓名", "灯架号",
				"矿灯号", "工组" };
		private HashSet<Integer> timeoutDateColumnSet = new HashSet<Integer>();
		private JTable historyTable;
		private JTable singleDayTable;
		private JTable timeoutTable;
		private JTabbedPane resultPanel;
	}

	// 下井人员信息
	class UnderGroundStaffInfoPanel extends JPanel {
		public UnderGroundStaffInfoPanel() {
			setLayout(new GridBagLayout());
			JPanel queryPanel = new JPanel();
			queryPanel.setBorder(BorderFactory.createTitledBorder("下井员工信息查询"));
			queryPanel.setLayout(new GridBagLayout());
			queryPanel.add(new JLabel("工号："), new GBC(0, 0).setWeight(0, 0));
			queryPanel.add(new JLabel("姓名："), new GBC(2, 0).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			queryPanel.add(new JLabel("矿灯号："), new GBC(4, 0).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			queryPanel.add(new JLabel("工种："), new GBC(0, 1).setWeight(0, 0));
			queryPanel.add(new JLabel("部门："), new GBC(2, 1).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			queryPanel.add(new JLabel("灯架号："), new GBC(4, 1).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			workIdTxt = new JTextField();
			workIdTxt.setPreferredSize(new Dimension(100, 20));
			nameTxt = new JTextField();
			nameTxt.setPreferredSize(new Dimension(100, 20));

			lampNoTxt = new JTextField();
			lampNoTxt.setPreferredSize(new Dimension(100, 20));
			List comboItemList = infoItemDAO
					.qeueryByType(InfoItem.WORK_TYPE_ITEM);
			comboItemList.add(0, "请选择");
			workTypeCombo = new JComboBox(comboItemList.toArray());
			workTypeCombo.setPreferredSize(new Dimension(100, 20));
			comboItemList = infoItemDAO.qeueryByType(InfoItem.DEPARTMENT_ITEM);
			comboItemList.add(0, "请选择");
			departmentCombo = new JComboBox(comboItemList.toArray());
			departmentCombo.setPreferredSize(new Dimension(100, 20));
			comboItemList = Util
					.makeArray(new Long(SysConfiguration.rackCount));
			comboItemList.add(0, "请选择");
			rackIdCombo = new JComboBox(comboItemList.toArray());
			rackIdCombo.setPreferredSize(new Dimension(100, 20));
			queryPanel.add(workIdTxt, new GBC(1, 0).setWeight(0, 0));
			queryPanel.add(nameTxt, new GBC(3, 0).setWeight(0, 0));
			queryPanel.add(lampNoTxt, new GBC(5, 0).setWeight(0, 0));
			queryPanel.add(workTypeCombo, new GBC(1, 1).setWeight(0, 0));
			queryPanel.add(departmentCombo, new GBC(3, 1).setWeight(0, 0));
			queryPanel.add(rackIdCombo, new GBC(5, 1).setWeight(0, 0));
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(new JButton(new ButtonAction("查询", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Long workId = null;
					if (!workIdTxt.getText().isEmpty()) {
						workId = new Long(workIdTxt.getText());
					}
					String name = nameTxt.getText();
					Long departmentId = null;
					if (departmentCombo.getSelectedItem() instanceof InfoItem) {
						departmentId = ((InfoItem) departmentCombo
								.getSelectedItem()).getId();
					}
					Long professionId = null;
					if (workTypeCombo.getSelectedItem() instanceof InfoItem) {
						professionId = ((InfoItem) workTypeCombo
								.getSelectedItem()).getId();
					}
					Long rackId = null;
					if (rackIdCombo.getSelectedItem() instanceof Long) {
						rackId = (Long) rackIdCombo.getSelectedItem();
					}
					Long lampNo = null;
					if (!lampNoTxt.getText().isEmpty()) {
						lampNo = new Long(lampNoTxt.getText());
					}
					table.setModel(TableUtil.getModel(staffDAO
							.queryStaffInfoModel(Staff.STAFF_MODEL_TYPE_2,
									workId, name, professionId, departmentId,
									rackId, lampNo), columnNames, dateColumnSet));

				}
			}));
			buttonPanel.add(new JButton(new ButtonAction("导出Excel", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Util.createExcelFile(TableUtil.UNDERGROUND_STAFF_FILENAME,
							TableUtil.UNDERGROUND_STAFF_TITLE, table.getModel());

				}
			}));
			queryPanel.add(buttonPanel, new GBC(6, 1).setWeight(100, 0));
			JPanel tablePanel = new JPanel();
			tablePanel.setBorder(BorderFactory.createTitledBorder("查询结果"));
			tablePanel.setLayout(new GridBagLayout());
			table = new JTable(new Object[][] {}, columnNames);
			table.setAutoCreateRowSorter(true);
			tablePanel.add(
					new JScrollPane(table),
					new GBC(0, 0).setWeight(100, 100).setFill(
							GridBagConstraints.BOTH));

			add(queryPanel,
					new GBC(0, 0).setWeight(100, 0).setFill(
							GridBagConstraints.HORIZONTAL));
			add(tablePanel,
					new GBC(0, 1).setWeight(100, 100).setFill(
							GridBagConstraints.BOTH));
		}

		private JTextField workIdTxt;
		private JTextField nameTxt;
		private JComboBox rackIdCombo;
		private JTextField lampNoTxt;
		private JComboBox workTypeCombo;
		private JComboBox departmentCombo;
		private String[] columnNames = { "队组", "工号", "姓名", "灯架号", "矿灯号", "工种",
				"时间" };
		private HashSet<Integer> dateColumnSet = new HashSet<Integer>(
				Arrays.asList(6));
		private JTable table;
	}

	// 未上井人员实时信息
	class TimeoutInfoPanel extends JPanel {
		public TimeoutInfoPanel() {
			setLayout(new GridBagLayout());

			JPanel tablePanel = new JPanel();
			tablePanel.setBorder(BorderFactory.createTitledBorder("查询结果"));
			tablePanel.setLayout(new GridBagLayout());
			final JTable table = new JTable(new Object[][] {}, columnNames);
			table.setAutoCreateRowSorter(true);
			tablePanel.add(
					new JScrollPane(table),
					new GBC(0, 0).setWeight(100, 100).setFill(
							GridBagConstraints.BOTH));
			add(Util.makeButton(new ButtonAction("查询", null) {

				@Override
				public void actionPerformed(ActionEvent e) {

					table.setModel(TableUtil.getModel(
							staffDAO.queryTimeoutDetailModel(), columnNames,
							dateColumnSet));
				}
			}),
					new GBC(0, 0).setWeight(100, 0)
							.setAnchor(GridBagConstraints.EAST)
							.setInsets(2, 0, 2, 20));
			add(tablePanel,
					new GBC(0, 1).setWeight(100, 100).setFill(
							GridBagConstraints.BOTH));
		}

		private JComboBox fieldCombo;
		private JTextField conditonTxt;
		private String[] columnNames = { "班次", "姓名", "灯架号", "矿灯号", "队组", "下井时间" };
		private HashSet<Integer> dateColumnSet = new HashSet<Integer>(
				Arrays.asList(5));
	}

	class CommDebugPanel extends JPanel {
		public CommDebugPanel() {
			setLayout(new GridBagLayout());
			JPanel functionPanel = new JPanel();
			add(functionPanel,
					new GBC(0, 0).setWeight(100, 0).setFill(
							GridBagConstraints.HORIZONTAL));
			commandTxt = new JTextField();
			commandTxt.setPreferredSize(new Dimension(180, 20));
			functionPanel.add(commandTxt);
			functionPanel.add(Util.makeButton(new ButtonAction("发送", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					String text = commandTxt.getText();
					Integer seq = Integer.valueOf(text, 16);
					SerialComm.sysSerialComm.write(new byte[] { seq.byteValue() });

				}
			}));

		}

		private JTextField commandTxt;
	}

	class UnitPopupMenu extends JPopupMenu {
		public UnitPopupMenu() {
			editAction = new ButtonAction("查看修改信息", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog dialog = new StaffInfoDialog(MinerLampFrame.this,
							lampNo, rackId, unitState);
					dialog.setVisible(true);

				}
			};
			add(editAction);
			addAction = new ButtonAction("添加关联", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog dialog = new StaffInfoDialog(MinerLampFrame.this,
							lampNo, rackId, unitState);
					dialog.setVisible(true);

				}
			};
			add(addAction);
			delAction = new ButtonAction("删除关联", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							MinerLampFrame.this, "确定要删除关联吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						staffDAO.deleteStaff(rackId, lampNo, unitState,
								StaffAction.DELETE_STAFF);
						JOptionPane
								.showMessageDialog(MinerLampFrame.this,
										"删除关联成功", "提示",
										JOptionPane.INFORMATION_MESSAGE);
						refreshLampUnits();
						refreshRackStatisticsLabelContent();

					}

				}
			};
			add(delAction);
			lockAction = new ButtonAction("开锁", null) {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							MinerLampFrame.this, "确定要开锁吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {

						CommCmdObject co = new CommCmdObject(
								CommCmdObject.COMM_CMD_OPEN_DOOR);
						List<byte[]> datList = new ArrayList<byte[]>();
						datList.add(new byte[] { CommUtil.makeCmdAndAddr(
								SerialComm.CMD_OPEN_DOOR, rackId.intValue()) });
						datList.add(new byte[] { lampNo.byteValue() });
						co.setDat(datList);
						GlobalData.cmdQueue.add(co);
						JOptionPane.showMessageDialog(MinerLampFrame.this,
								"锁已打开", "提示", JOptionPane.INFORMATION_MESSAGE);

					}

				}
			};
			add(lockAction);
			addSeparator();
			settingAction = new ButtonAction("矿灯使用设置", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					SysSettingDialog dialog = new SysSettingDialog(
							MinerLampFrame.this, FunctionSetting.USE_NO);
				}
			};
			add(settingAction);

			simuUndergroundAction = new ButtonAction("模拟下井", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Staff staff = staffDAO.queryByLampNoandRackId(lampNo,
							rackId);
					LampUnit lampUnit = lampUnitDAO.queryByLampNoandRackId(
							lampNo, rackId);
					if (staff != null) {
						staffDAO.updateStaffChargingLastTime(staff, new Date());
						lampUnitDAO
								.updateLampUnitStateAndChargingCountIfNecessary(
										lampUnit, StaffState.STAFF_UNDERGROUND,
										rackId, lampNo);
						chargingLogDAO.save(staff, unitState,
								StaffAction.TAKE_AWAY,
								StaffState.STAFF_UNDERGROUND);
						refreshLampUnits();
						refreshRackStatisticsLabelContent();
					}
				}
			};
			add(simuUndergroundAction);
			simuChargingAction = new ButtonAction("模拟充电", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Staff staff = staffDAO.queryByLampNoandRackId(lampNo,
							rackId);
					LampUnit lampUnit = lampUnitDAO.queryByLampNoandRackId(
							lampNo, rackId);
					if (staff != null) {
						Integer action = null;
						if (unitState == StaffState.STAFF_DEFINED) {
							action = StaffAction.FIRSTTIME_CHARGING;
						} else {
							action = StaffAction.PUT_ON;
						}
						lampUnitDAO
								.updateLampUnitStateAndChargingCountIfNecessary(
										lampUnit, StaffState.LAMP_CHARGING,
										rackId, lampNo);
						chargingLogDAO.save(staff, unitState, action,
								StaffState.LAMP_CHARGING);
					}
					refreshLampUnits();
					refreshRackStatisticsLabelContent();
				}
			};
			add(simuChargingAction);
			simuChargingFullAction = new ButtonAction("模拟充满", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Staff staff = staffDAO.queryByLampNoandRackId(lampNo,
							rackId);
					LampUnit lampUnit = lampUnitDAO.queryByLampNoandRackId(
							lampNo, rackId);
					if (staff != null) {
						lampUnitDAO
								.updateLampUnitStateAndChargingCountIfNecessary(
										lampUnit, StaffState.LAMP_FULL, rackId,
										lampNo);
						chargingLogDAO.save(staff, unitState,
								StaffAction.CHARGING_OK, StaffState.LAMP_FULL);
						refreshLampUnits();
						refreshRackStatisticsLabelContent();
					}
				}
			};
			add(simuChargingFullAction);
			simuTimeoutAction = new ButtonAction("模拟超时上井", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Staff staff = staffDAO.queryByLampNoandRackId(lampNo,
							rackId);
					LampUnit lampUnit = lampUnitDAO.queryByLampNoandRackId(
							lampNo, rackId);
					if (staff != null) {
						lampUnitDAO
								.updateLampUnitStateAndChargingCountIfNecessary(
										lampUnit, StaffState.LAMP_CHARGING,
										rackId, lampNo);
						chargingLogDAO
								.save(staff, unitState, StaffAction.OVER_TIME,
										StaffState.LAMP_CHARGING);
					}
					refreshLampUnits();
					refreshRackStatisticsLabelContent();
				}
			};
			add(simuTimeoutAction);
			simuErrorAction = new ButtonAction("模拟故障", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Staff staff = staffDAO.queryByLampNoandRackId(lampNo,
							rackId);
					LampUnit lampUnit = lampUnitDAO.queryByLampNoandRackId(
							lampNo, rackId);
					if (staff != null) {
						lampUnitDAO
								.updateLampUnitStateAndChargingCountIfNecessary(
										lampUnit, StaffState.LAMP_ERROR,
										rackId, lampNo);
						chargingLogDAO.save(staff, unitState,
								StaffAction.ERROR_HAPPENS,
								StaffState.LAMP_ERROR);
						refreshLampUnits();
						refreshRackStatisticsLabelContent();
					}
				}
			};
			add(simuErrorAction);
			simureparedAction = new ButtonAction("模拟修复", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					Staff staff = staffDAO.queryByLampNoandRackId(lampNo,
							rackId);
					LampUnit lampUnit = lampUnitDAO.queryByLampNoandRackId(
							lampNo, rackId);
					if (staff != null) {
						lampUnitDAO
								.updateLampUnitStateAndChargingCountIfNecessary(
										lampUnit, StaffState.STAFF_DEFINED,
										rackId, lampNo);
						chargingLogDAO.save(staff, unitState,
								StaffAction.ERROR_REPARED,
								StaffState.STAFF_DEFINED);
						refreshLampUnits();
						refreshRackStatisticsLabelContent();
					}
				}
			};
			add(simureparedAction);

		}

		public UnitPopupMenu(Long rackId, Long lampNo, Integer state) {
			this();
			setRackId(rackId);
			setLampNo(lampNo);
			setUnitState(state);
		}

		public Integer getSimuState() {
			return simuState;
		}

		public void setSimuState(Integer simuState) {

		}

		public Integer getUnitState() {
			return unitState;
		}

		public void setUnitState(Integer state) {
			this.unitState = state;
			/*
			 * if (state == StaffState.STAFF_UNDEFINED) {
			 * editAction.setEnabled(false); addAction.setEnabled(true);
			 * delAction.setEnabled(false); settingAction.setEnabled(false);
			 * 
			 * simuUndergroundAction.setEnabled(false);
			 * simuChargingAction.setEnabled(false);
			 * simuChargingFullAction.setEnabled(false);
			 * simuErrorAction.setEnabled(false);
			 * simuTimeoutAction.setEnabled(false);
			 * simureparedAction.setEnabled(false); } else {
			 * editAction.setEnabled(true); addAction.setEnabled(false);
			 * delAction.setEnabled(true); settingAction.setEnabled(true); if
			 * (state == StaffState.STAFF_DEFINED) {
			 * simuUndergroundAction.setEnabled(false);
			 * simuChargingAction.setEnabled(true);
			 * simuChargingFullAction.setEnabled(false);
			 * simuErrorAction.setEnabled(true);
			 * simuTimeoutAction.setEnabled(false);
			 * simureparedAction.setEnabled(false); } else if (state ==
			 * StaffState.STAFF_UNDERGROUND) {
			 * simuUndergroundAction.setEnabled(false);
			 * simuChargingAction.setEnabled(true);
			 * simuChargingFullAction.setEnabled(false);
			 * simuErrorAction.setEnabled(true);
			 * simuTimeoutAction.setEnabled(true);
			 * simureparedAction.setEnabled(false); } else if (state ==
			 * StaffState.LAMP_CHARGING) {
			 * simuUndergroundAction.setEnabled(true);
			 * simuChargingAction.setEnabled(false);
			 * simuChargingFullAction.setEnabled(true);
			 * simuErrorAction.setEnabled(true);
			 * simuTimeoutAction.setEnabled(false);
			 * simureparedAction.setEnabled(false); } else if (state ==
			 * StaffState.LAMP_FULL) { simuUndergroundAction.setEnabled(true);
			 * simuChargingAction.setEnabled(false);
			 * simuChargingFullAction.setEnabled(false);
			 * simuErrorAction.setEnabled(true);
			 * simuTimeoutAction.setEnabled(false);
			 * simureparedAction.setEnabled(false); } else if (state ==
			 * StaffState.LAMP_ERROR) { simuUndergroundAction.setEnabled(false);
			 * simuChargingAction.setEnabled(false);
			 * simuChargingFullAction.setEnabled(false);
			 * simuErrorAction.setEnabled(false);
			 * simuTimeoutAction.setEnabled(false);
			 * simureparedAction.setEnabled(true); } }
			 */
		}

		public Long getRackId() {
			return rackId;
		}

		public void setRackId(Long rackId) {
			this.rackId = rackId;
		}

		public Long getLampNo() {
			return lampNo;
		}

		public void setLampNo(Long lampNo) {
			this.lampNo = lampNo;
		}

		private Integer simuState;
		private Integer unitState;
		private Long rackId;
		private Long lampNo;
		private Action editAction;
		private Action addAction;
		private Action delAction;
		private Action lockAction;
		private Action settingAction;
		private Action simuUndergroundAction;
		private Action simuChargingAction;
		private Action simuChargingFullAction;
		private Action simuErrorAction;
		private Action simureparedAction;
		private Action simuTimeoutAction;
	}

	class BackgroundActivity extends SwingWorker<Void, Integer> {

		public BackgroundActivity() {

		}

		protected Void doInBackground() throws Exception {

			while (true) {
				synchronized (this) {
					wait();
					if (SerialComm.sysSerialComm.isRackNeededRefresh()) {

						// TODO
						List<LampUnit> unitList = lampUnitDAO
								.getUnitsByRackId(new Long(
										StaffState.rackPacket.getAddr()));
						System.out.println("refresh rackId="
								+ StaffState.rackPacket.getAddr()
								+ " unit Count=" + unitList.size());
						for (int i = 0; i < unitList.size(); i++) {
							LampUnit lampUnit = unitList.get(i);
							int newState = StaffState.rackPacket
									.getData(lampUnit.getRackId().intValue())[i];
							System.out.print("state["
									+ i
									+ "]="
									+ StaffState.rackPacket.getData(lampUnit
											.getRackId().intValue())[i] + " ");

							int action = changeStateIfNecessaryWithReturnAction(
									lampUnit, newState);
							if (action != StaffAction.NOTHING_TO_DO) {
								System.out.println(" unitId=" + new Long(i + 1)
										+ " rackId="
										+ StaffState.rackPacket.getAddr()
										+ " newState= " + newState);
								/*
								 * if (action == StaffAction.ERROR_REPARED ||
								 * action == StaffAction.ONLINE) { newState =
								 * StaffState.STAFF_DEFINED; }
								 */
								Staff staff = staffDAO.queryByLampNoandRackId(
										new Long(i + 1),
										new Long(StaffState.rackPacket
												.getAddr()));
								if (action == StaffAction.TAKE_AWAY) {
									staffDAO.updateStaffChargingLastTime(staff,
											new Date());
								}
								lampUnitDAO
										.updateLampUnitStateAndChargingCountIfNecessary(
												lampUnit,
												newState,
												new Long(StaffState.rackPacket
														.getAddr()), new Long(
														i + 1)); //

								chargingLogDAO.save(staff, lampUnit.getState(),
										action, newState);
							}
						}
						SerialComm.sysSerialComm.setRackRefreshed(false);
						publish(0);

					} else {
						if (SerialComm.sysSerialComm.commState == SerialComm.COMM_STATE_NOTHING) {
							publish(1);
						}
					}
				}
			}

		}

		protected void process(List<Integer> list) {
			for (Integer value : list) {
				switch (value.intValue()) {
				case 0:
					if (SerialComm.commState == SerialComm.COMM_STATE_UNITS_INFO) {
						refreshRackStatisticsLabelContent();
						SerialComm.commState = SerialComm.COMM_STATE_NOTHING;
						setCommBusy(false);
					} else if (SerialComm.commState == SerialComm.COMM_STATE_SEL_UNITS_INFO) {
						System.out.println("refresh sel rackPanel");
						refreshLampUnitsWithSelectRack(true);
						refreshRackStatisticsLabelContent();
						SerialComm.commState = SerialComm.COMM_STATE_NOTHING;
						setCommBusy(false);

					}

					/*
					 * if (SerialComm.commState ==
					 * SerialComm.DATA_TYPE_UNITS_INFO) {
					 * SerialComm.sysSerialComm.reqNextRackUnitInfo(); } else if
					 * (SerialComm.commState ==
					 * SerialComm.DATA_TYPE_SINGLE_UNIT_INFO) { {
					 * SerialComm.commState = SerialComm.DATA_TYPE_NOTHING;
					 * setCommBusy(false); } }
					 */
					break;
				case 1:
					setCommBusy(false);
					break;
				default:
					break;
				}
			}

		}

		protected void done() {

		}

	}

	public static final int DEFAULT_WIDTH = 1024;
	public static final int DEFAULT_HEIGHT = 768;
	public static final String DEFALUT_LOOKANDFEEL = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	private String titleExtra = "";
	private JMenu sysMenu;
	private JMenu lampMenu;
	private JMenu staffMenu;
	private JMenu alarmMenu;
	private JMenu helpMenu;
	private JButton loginButton;
	private JButton logoutButton;
	private JButton dataBackupButton;
	private JButton lampStateButton;
	private JButton checkinginQueryButton;
	private JButton lampStatisticsButton;
	private JButton sysSettingButton;
	private JButton timeoutButton;
	private JButton ledSettingButton;
	private JPanel leftPanel;
	private JPanel rightStackPanel;
	private JPanel rightPanel;
	private JPanel beforeLoginPanel;
	private JPanel rackPanel;// 充电架
	private JTree rackTree;
	private DefaultTreeModel rackTreeModel;
	private JPanel statisticPane;// 统计信息
	private JTabbedPane mainPanel;// 内容区
	private UnitsPanel unitsPanel;
	private JSplitPane splitPane1;
	private JPanel statePanel;
	private UnitPopupMenu unitPopupMenu;

	private JButton refreshAllRackButton; // 刷新按钮
	private JButton refreshRackButton; // 刷新按钮
	// 统计信息
	private JLabel rackNoLabel;
	private JLabel idleUintCountLabel;
	private JLabel chargingUnitCountLabel;
	private JLabel undergroundStaffLabel;
	private JLabel faultUnintLabel;
	private JLabel fullUnitLabel;
	private JLabel unitTotalCountLabel;
	private JLabel rackTotalCountLabel;
	private JLabel underGroundStaffCountLabel;
	private JLabel ChargingStaffCountLabel;
	private SysInfDAO sysInfDAO;
	private LampRackDAO lampRackDAO;
	private LampUnitDAO lampUnitDAO;
	private StaffDAO staffDAO;
	private InfoItemDAO infoItemDAO;
	private ChargingLogDAO chargingLogDAO;

	private boolean reachLimited = false;
	private User currentUser;
	private JLabel commStateLabel;
	private JLabel commInfoLabel;
	private JLabel operatorLabel;
	private JLabel emptyLabel;
	private JLabel updateStateLabel;
	private JLabel sysStateLabel;
	
	private Timer checkRegistTimer;

	private BackgroundActivity backgroundActivity;
	private JButton addRackBtn;

	public void setCommStateLableText(String text) {
		commStateLabel.setText(text);
	}

	public void setOperatorLabelText(String text) {
		operatorLabel.setText(text);
	}

	public void setEmptyLabelText(String text) {
		emptyLabel.setText(text);
	}

	public void setUpdateStateLabelText(String text) {
		updateStateLabel.setText(text);
	}

	public void setSysStateLabelText(String text) {
		sysStateLabel.setText(text);
	}

	public SysInfDAO getSysInfDAO() {
		return sysInfDAO;
	}

	public void setSysInfDAO(SysInfDAO sysInfDAO) {
		this.sysInfDAO = sysInfDAO;
	}

	public LampRackDAO getLampRackDAO() {
		return lampRackDAO;
	}

	public void setLampRackDAO(LampRackDAO lampRackDAO) {
		this.lampRackDAO = lampRackDAO;
	}

	public LampUnitDAO getLampUintDAO() {
		return lampUnitDAO;
	}

	public void setLampUintDAO(LampUnitDAO lampUintDAO) {
		this.lampUnitDAO = lampUintDAO;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
		setOperatorLabelText("操作员："
				+ (currentUser != null ? currentUser.getName() : ""));
	}
}