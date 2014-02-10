package com.vv.minerlamp.util;

public class StaffAction {
	public static final int NOTHING_TO_DO = 0;
	public static final int DEFINE_STAFF = 1;
	public static final int EDIT_STAFF = 2;
	public static final int DELETE_STAFF = 3;
	public static final int TAKE_AWAY = 4;
	public static final int PUT_ON = 5;
	public static final int CHARGING_OK = 6;
	public static final int OVER_TIME = 7;
	public static final int ERROR_HAPPENS = 8;
	public static final int ERROR_REPARED = 9;
	public static final int FIRSTTIME_CHARGING = 10;
	public static final int OFFLINE = 11;
	public static final int ONLINE = 12;
	public static final int TIME_TOO_SHORT = 13; //允许电池状态变化，但不作为考勤统计 可描述为： 过早归位
	public static final int BAT_ALREADY_FULL=14; //从 下井或已定义状态直接到充满状态 。矿灯并未使用，不作为考勤
}
