package com.vv.minerlamp.util;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.vv.minerlamp.comm.CommCmdObject;
import com.vv.minerlamp.entity.SysInf;



public class GlobalData {
	public static BlockingQueue<CommCmdObject> cmdQueue = new ArrayBlockingQueue<CommCmdObject>(10);
	public static volatile boolean refreshSelRack=false;
	public static volatile int selRackId=1;
	public static HashMap<Integer,SysInf> sysInfoMap=new HashMap<Integer, SysInf>();
	public  static   String machineCode=null;
	public static final int MACHINE_CODE_LEN=39;

}
