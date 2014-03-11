package com.vvdeng.minerlamp.security;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WindowsSequenceService {

	private static final Logger log = LoggerFactory
			.getLogger(WindowsSequenceService.class);

	public String getSequence() {

		String cpuID = getCPUSerial();

		String hdID = getHDSerial("C");

		if (cpuID == null || hdID == null) {

			return null;

		}

		String machineCode = getMD5(cpuID + hdID);

		machineCode = Utils.getSplitString(machineCode);

		return machineCode;

	}

	/**
	 * 
	 * 对一段String生成MD5加密信息
	 * 
	 * @param message
	 *            要加密的String
	 * 
	 * @return 生成的MD5信息
	 */

	private String getMD5(String message) {

		message += "D2S";

		try {

			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] b = md.digest(message.getBytes("utf-8"));

			return Utils.byte2HexString(b) + message.length();

		} catch (NoSuchAlgorithmException e) {

			log.error("获取机器码失败：", e);

		} catch (UnsupportedEncodingException e) {

			log.error("获取机器码失败：", e);

		}

		return null;

	}

	/**
	 * 
	 * 
	 * 
	 * @param drive
	 *            硬盘驱动器分区 如C,D
	 * 
	 * @return 该分区的卷标
	 */

	private String getHDSerial(String drive) {

		String result = "";

		try {

			File file = File.createTempFile("tmp", ".vbs");

			file.deleteOnExit();

			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"

					+ "Set colDrives = objFSO.Drives\n"
					+ "Set objDrive = colDrives.item(\"" + drive + "\")\n"

					+ "Wscript.Echo objDrive.SerialNumber";

			fw.write(vbs);

			fw.close();

			Process p = Runtime.getRuntime().exec(
					"cscript //NoLogo " + file.getPath());

			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line;

			while ((line = input.readLine()) != null) {

				result += line;

			}

			input.close();

			file.delete();

		} catch (Exception e) {

		}

		if (result.trim().length() < 1 || result == null) {

			log.debug("无磁盘ID被读取");

			return null;

		}

		return result.trim();

	}

	/**
	 * 
	 * 获取CPU号,多CPU时,只取第一个
	 * 
	 * @return
	 */

	private String getCPUSerial() {

		String result = "";

		try {

			File file = File.createTempFile("tmp", ".vbs");

			file.deleteOnExit();

			FileWriter fw = new FileWriter(file);

			String vbs = "On Error Resume Next \r\n\r\n"
					+ "strComputer = \".\"  \r\n"

					+ "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"

					+ "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"

					+ "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "

					+ "For Each objItem in colItems\r\n "
					+ "    Wscript.Echo objItem.ProcessorId  \r\n "

					+ "    exit for  ' do the first cpu only! \r\n"
					+ "Next                    ";

			fw.write(vbs);

			fw.close();

			Process p = Runtime.getRuntime().exec(
					"cscript //NoLogo " + file.getPath());

			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line;

			while ((line = input.readLine()) != null) {

				result += line;

			}

			input.close();

			file.delete();

		} catch (Exception e) {

			e.fillInStackTrace();

		}

		if (result.trim().length() < 1 || result == null) {

			log.debug("无CPU_ID被读取");

			return null;

		}

		return result.trim();

	}

	public static void main(String[] args) {
		System.out.println(new Date());
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
			 System.out.println("executed "+new Date());
			}
		};
		new Timer(5000, taskPerformer).start();
	//	WindowsSequenceService s = new WindowsSequenceService();

	//	String seq = s.getSequence();

	//	System.out.println(seq);
		
while(true);
	}

}