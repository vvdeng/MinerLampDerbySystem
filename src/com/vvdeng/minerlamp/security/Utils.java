package com.vvdeng.minerlamp.security;

import java.io.BufferedWriter;

import java.io.File;

import java.io.FileWriter;

/**
 * 
 * 
 * 
 * @author ysc
 */

public class Utils {

	public static boolean appendText(String path, String text) {

		try {

			File file = new File(path);

			System.out.println("file:" + file.getPath());

			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
					true));

			writer.write(text);

			writer.close();

			return true;

		} catch (Exception e) {

			e.printStackTrace();

		}

		return false;

	}

	public static String getSplitString(String str) {

		return getSplitString(str, "-", 4);

	}

	public static String getSplitString(String str, String split, int length) {

		int len = str.length();

		StringBuilder temp = new StringBuilder();

		for (int i = 0; i < len; i++) {

			if (i % length == 0 && i > 0) {

				temp.append(split);

			}

			temp.append(str.charAt(i));

		}

		String[] attrs = temp.toString().split(split);

		StringBuilder finalMachineCode = new StringBuilder();

		for (String attr : attrs) {

			if (attr.length() == length) {

				finalMachineCode.append(attr).append(split);

			}

		}

		String result = finalMachineCode.toString().substring(0,
				finalMachineCode.toString().length() - 1);

		return result;

	}

	public static String byte2HexString(byte[] b) {

		char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7',

		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

		char[] newChar = new char[b.length * 2];

		for (int i = 0; i < b.length; i++) {

			newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];

			newChar[2 * i + 1] = hex[b[i] & 0xf];

		}

		return new String(newChar);

	}

	public static String getTimeDes(long ms) {

		int ss = 1000;

		int mi = ss * 60;

		int hh = mi * 60;

		int dd = hh * 24;

		long day = ms / dd;

		long hour = (ms - day * dd) / hh;

		long minute = (ms - day * dd - hour * hh) / mi;

		long second = (ms - day * dd - hour * hh - minute * mi) / ss;

		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		StringBuilder str = new StringBuilder();

		if (day > 0) {

			str.append(day).append("天,");

		}

		if (hour > 0) {

			str.append(hour).append("小时,");

		}

		if (minute > 0) {

			str.append(minute).append("分钟,");

		}

		if (second > 0) {

			str.append(second).append("秒,");

		}

		if (milliSecond > 0) {

			str.append(milliSecond).append("毫秒,");

		}

		if (str.length() > 0) {

			str = str.deleteCharAt(str.length() - 1);

		}

		return str.toString();

	}

}
