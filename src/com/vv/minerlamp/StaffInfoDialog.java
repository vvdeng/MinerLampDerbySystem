package com.vv.minerlamp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.hibernate.Hibernate;
import org.jdesktop.swingx.JXDatePicker;

import com.vv.minerlamp.comm.SerialComm;
import com.vv.minerlamp.dao.InfoItemDAO;
import com.vv.minerlamp.dao.LampChangeLogDAO;
import com.vv.minerlamp.dao.LampUnitDAO;
import com.vv.minerlamp.dao.StaffDAO;
import com.vv.minerlamp.entity.InfoItem;
import com.vv.minerlamp.entity.LampChangeLog;
import com.vv.minerlamp.entity.LampUnit;
import com.vv.minerlamp.entity.Staff;
import com.vv.minerlamp.util.StaffAction;
import com.vv.minerlamp.util.StaffState;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

class StaffInfoDialog extends JDialog {
	public StaffInfoDialog(final MinerLampFrame owner, final Long lampNo,
			final Long rackId, final Integer state) {

		super(owner, "员工信息", true);
		setIconImage(SysConfiguration.sysIcon);
		staffDAO = new StaffDAO();
		infoItemDAO = new InfoItemDAO();
		lampUnitDAO = new LampUnitDAO();
		lampChangeLogDAO = new LampChangeLogDAO();
		this.lampNo = lampNo;
		this.rackId = rackId;
		JLabel discLabel = new JLabel(new ImageIcon(
				"resources/staffInfoTitle.png"));

		discLabel.setBackground(Color.BLUE);
		add(discLabel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		add(panel, BorderLayout.CENTER);
		// 左面板
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		leftPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));
		leftPanel.add(new JLabel("    工号："), new GBC(0, 0).setWeight(0, 0)
				.setInsets(16, 0, 0, 0));
		leftPanel.add(new JLabel("    姓名："), new GBC(0, 1).setWeight(0, 0)
				.setInsets(16, 0, 0, 0));
		leftPanel.add(new JLabel("    性别："), new GBC(0, 2).setWeight(0, 0)
				.setInsets(16, 0, 0, 0));
		leftPanel.add(new JLabel("出生日期："), new GBC(0, 3).setWeight(0, 0)
				.setInsets(16, 0, 0, 0));
		leftPanel.add(new JLabel("  证件号："), new GBC(0, 4).setWeight(0, 0)
				.setInsets(16, 0, 0, 0));
		leftPanel.add(new JLabel("文化水平："), new GBC(0, 5).setWeight(0, 0)
				.setInsets(15, 0, 0, 0));
		leftPanel.add(new JLabel("    电话："), new GBC(0, 6).setWeight(0, 0)
				.setInsets(15, 0, 0, 0));
		leftPanel.add(new JLabel("通信地址："), new GBC(0, 7).setWeight(0, 0)
				.setInsets(15, 0, 1, 0));

		staffIdTxt = new JFormattedTextField(Util.getIntegerNumberFormat());

		staffIdTxt.setPreferredSize(new Dimension(100, 20));
		nameTxt = new JTextField();
		nameTxt.setPreferredSize(new Dimension(100, 20));
		ButtonGroup sexGroup = new ButtonGroup();
		maleRadioButton = new JRadioButton("男", true);
		fmaleRadioButton = new JRadioButton("女", true);
		sexGroup.add(maleRadioButton);
		sexGroup.add(fmaleRadioButton);
		birthDateTxt = new JXDatePicker();
		birthDateTxt.setFormats(Util.dateFormat);
		birthDateTxt.setPreferredSize(new Dimension(100, 20));
		certificateNoTxt = new JTextField();
		certificateNoTxt.setPreferredSize(new Dimension(100, 20));
		educationCombo = new JComboBox(infoItemDAO.qeueryByType(
				InfoItem.EDUCATION_ITEM).toArray());

		phoneTxt = new JTextField();
		phoneTxt.setPreferredSize(new Dimension(100, 20));
		addressTxt = new JTextField();
		imagePanel = new JPanel();
		// imagePanel.setBorder(BorderFactory.createEtchedBorder());
		imagePanel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));

		imageLabel = new JLabel();
		makeImageLabel("resources/staff_image_null.jpg");
		imagePanel.add(imageLabel);

		openImageButton = Util.makePrivButton(new ButtonAction("打开照片", null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				String extj[] = { "jpeg", "jpg" };
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"JPEG Image", extj);
				chooser.setFileFilter(filter);
				int retval = chooser.showOpenDialog(StaffInfoDialog.this);// ;
				if (retval == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();

					makeImageLabel(file.getAbsolutePath());
				}

			}
		}, owner.getCurrentUser());
		clearImageButton = Util.makePrivButton(new ButtonAction("清除照片", null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				makeImageLabel("resources/staff_image_null.jpg");
			}
		}, owner.getCurrentUser());

		leftPanel.add(staffIdTxt, new GBC(1, 0, 2, 1).setWeight(100, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 1, 0, 1));
		leftPanel.add(
				nameTxt,
				new GBC(1, 1, 2, 1).setWeight(100, 0)
						.setFill(GridBagConstraints.HORIZONTAL)
						.setInsets(16, 1, 0, 1));
		leftPanel.add(maleRadioButton, new GBC(1, 2, 1, 1).setWeight(0, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(16, 1, 0, 1));
		leftPanel.add(fmaleRadioButton, new GBC(2, 2, 1, 1).setWeight(0, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(16, 1, 0, 1));
		leftPanel.add(birthDateTxt, new GBC(1, 3, 2, 1).setWeight(100, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(16, 1, 0, 1));
		leftPanel.add(certificateNoTxt, new GBC(1, 4, 2, 1).setWeight(100, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(16, 1, 0, 1));
		leftPanel.add(educationCombo, new GBC(1, 5, 2, 1).setWeight(100, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(15, 1, 0, 1));
		leftPanel.add(
				phoneTxt,
				new GBC(1, 6, 2, 1).setWeight(100, 0)
						.setFill(GridBagConstraints.HORIZONTAL)
						.setInsets(15, 1, 0, 1));
		leftPanel.add(imagePanel, new GBC(3, 0, 2, 5).setWeight(100, 100)
				.setInsets(1, 1, 1, 1).setAnchor(GridBagConstraints.NORTH));
		leftPanel.add(openImageButton,
				new GBC(3, 5).setWeight(0, 0).setInsets(15, 1, 0, 1));
		leftPanel.add(clearImageButton, new GBC(4, 5).setWeight(0, 0)
				.setInsets(15, 1, 0, 1));
		leftPanel.add(addressTxt, new GBC(1, 7, 4, 1).setWeight(100, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(15, 1, 1, 1));

		// 右上面版
		JPanel rightTopPanel = new JPanel();
		rightTopPanel.setBorder(BorderFactory.createTitledBorder("工作信息"));
		rightTopPanel.setLayout(new GridBagLayout());
		rightTopPanel.add(new JLabel("  灯架号："), new GBC(0, 0).setWeight(0, 0));
		rightTopPanel.add(new JLabel("  矿灯号："), new GBC(0, 1).setWeight(0, 0));
		rightTopPanel.add(new JLabel("    工种："), new GBC(0, 2).setWeight(0, 0));
		rightTopPanel.add(new JLabel("    部门："), new GBC(0, 3).setWeight(0, 0));
		rightTopPanel.add(new JLabel("    班次："), new GBC(0, 4).setWeight(0, 0));
		rightTopPanel.add(new JLabel("矿灯厂家："), new GBC(0, 5).setWeight(0, 0));
		rackNoCombo = new JComboBox(Util.makeArray(20L).toArray());
		selectCombo(rackNoCombo, rackId.toString());
		rackNoCombo.setEnabled(false);
		unitNoTxt = new JTextField();
		unitNoTxt.setText(lampNo.toString());
		unitNoTxt.setEnabled(false);
		workTypeCombo = new JComboBox(infoItemDAO.qeueryByType(
				InfoItem.WORK_TYPE_ITEM).toArray());
		departmentCombo = new JComboBox(infoItemDAO.qeueryByType(
				InfoItem.DEPARTMENT_ITEM).toArray());
		clazzCombo = new JComboBox(infoItemDAO
				.qeueryByType(InfoItem.CLAZZ_ITEM).toArray());
		manufacturerTxt = new JTextField();
		rightTopPanel.add(
				rackNoCombo,
				new GBC(1, 0).setWeight(100, 0)
						.setFill(GridBagConstraints.HORIZONTAL).setInsets(2));
		rightTopPanel.add(
				unitNoTxt,
				new GBC(1, 1).setWeight(100, 0)
						.setFill(GridBagConstraints.HORIZONTAL).setInsets(2));
		rightTopPanel.add(workTypeCombo, new GBC(1, 2).setWeight(100, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(2));
		rightTopPanel.add(departmentCombo, new GBC(1, 3).setWeight(100, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(2));
		rightTopPanel.add(
				clazzCombo,
				new GBC(1, 4).setWeight(100, 0)
						.setFill(GridBagConstraints.HORIZONTAL).setInsets(2));
		rightTopPanel.add(manufacturerTxt, new GBC(1, 5).setWeight(100, 0)
				.setFill(GridBagConstraints.HORIZONTAL).setInsets(2));
		// 右下面版
		JPanel rightBottomPanel = new JPanel();
		rightBottomPanel.setBorder(BorderFactory.createTitledBorder("使用情况"));
		rightBottomPanel.setLayout(new GridBagLayout());
		chargingCountLabel = new JLabel("累计充电次数：0");
		rightBottomPanel.add(chargingCountLabel,
				new GBC(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST)
						.setInsets(2));
		chargingBeginTimeLabel = new JLabel("开始使用时间：0000-00-00 00:00:00");
		rightBottomPanel.add(chargingBeginTimeLabel,
				new GBC(0, 1, 2, 1).setInsets(2));
		useCountButton = Util.makePrivButton(new ButtonAction("使用度", null) {

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new ChangeLampDialog(StaffInfoDialog.this,
						rackId, lampNo);
				dialog.setVisible(true);

			}
		}, owner.getCurrentUser());
		rightBottomPanel.add(useCountButton, new GBC(0, 2).setInsets(2));
		changeButton = Util.makePrivButton(new ButtonAction(" 更换 ", null) {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selection = JOptionPane.showConfirmDialog(
						StaffInfoDialog.this, "确定要更换矿灯吗", "提示",
						JOptionPane.YES_NO_OPTION);
				if (selection == JOptionPane.YES_OPTION) {
					if (staff == null) {
						JOptionPane.showMessageDialog(StaffInfoDialog.this,
								"该充电座尚未使用");
						return;
					}
					lampUnitDAO.changeLamp(rackId, lampNo, 0, new Date());
					LampChangeLog lampChangeLog = new LampChangeLog();
					lampChangeLog.setRackId(rackId);
					lampChangeLog.setLampNo(lampNo);
					lampChangeLog.setWorkId(staff.getWorkId());
					lampChangeLog.setStaffName(staff.getName());
					lampChangeLog.setChangeTime(new Date());
					lampChangeLog.setOperation(LampChangeLog.OPERATION_CHANGE);
					lampChangeLogDAO.save(lampChangeLog);
					JOptionPane.showMessageDialog(StaffInfoDialog.this,
							"矿灯更换成功", "提示", JOptionPane.INFORMATION_MESSAGE);
					refreshChargingLabel();
				}
			}
		}, owner.getCurrentUser());
		rightBottomPanel.add(changeButton, new GBC(1, 2).setInsets(2));
		editButton = Util.makePrivButton(new ButtonAction(" 编辑 ", null) {

			@Override
			public void actionPerformed(ActionEvent e) {
				elementsEnabled(true);

			}
		}, owner.getCurrentUser());
		rightBottomPanel.add(editButton, new GBC(0, 3).setInsets(2));
		delButton = Util.makePrivButton(new ButtonAction(" 删除 ", null) {

			@Override
			public void actionPerformed(ActionEvent e) {

				int selection = JOptionPane.showConfirmDialog(
						StaffInfoDialog.this, "确定要删除矿灯吗", "提示",
						JOptionPane.YES_NO_OPTION);
				if (selection == JOptionPane.YES_OPTION) {
					staffDAO.deleteStaff(rackId, lampNo, state,
							StaffAction.DELETE_STAFF);
					JOptionPane.showMessageDialog(StaffInfoDialog.this,
							"矿灯删除成功", "提示", JOptionPane.INFORMATION_MESSAGE);
					owner.refreshLampUnits();
					owner.refreshRackStatisticsLabelContent();

					dispose();
				}

			}
		}, owner.getCurrentUser());
		rightBottomPanel.add(delButton, new GBC(1, 3).setInsets(2));
		saveButton = Util.makePrivButton(new ButtonAction(" 保存 ", null) {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (staffIdTxt.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(StaffInfoDialog.this,
							"工号不能为空");
					return;
				}
				if (nameTxt.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(StaffInfoDialog.this,
							"姓名不能为空");
					return;
				}
				Long workId = new Long(staffIdTxt.getText());
				String name = nameTxt.getText();
				int sex = maleRadioButton.isSelected() ? 0 : 1;
				String birthDate = null;
				if (birthDateTxt.getDate() != null) {
					birthDate = Util.formatDate(birthDateTxt.getDate());
				}
				String certificateNo = certificateNoTxt.getText();
				String eduLevel = educationCombo.getSelectedItem().toString();
				String phone = phoneTxt.getText();
				if ((!phone.isEmpty())
						&& (phone.length() > 15 || !phone
								.matches("(\\d+-){0,1}\\d+"))) {
					JOptionPane.showMessageDialog(StaffInfoDialog.this,
							"电话号码格式不正确");
					return;
				}
				String address = addressTxt.getText();
				Long rackId = (Long) rackNoCombo.getSelectedItem();
				Long lampNo = new Long(unitNoTxt.getText().toString());
				InfoItem selProfession = (InfoItem) workTypeCombo
						.getSelectedItem();
				Long professionId = selProfession.getId();
				String profession = selProfession.getValue();
				InfoItem selDepartment = (InfoItem) departmentCombo
						.getSelectedItem();
				Long departmentId = selDepartment.getId();
				String department = selDepartment.getValue();
				InfoItem selClazz = (InfoItem) clazzCombo.getSelectedItem();
				Long clazzId = selClazz.getId();
				String clazz = selClazz.getValue();
				String manufacturer = manufacturerTxt.getText();
				Blob imageBlob = null;
				try {

					BufferedImage bu = new BufferedImage(IMAGE_WIDTH,
							IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
					ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
					Graphics graphics = bu.getGraphics();
					graphics.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT,
							null);
					boolean resultWrite = ImageIO.write(bu, "png", imageStream);
					FileOutputStream fos = new FileOutputStream(new File(
							"c:/abc.png"));
					imageStream.close();
					fos.write(imageStream.toByteArray());

					fos.close();
					imageBlob = Hibernate.createBlob(imageStream.toByteArray());

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Staff staff = new Staff(workId, name, sex, birthDate,
						certificateNo, eduLevel, phone, address, rackId,
						lampNo, professionId, profession, departmentId,
						department, clazzId, clazz, imageBlob);
				Integer action = null;
				if (state == StaffState.STAFF_UNDEFINED) {
					action = StaffAction.DEFINE_STAFF;
				} else {
					action = StaffAction.EDIT_STAFF;
				}

				staffDAO.save(staff, state, action, manufacturer);
				if (SerialComm.sysSerialComm.isSerialCommOk()) {
					SerialComm.sysSerialComm.updateStaffInfo(staff);
				}
				JOptionPane.showMessageDialog(StaffInfoDialog.this, "保存成功");
				buttonEnabled(true);
				
				owner.refreshLampUnits();
				owner.refreshRackStatisticsLabelContent();
			}
		}, owner.getCurrentUser());
		rightBottomPanel.add(saveButton, new GBC(0, 4).setInsets(2));
		// rightBottomPanel.add(makeButton(new ButtonAction(" 添加 ", null) {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// }), new GBC(1, 4));

		panel.add(
				leftPanel,
				new GBC(0, 0, 1, 2).setWeight(100, 60).setFill(
						GridBagConstraints.BOTH));
		panel.add(
				rightTopPanel,
				new GBC(1, 0).setWeight(100, 40).setFill(
						GridBagConstraints.BOTH));
		panel.add(
				rightBottomPanel,
				new GBC(1, 1).setWeight(100, 40).setFill(
						GridBagConstraints.BOTH));

		JButton exit = new JButton("退出");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				dispose();
			}
		});
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(exit);
		add(bottomPanel, BorderLayout.SOUTH);
		setResizable(false);
		setSize(560, 450);
		setLocation(Util.calculatePosition(owner, this));

		staff = staffDAO.queryByLampNoandRackId(lampNo, rackId);
		lampUnit = lampUnitDAO.queryByLampNoandRackId(lampNo, rackId);
		if (staff != null) {

			fillElements(staff, lampUnit);
			elementsEnabled(false);

		} else {
			buttonEnabled(false);
		}
	}

	public void fillElements(Staff staff, LampUnit lampUnit) {

		staffIdTxt.setValue(staff.getWorkId());
		nameTxt.setText(staff.getName());
		maleRadioButton.setSelected(staff.getSex() == 0);
		fmaleRadioButton.setSelected(staff.getSex() != 0);
		if (staff.getBirthDate() != null) {
			birthDateTxt.setDate(Util.parseDate(staff.getBirthDate()));
		}
		certificateNoTxt.setText(staff.getCertificateNo());
		selectCombo(educationCombo, staff.getEduLevel());
		phoneTxt.setText(staff.getPhone());
		addressTxt.setText(staff.getAddress());

		selectCombo(rackNoCombo, staff.getRackId().toString());
		unitNoTxt.setText(staff.getLampNo().toString());

		selectCombo(workTypeCombo, staff.getProfession());
		selectCombo(departmentCombo, staff.getDepartment());
		selectCombo(clazzCombo, staff.getClazz());
		manufacturerTxt.setText(lampUnit.getManufacturer());
		refreshChargingLabel();
		makeImageLabel(staff.getImage());
	}

	public void refreshChargingLabel() {
		lampUnit = lampUnitDAO.queryByLampNoandRackId(lampNo, rackId);
		if (lampUnit != null) {

			chargingCountLabel
					.setText("累计充电次数：" + lampUnit.getChargingCounts());
			chargingBeginTimeLabel.setText("开始使用时间："
					+ Util.formatDate(lampUnit.getChargingBeginTime()));

		}
	}

	public void elementsEnabled(boolean flag) {
		if (flag) {
			staffIdTxt.setEnabled(true);
			nameTxt.setEnabled(true);
			maleRadioButton.setEnabled(true);
			fmaleRadioButton.setEnabled(true);
			birthDateTxt.setEnabled(true);
			certificateNoTxt.setEnabled(true);
			educationCombo.setEnabled(true);
			phoneTxt.setEnabled(true);
			addressTxt.setEnabled(true);
			clazzCombo.setEnabled(true);
			manufacturerTxt.setEnabled(true);
			workTypeCombo.setEnabled(true);
			departmentCombo.setEnabled(true);

		} else {
			staffIdTxt.setEnabled(false);
			nameTxt.setEnabled(false);
			maleRadioButton.setEnabled(false);
			fmaleRadioButton.setEnabled(false);
			birthDateTxt.setEnabled(false);
			certificateNoTxt.setEnabled(false);
			educationCombo.setEnabled(false);
			phoneTxt.setEnabled(false);
			addressTxt.setEnabled(false);
			clazzCombo.setEnabled(false);
			manufacturerTxt.setEnabled(false);
			rackNoCombo.setEnabled(false);
			unitNoTxt.setEnabled(false);
			workTypeCombo.setEnabled(false);
			departmentCombo.setEnabled(false);

		}
	}

	public void buttonEnabled(boolean flag) {
		if (!flag) {
			useCountButton.setEnabled(false);
			changeButton.setEnabled(false);
			editButton.setEnabled(false);
			delButton.setEnabled(false);
		} else {
			useCountButton.setEnabled(true);
			changeButton.setEnabled(true);
			editButton.setEnabled(true);
			delButton.setEnabled(true);
		}
	}

	public void selectCombo(JComboBox comboBox, String item) {
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			if (comboBox.getItemAt(i).toString().equals(item)) {
				comboBox.setSelectedIndex(i);
				break;
			}
		}
	}

	public JButton makeButton(Action action) {
		JButton button = new JButton(action);
		return button;
	}

	public void makeImageLabel(String filePath) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		image = kit.getImage(filePath);
		image = image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
				Image.SCALE_SMOOTH);
		imageLabel.setIcon(new ImageIcon(image));
	}

	public void makeImageLabel(Blob blob) {
		try {
			image = ImageIO.read(blob.getBinaryStream());
		} catch (IOException e) {

			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		image = image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
				Image.SCALE_SMOOTH);
		imageLabel.setIcon(new ImageIcon(image));
	}

	private Long lampNo;
	private Long rackId;
	private JFormattedTextField staffIdTxt;
	private JTextField nameTxt;
	private JRadioButton maleRadioButton;
	private JRadioButton fmaleRadioButton;
	private JXDatePicker birthDateTxt;
	private JTextField certificateNoTxt;
	private JComboBox educationCombo;
	private JTextField phoneTxt;
	private JTextField addressTxt;
	private JPanel imagePanel;
	private JLabel imageLabel;
	private Image image;
	private JButton openImageButton;
	private JButton clearImageButton;

	private JComboBox rackNoCombo;
	private JTextField unitNoTxt;
	private JComboBox workTypeCombo;
	private JComboBox departmentCombo;
	private JComboBox clazzCombo;
	private JTextField manufacturerTxt;
	private JLabel chargingCountLabel;
	private JLabel chargingBeginTimeLabel;
	private StaffDAO staffDAO;
	private InfoItemDAO infoItemDAO;
	private LampUnitDAO lampUnitDAO;
	private LampChangeLogDAO lampChangeLogDAO;
	private Staff staff;
	private LampUnit lampUnit;
	public static final int IMAGE_HEIGHT = 160;
	private static final int IMAGE_WIDTH = 120;
	private JButton useCountButton;
	private JButton changeButton;
	private JButton editButton;
	private JButton delButton;
	private JButton saveButton;
}
