package com.vv.minerlamp.util;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.vv.minerlamp.comm.SerialComm;

public class RackPacket {
	private int addr;
	private static int[][] data = new int[SysConfiguration.rackCount + 1][];// 灯架索引0不用
	private static byte[][] originData = new byte[SysConfiguration.rackCount + 1][];// 保存模拟量原始数据

	public RackPacket(byte[] ldata) {
		this.addr = ldata[0];
		System.out.println("addr="+addr);
		if(addr>SysConfiguration.rackCount){
			
			System.out.println("数据出错!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		this.originData[addr] = new byte[SerialComm.DATA_UNITS_INFO_LENTTH];
		data[addr] = new int[SerialComm.DATA_UNITS_INFO_LENTTH - 1];//索引从0开始，对应灯座1
		// System.arraycopy(ldata, 0, originData[addr], 0,
		// originData[addr].length);
		
		this.originData[addr][0] = ldata[0];// 地址位没有加数据前缀，单独处理
		for (int i = 1; i < SerialComm.DATA_UNITS_INFO_LENTTH; i++) {
			if ((ldata[i] & 0xf0) == SerialComm.DATA_PRE_UNIT_INFO) {
				this.originData[addr][i] = (byte) (ldata[i] & 0x0f);
			}
			else{
				this.originData[addr][i] =0;
			}
			data[addr][i-1] = StaffState.convertUnitState(this.originData[addr][i]);
		}
//		System.out.println("slave addr=" + addr);

	}

	public int getAddr() {
		return addr;
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public int[] getData(int index) {
		return data[index];
	}

	public byte[] getOriginData(int index) {
		return originData[index];
	}

}