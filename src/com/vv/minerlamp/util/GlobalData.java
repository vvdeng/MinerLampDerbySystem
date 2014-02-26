package com.vv.minerlamp.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.vv.minerlamp.comm.CommCmdObject;



public class GlobalData {
	public static BlockingQueue<CommCmdObject> cmdQueue = new ArrayBlockingQueue<CommCmdObject>(10);
	public static volatile boolean refreshSelRack=false;
	public static volatile int selRackId=1;
}
