package com.vv.minerlamp.comm;

import java.util.List;

public class CommCmdObject {
	public static int COMM_CMD_UPDATE_STAFF=0;
	public static int COMM_CMD_BRORAD_MSG=1;
	public static int COMM_CMD_OPEN_DOOR=2;
	
	private int cmdNo;
	private List< byte[]> dat;
	
	public CommCmdObject() {
		super();
	}

	public CommCmdObject(int cmdNo) {
		super();
		this.cmdNo = cmdNo;
	}

	public CommCmdObject(int cmdNo, List< byte[]> dat) {
		super();
		this.cmdNo = cmdNo;
		this.dat = dat;
	}

	public int getCmdNo() {
		return cmdNo;
	}

	public void setCmdNo(int cmdNo) {
		this.cmdNo = cmdNo;
	}

	public List< byte[]> getDat() {
		return dat;
	}

	public void setDat(List< byte[]> dat) {
		this.dat = dat;
	}
	
	
}
