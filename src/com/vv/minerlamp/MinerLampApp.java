package com.vv.minerlamp;

import java.awt.EventQueue;
import java.io.IOException;
import java.sql.SQLException;

import javax.comm.PortInUseException;
import javax.swing.JOptionPane;

import org.apache.commons.collections.functors.IfClosure;

import com.vv.minerlamp.util.SysConfiguration;

public class MinerLampApp {
	public static void main(String[] args) {
		/*
		 * Runtime.getRuntime().addShutdownHook(new Thread() { public void run()
		 * { try { System.out.println("sysstate=" + SysConfiguration.sysState);
		 * if (SysConfiguration.sysState == SysConfiguration.SYS_STATE_REBOOT) {
		 * Runtime.getRuntime().exec("javaw -jar MinerLamp.jar"); } } catch
		 * (IOException e) {
		 * 
		 * e.printStackTrace(); } } });
		 */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MinerLampFrame frame = new MinerLampFrame();
				} catch (Exception e) {
					if (e instanceof PortInUseException) {
						JOptionPane
								.showMessageDialog(null,
										"             �˿ڱ�ռ�ã� ����ʧ��              \n�����Ƿ���ʵ��������");
					} else {
						if(e.getCause()!=null){
							Throwable cex=e.getCause();
							if(cex.getClass().equals(SQLException.class)){
								String em = cex.getMessage();
								if (em.contains("δ�ҵ�")) {
									JOptionPane
									.showMessageDialog(null,"���ݿⲻ���ڣ��봴�����ݿ�");
								} else if (em.contains("�޷�ʹ���������")) {
									JOptionPane
									.showMessageDialog(null, "����ʧ�ܣ����ȹر���������");
								} 
							}
						}
						else{
						JOptionPane
								.showMessageDialog(null,
										"              ����ʧ��              \n�������ݿ����л������������Ƿ�����");
						}
					}
					e.printStackTrace();
				}
			}
		});
	}
}
