package com.vv.minerlamp.util;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.vv.minerlamp.comm.SerialComm;
import com.vv.minerlamp.entity.LedSetting;

public class CommUtil {
	public static final int MESSAGE_NUM=6;
	public static final int SENTENCE_LENGTH = 32;
	public static final int STAFF_INFO_FIELD_LENGTN=8;
	public static final int SENTENCE_STR_LENGTH = 32;
	public static List<LedSetting> ledSettingList; 
	public static Integer messageInteval;
	public static Integer messageNum;
	public static byte[] fillSentence(String str, int length) {
		byte[] bytes = new byte[length];
		try {
			byte[] strBytes = str.getBytes("gbk");
			int len = strBytes.length > SENTENCE_STR_LENGTH ? SENTENCE_STR_LENGTH
					: strBytes.length;
			for (int i = 0; i < len; i++) {
				bytes[i] = strBytes[i];
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return bytes;
	}

	public static void main(String[] args) {
		String str = "";
		byte[] bytes = fillSentence(str, 15);
		for (int i = 0; i < bytes.length; i++) {
			System.out.println(" " + bytes[i]);
		}
	}

	public static byte[] processData(byte[] bytes,byte pre) {
		if (bytes == null) {
			return null;
		}
		int verify = 0;
		int len = bytes.length * 2;
		byte[] processedData = new byte[len];
		for (int i = 0; i < len; i++) {
			(processedData[i]) = (byte) (i % 2 == 0 ? (((bytes[i / 2] >> 4) & 0x0f)|pre)
					: ((bytes[i / 2] & 0x0f)|pre));
			verify += processedData[i];
		}
		// processedData[len]=(byte)(verify%256);
		return processedData;
	}

	public static byte[] displayString(String str, boolean display) {
		byte[] bytes = null;

		return bytes;
	}


}
