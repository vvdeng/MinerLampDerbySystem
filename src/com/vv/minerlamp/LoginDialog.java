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

import com.vv.minerlamp.dao.UserDAO;
import com.vv.minerlamp.entity.User;
import com.vv.minerlamp.util.SysConfiguration;
import com.vv.minerlamp.util.Util;

class LoginDialog extends JDialog
{
   public LoginDialog(final MinerLampFrame owner)
   {
      super(owner, "登录", true);
      setIconImage(SysConfiguration.sysIcon);
      userDAO=new UserDAO();
      setLayout(new GridBagLayout());
      JPanel panel=new JPanel();
      add(panel,new GBC(0, 0).setWeight(100, 100).setFill(GridBagConstraints.BOTH));
      panel.setLayout(new GridBagLayout());
      panel.add(new JLabel(new ImageIcon("resources/login.png")),new GBC(0, 0,GridBagConstraints.REMAINDER,1).setFill(GridBagConstraints.HORIZONTAL).setWeight(100, 0));
      
      panel.add(new JLabel("用户名："),new GBC(0,1).setWeight(100, 0).setAnchor(GridBagConstraints.EAST));
      panel.add(new JLabel("  密码："),new GBC(0,2).setWeight(100, 0).setAnchor(GridBagConstraints.EAST));
      nameTxt=new JTextField();
   //   nameTxt.setText("       ");
      nameTxt.setPreferredSize(new Dimension(185,20));
      pwdTxt=new JPasswordField();
 //     pwdTxt.setText("       ");
      pwdTxt.setPreferredSize(new Dimension(185,20));
      ActionListener loginActionListener=new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			User currentUser=userDAO.checkLogin(nameTxt.getText(), pwdTxt.getText());
			if(currentUser!=null){
        		owner.showRightPanel(true);
        		owner.toolbarEnabled(true);
        		owner.toolbarAndMenuEnabled(true);
        	owner.setCurrentUser(currentUser);
        		dispose();
        	}
        	else{

        		JOptionPane.showMessageDialog(owner, "用户名或者密码不正确");
        	}
			
		}
	};
      pwdTxt.registerKeyboardAction(loginActionListener,
              KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
              JComponent.WHEN_FOCUSED);
      panel.add(nameTxt,new GBC(1,1,2,1).setWeight(100, 0).setAnchor(GridBagConstraints.WEST).setInsets(5));
      panel.add(pwdTxt,new GBC(1,2,2,1).setWeight(100, 0).setAnchor(GridBagConstraints.WEST).setInsets(5));
      JButton loginBtn = new JButton("登录");
      loginBtn.addActionListener(loginActionListener);
      JButton exitBtn = new JButton("退出");
      exitBtn.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
             //  setVisible(false);
            	dispose();
            }
         });
      panel.add(loginBtn, new GBC(1, 3).setInsets(5,5,5,0));
      panel.add(exitBtn, new GBC(2, 3).setInsets(5,0,5,5));
      
      setResizable(false);
      setSize(320, 200);
      setLocation(Util.calculatePosition(owner, this));
      pack();
   }
   private JTextField nameTxt;
   private JPasswordField pwdTxt;
   private UserDAO userDAO;
}
