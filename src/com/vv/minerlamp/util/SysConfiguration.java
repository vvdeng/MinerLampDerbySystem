package com.vv.minerlamp.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.vv.minerlamp.entity.RackStatistics;

public class SysConfiguration {
	public static final int COUNT_LIMIT=0;
	public static final int TIME_LIMIT=1;
	public static final String FILE_PATH = "init.properties";
	public static final String DBCONFIG_FILE_PATH = "db.properties";
	public static final int SYS_STATE_RUNNING = 1;
	public static final int SYS_STATE_REBOOT = 2;
	public static String commPort;
	public static Integer baudRate;
	public static Integer delay;
	public static Integer rackThreadSleepTime;
	public static Integer rackCount;
	public static Integer rackRow;
	public static Integer rackColumn;
	public static Integer rackSequence;
	public static Integer rackSerial;
	public static Integer isCountLimited;
	public static Integer limitCount;
	public static Integer limitTime;
	public static Integer timeOutTime;
	public static Integer standardWorkTime = 8;
	public static Integer ledContentChangeTime;
	public static String dbUrl;
	public static String dbIp;
	public static Integer dbPort;
	public static String dbUserName;
	public static String dbPwd;
	public static String dbName = "minerlamp";
	public static String backupFileName = "智能管理系统数据备份.dat";
	public static Image sysIcon = null;
	public static String clazz0 = "0点";
	public static String clazz8 = "8点";
	public static String clazz16 = "16点";
	public static String tempDir = "temp/";
	public static int sysState=SYS_STATE_REBOOT;
	public static void init() {
		Properties props = PropertiesUtil.getProperties("init.properties");
		Properties dbProps = PropertiesUtil.getProperties("db.properties");
		commPort = props.get("commPort").toString();
		baudRate = new Integer(props.get("baudRate").toString());
		delay=new Integer(props.get("delay").toString())*1000;
		rackThreadSleepTime=new Integer(props.get("rackThreadSleepTime").toString());
		rackCount = new Integer(props.get("rackCount").toString());
		rackRow = new Integer(props.get("rackRow").toString());
		rackColumn = new Integer(props.get("rackColumn").toString());
		isCountLimited = new Integer(props.get("isCountLimited").toString());
		rackSequence = new Integer(props.get("rackSequence").toString());
		rackSerial = new Integer(props.get("rackSerial").toString());
		limitCount = new Integer(props.get("limitCount").toString());
		limitTime = new Integer(props.get("limitTime").toString());
		timeOutTime = new Integer(props.get("timeOutTime").toString());
		ledContentChangeTime = new Integer(props.get("ledContentChangeTime")
				.toString());
		parse(dbProps.get("hibernate.connection.url").toString());
		dbUserName = dbProps.get("hibernate.connection.username").toString();
		dbPwd = dbProps.get("hibernate.connection.password").toString();
		try {
			sysIcon = ImageIO.read(new File("resources/minerlamp.png"));

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void parse(String url) {
		Pattern pattern = Pattern.compile(".*//(.*):(\\d*)/.*");
		Matcher matcher = pattern.matcher(url);

		if (matcher.find()) {
			dbIp = matcher.group(1);
			dbPort = new Integer(matcher.group(2));
		}
	}

	public static String makeDbUrl(String ip, Integer port) {
		return new StringBuilder().append("jdbc:mysql://").append(ip)
				.append(":").append(port).append("/minerlamp").toString();
	}

	public static void main(String[] args) {
		init();
	}
}
