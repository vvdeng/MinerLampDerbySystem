package com.vv.minerlamp.util;

public class CommState {
	public static final int ADDR = 1;
	public static final int CMD = 2;
	public static final int SEND_DATA = 3;
	public static final int WAIT_DATA = 4;
	public static final int LISTEN_REPLY = 5;
	public static final int FINISHED = 6;
	public static final int PEER_RECEIVE_READY = 1;
	public static final int PEER_SEND_READY = 2;
	public static final int ADDR_LENGTH = 1;
	public static final int CMD_LENGTH = 1;
	public static final int LISTEN_REPLY_LENGTH = 1;
}
