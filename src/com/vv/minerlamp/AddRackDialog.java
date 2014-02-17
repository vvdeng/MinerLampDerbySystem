package com.vv.minerlamp;

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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vv.minerlamp.dao.LampRackDAO;
import com.vv.minerlamp.dao.LampUnitDAO;
import com.vv.minerlamp.dao.UserDAO;
import com.vv.minerlamp.entity.LampRack;
import com.vv.minerlamp.entity.User;
import com.vv.minerlamp.util.HibernateUtil;
import com.vv.minerlamp.util.PropertiesUtil;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

class AddRackDialog extends JDialog {
	public AddRackDialog(final MinerLampFrame owner) {
		super(owner, "增加充电架", true);
		setIconImage(SysConfiguration.sysIcon);
		lampUnitDAO=new LampUnitDAO();
		lampRackDAO=new LampRackDAO();
		setLayout(new GridBagLayout());
		JPanel panel = new JPanel();
		// add(panel,new GBC(0, 0).setWeight(100,
		// 100).setFill(GridBagConstraints.BOTH));
		panel.setLayout(new GridBagLayout());

		add(new JLabel("新增架数："),
				new GBC(0, 0).setWeight(100, 0).setAnchor(
						GridBagConstraints.EAST));

		rackCountTxt = new JTextField();
		// nameTxt.setText("       ");
		// rackCountTxt.setPreferredSize(new Dimension(185,20));

		ActionListener addActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Integer addRackCount = null;
				try {
					addRackCount=Integer.parseInt(rackCountTxt.getText().trim());
				} catch (Exception ex) {
					// TODO: handle exception
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "数据格式不争确");
				}
				Session session = HibernateUtil.getSessionFactory().openSession();
				Transaction transaction = null;
				try {
					transaction=session.beginTransaction();
					Long maxRackId=lampRackDAO.getMaxIdInCurrentSession(session);
					lampRackDAO.batchAddSaveInCurrentSession(maxRackId,addRackCount, session);
					lampUnitDAO.batchAddSaveInCurrentSession(maxRackId, addRackCount, SysConfiguration.rackColumn*SysConfiguration.rackRow, session);
					transaction.commit();
					PropertiesUtil.writeProperties(
							SysConfiguration.FILE_PATH,
							"rackCount", new Integer(SysConfiguration.rackCount+addRackCount).toString());
					
				} catch (HibernateException ex) {
					transaction.rollback();
					ex.printStackTrace();
				} finally {
					session.close();
					JOptionPane.showMessageDialog(owner, "添加成功,请重启管理软件");
				
				//	owner.refreshLampRackTree();
				//	dispose();
					System.exit(0);
				}

			}
		};

		add(rackCountTxt,
				new GBC(1, 0).setWeight(100, 0).setFill(GBC.HORIZONTAL)
						.setAnchor(GridBagConstraints.WEST).setInsets(5));
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(addActionListener);
		JButton exitBtn = new JButton("取消");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// setVisible(false);
				dispose();
			}
		});
		add(okBtn, new GBC(0, 1).setInsets(5, 5, 5, 0));
		add(exitBtn, new GBC(1, 1).setInsets(5, 0, 5, 5));

		// setResizable(false);
		setSize(320, 200);
		setLocation(Util.calculatePosition(owner, this));
		pack();
	}

	private JTextField rackCountTxt;

	private LampUnitDAO lampUnitDAO;
	private LampRackDAO lampRackDAO;
}
