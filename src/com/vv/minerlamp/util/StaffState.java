package com.vv.minerlamp.util;

import com.vv.minerlamp.comm.SerialComm;

public class StaffState {
	public static final int LAMP_NULL = 0;
	public static final int STAFF_UNDEFINED = 1;
	public static final int STAFF_DEFINED = 2;
	public static final int STAFF_UNDERGROUND = 3;
	public static final int LAMP_CHARGING = 4;
	public static final int LAMP_FULL = 5;
	public static final int LAMP_ERROR = 6;
	public static final int LAMP_OFFLINE = 7;
	public static int[] unitsStateArr = new int[SerialComm.DATA_UNITS_INFO_LENTTH];
	public static final int UNIT_STATE_NULL = 0;
	public static final int UNIT_STATE_EMPTY = 1;
	public static final int UNIT_STATE_CHARGING = 2;
	public static final int UNIT_STATE_FULL = 3;
	public static final int UNIT_STATE_ERROR = 4;
	public static final int UNIT_STATE_OFFLINE = 5;
	public static RackPacket rackPacket;

	public static int convertUnitState(int unitState) {
		// TODO ²âÊÔÓÃ
		// if (unitState > 4) {
		// unitState = (unitState %100)/25;
		// }
		if (unitState == UNIT_STATE_CHARGING) {
			return LAMP_CHARGING;
		} else if (unitState == UNIT_STATE_FULL) {
			return LAMP_FULL;
		} else if (unitState == UNIT_STATE_ERROR) {
			return LAMP_ERROR;

		} else if (unitState == UNIT_STATE_OFFLINE) {
			return LAMP_OFFLINE;

		} else if ( unitState == UNIT_STATE_NULL) {
			return LAMP_NULL;
		} else {
			return STAFF_UNDERGROUND;
		}
	}

}
