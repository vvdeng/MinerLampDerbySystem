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
										"             端口被占用， 启动失败              \n请检查是否有实例在运行");
					} else {
						if(e.getCause()!=null){
							Throwable cex=e.getCause();
							if(cex.getClass().equals(SQLException.class)){
								String em = cex.getMessage();
								if (em.contains("未找到")) {
									JOptionPane
									.showMessageDialog(null,"数据库不存在，请创建数据库");
								} else if (em.contains("无法使用类加载器")) {
									JOptionPane
									.showMessageDialog(null, "连接失败，请先关闭其他程序");
								} 
							}
						}
						else{
						JOptionPane
								.showMessageDialog(null,
										"              启动失败              \n请检查数据库运行或者网络连接是否正常");
						}
					}
					e.printStackTrace();
				}
			}
		});
	}
}
