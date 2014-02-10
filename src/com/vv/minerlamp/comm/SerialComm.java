package com.vv.minerlamp.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;
import javax.swing.JOptionPane;

import com.vv.minerlamp.MinerLampFrame;
import com.vv.minerlamp.entity.LedSetting;
import com.vv.minerlamp.entity.Staff;
import com.vv.minerlamp.util.CommState;
import com.vv.minerlamp.util.CommUtil;
import com.vv.minerlamp.util.RackPacket;
import com.vv.minerlamp.util.StaffState;
import com.vv.minerlamp.util.SysConfiguration;

public class SerialComm implements /* Runnable, */SerialPortEventListener {
	public static SerialComm sysSerialComm = null;
	private MinerLampFrame minerLampFrame;
	private CommPortIdentifier portId;
	private InputStream inputStream;
	private OutputStream outputStream;
	private SerialPort serialPort;
	private boolean isRefreshed;
	public static int dataType;
	public static int state;
	// private Thread readThread;
	public static final int BUFFER_SIZE = 1024;
	public static List<String> portNameList;
	public static final int WAIT_TIME = 2000;
	public static final int DATA_TYPE_NOTHING = 0;
	public static final int SEND_LED_MESSAGES = 1;
	public static final int DATA_TYPE_UNITS_INFO = 2;
	public static final int SEND_ALL_LED_MESSAGES = 3;
	public static final int UPDATE_STAFF_INFO = 4;
	public static final int DATA_TYPE_SINGLE_UNIT_INFO = 5;
	public static final int DATA_TYPE_UNITS_INFO_LENTTH = 101;
	// TODO //命令号最高位更改为1 广播1000 0000 ；获取充电状态1010 00000；更新员工信息1100 0000；
	public static final byte CMD_SEND_ALL_LED_MESSAGES = (byte) 0x80;// 广播LED文字信息
	public static final byte CMD_REQ_UNIT_INFO = (byte) 0xA0;// 获取充电状态信息
	public static final byte CMD_UPDATE_STAFF_INFO = (byte) 0xC0;// 更新员工信息
	public static final byte DATA_PRE_UNIT_INFO = 0x50; // 充电状态数据前缀
	public static final byte DATA_PRE__UPDATE_RACK_STAFF = 0x60; // 更新员工信息前缀
	public static final byte DATA_PRE__NULL = 0x00; // 空前缀
	private byte[] buffers;
	private int index;
	private boolean serialCommBusy;
	private boolean serialCommNotConnected;
	private int currentAddr = 1;
	private Staff currentStaff;
	private ReplyTimeoutThread[] replyTimeoutThreads;

	public static void main(String[] args) {

		// SerialComm serialComm = new SerialComm();
		// while (true) {
		// BufferedReader reader = new BufferedReader(new InputStreamReader(
		// System.in));
		// try {
		// String data = reader.readLine();
		// serialComm.write(data.getBytes());
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
	}

	@SuppressWarnings("restriction")
	public SerialComm(MinerLampFrame minerLampFrame) {
		this.minerLampFrame = minerLampFrame;
		replyTimeoutThreads = new ReplyTimeoutThread[SysConfiguration.rackCount + 1];// 序号0废弃
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		portNameList = new ArrayList<String>();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				portNameList.add(portId.getName());
				if (portId.getName().toUpperCase()
						.equals(SysConfiguration.commPort)) {
					try {
						serialPort = (SerialPort) portId.open("MinerLampApp",
								WAIT_TIME);

					} catch (PortInUseException e) {
						serialCommBusy = true;
						e.printStackTrace();
					}
				}

			}
		}
		Collections.sort(portNameList);
		System.out.println(serialPort);
		if (serialPort == null) {

			serialCommNotConnected = true;
		}
		if (isSerialCommOk()) {
			try {
				if (serialCommBusy == false && serialCommNotConnected == false) {
					inputStream = serialPort.getInputStream();
					outputStream = serialPort.getOutputStream();
				}

			} catch (IOException e) {

				e.printStackTrace();
			}
			try {
				if (serialCommBusy == false && serialCommNotConnected == false) {
					serialPort.addEventListener(this);
				}
			} catch (TooManyListenersException e) {
			}
			serialPort.setInputBufferSize(BUFFER_SIZE);
			serialPort.notifyOnDataAvailable(true);

			try {
				serialPort.setSerialPortParams(SysConfiguration.baudRate,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
			}
			buffers = new byte[BUFFER_SIZE];
			// readThread = new Thread(this);
			// readThread.start();
		}
	}

	public void write(byte[] bytes) {
		if (bytes == null) {
			return;
		}
		try {
			System.out.print("send bytes...");
			for (int i = 0; i < bytes.length; i++) {
				System.out.print(" bytes[" + i + "] =" + bytes[i]);
			}
			System.out.println();

			outputStream.write(bytes);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	//		JOptionPane.showMessageDialog(null, "通信异常，请重新插拔计算机串口的连接");
			JOptionPane.showMessageDialog(null,
					"通信异常，请重新插拔计算机串口的连接后\n重启本软件");
			System.exit(0);
		}
	}

	public void run() {
		// try {
		// Thread.sleep(20000);
		// } catch (InterruptedException e) {
		// }

	}

	public void serialEvent(SerialPortEvent event) {

		switch (event.getEventType()) {
		
		case SerialPortEvent.BI:
			break;
		case SerialPortEvent.OE:
			break;
		case SerialPortEvent.FE:
			break;
		case SerialPortEvent.PE:
			break;
		case SerialPortEvent.CD:
			break;
		case SerialPortEvent.CTS:
			break;
		case SerialPortEvent.DSR:
			break;
		case SerialPortEvent.RI:
			break;
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;

		case SerialPortEvent.DATA_AVAILABLE:

			try {
				while (inputStream.available() > 0) {
					int c = inputStream.read();
					System.out.println("c=" + c + " state=" + state + " index="
							+ index + " dataType=" + dataType + " data=" + c);
					if (isCmd(c)) {
						continue;
					}
					if (dataType == DATA_TYPE_NOTHING) {
						continue;
					}
					if (dataType == SEND_LED_MESSAGES) {

						buffers[index++] = (byte) c;

						if (state == CommState.ADDR
								&& index == CommState.ADDR_LENGTH) {
							System.out.println("addr=" + buffers[0]);
							if (buffers[index - 1] != currentAddr) {
								clearBuffer();
								break;
							}
							clearBuffer();
							System.out.println();
							state = CommState.CMD;
							write(new byte[] { 1 });
						}
						if (state == CommState.CMD
								&& index == CommState.CMD_LENGTH) {
							System.out.println("cmd=" + buffers[0]);
							clearBuffer();
							state = CommState.WAIT_DATA;
							write(new byte[] { 0 });
							write(CommUtil.processData(
									new byte[] { CommUtil.messageInteval
											.byteValue() }, DATA_PRE__NULL));
							for (int i = 0; i < CommUtil.MESSAGE_NUM; i++) {
								write((CommUtil.processData(CommUtil
										.fillSentence(CommUtil.ledSettingList
												.get(i).getContent(),
												CommUtil.SENTENCE_LENGTH),
										DATA_PRE__NULL)));
							}
							clearBuffer();
							state = CommState.FINISHED;
							dataType = DATA_TYPE_NOTHING;
						}
					} else if (dataType == DATA_TYPE_UNITS_INFO) {
						// System.out.println(" curentAddr in dataTypeUnitsInfo="+currentAddr);
						replyTimeoutThreads[currentAddr].setReplyed(true);
						// if (replyTimeoutThreads[currentAddr].isAlive()) {
						// replyTimeoutThreads[currentAddr].stop();
						// }
						buffers[index++] = (byte) c;

						if (state == CommState.WAIT_DATA
								&& index == DATA_TYPE_UNITS_INFO_LENTTH) {
							replyTimeoutThreads[currentAddr].setFinished(true);
							if (replyTimeoutThreads[currentAddr].isAlive()) {
								replyTimeoutThreads[currentAddr].stop();
							}
							state = CommState.FINISHED;

							setUnitsRefreshed(true);
							// reqNextRackUnitInfo(); //改为在activity中调用
						}

					} else if (dataType == DATA_TYPE_SINGLE_UNIT_INFO) {
						// System.out.println(" curentAddr in dataTypeUnitsInfo="+currentAddr);
						replyTimeoutThreads[currentAddr].setReplyed(true);
						// if (replyTimeoutThreads[currentAddr].isAlive()) {
						// replyTimeoutThreads[currentAddr].stop();
						// }
						buffers[index++] = (byte) c;

						if (state == CommState.WAIT_DATA
								&& index == DATA_TYPE_UNITS_INFO_LENTTH) {
							replyTimeoutThreads[currentAddr].setFinished(true);
							if (replyTimeoutThreads[currentAddr].isAlive()) {
								replyTimeoutThreads[currentAddr].stop();
							}
							state = CommState.FINISHED;

							setUnitsRefreshed(true);
							// reqNextRackUnitInfo(); //改为在activity中调用
						}

					} else if (dataType == UPDATE_STAFF_INFO) {

					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	private boolean isCmd(int c) {
		if ((c & 0x80) == 0x80) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isNewDataCome() {
		return isRefreshed;
	}

	public void clearBuffer() {
		index = 0;
	}

	public void setUnitsRefreshed(boolean isRefreshed) {

		this.isRefreshed = isRefreshed;
		if (this.isRefreshed == true) {
			StaffState.rackPacket = new RackPacket(buffers);

			clearBuffer();
			minerLampFrame.notifyActivity();
		}

	}

	public boolean reqNextRackUnitInfo() {
		boolean finished = false;
		if (currentAddr < SysConfiguration.rackCount) {
			currentAddr++;
			state = CommState.WAIT_DATA;
			write(new byte[] { (byte) makeCmdAndAddr(CMD_REQ_UNIT_INFO,
					currentAddr) });
			replyTimeoutThreads[currentAddr] = new ReplyTimeoutThread();
		} else {
			finished = true;

			dataType = DATA_TYPE_NOTHING;
			minerLampFrame.notifyActivity();
			;
		}
		return finished;
	}

	public byte[] getBuffers() {
		return buffers;
	}

	public void setBuffers(byte[] buffers) {
		this.buffers = buffers;
	}

	public boolean isCommand(byte b) {
		return (0x80 & b) == 0x80;
	}

	public boolean isSerialCommBusy() {
		return serialCommBusy;
	}

	public boolean isSerialCommNotConnected() {
		return serialCommNotConnected;
	}

	public void setSerialCommNotConnected(boolean serialCommNotConnected) {
		this.serialCommNotConnected = serialCommNotConnected;
	}

	public void setSerialCommBusy(boolean serialCommBusy) {
		this.serialCommBusy = serialCommBusy;
	}

	public boolean isSerialCommOk() {
		return this.serialCommBusy == false
				&& this.serialCommNotConnected == false
				&& this.dataType == DATA_TYPE_NOTHING;
	}

	public void reqUnitInfo() {
		if (isSerialCommOk()) {

			clearBuffer();
			dataType = SerialComm.DATA_TYPE_UNITS_INFO;
			state = CommState.WAIT_DATA;
			currentAddr = 1;
			write(new byte[] { makeCmdAndAddr(CMD_REQ_UNIT_INFO, currentAddr) });
			replyTimeoutThreads[currentAddr] = new ReplyTimeoutThread();

		}
	}

	public void reqSingleUnitInfo(Long rackNo) {
		if (isSerialCommOk()) {

			clearBuffer();
			dataType = SerialComm.DATA_TYPE_SINGLE_UNIT_INFO;
			state = CommState.WAIT_DATA;
			currentAddr = rackNo.intValue();
			write(new byte[] { makeCmdAndAddr(CMD_REQ_UNIT_INFO, currentAddr) });
			replyTimeoutThreads[currentAddr] = new ReplyTimeoutThread();

		}
	}

	public void updateStaffInfo(Staff staff) {
		if (isSerialCommOk()) {
			currentStaff = staff;
			clearBuffer();
			dataType = SerialComm.UPDATE_STAFF_INFO;
			state = CommState.ADDR;
			System.out.println("addr=" + currentStaff.getRackId().intValue());
			write(new byte[] { makeCmdAndAddr(CMD_UPDATE_STAFF_INFO,
					currentStaff.getRackId().intValue()) });
			write(CommUtil.processData(new byte[] { currentStaff.getLampNo()
					.byteValue() }, DATA_PRE__UPDATE_RACK_STAFF));
			// write(new byte[] {(byte) 0xC5 });
			state = CommState.WAIT_DATA;
			write((CommUtil.processData(CommUtil.fillSentence(staff.getName(),// 姓名
					CommUtil.STAFF_INFO_FIELD_LENGTN),
					DATA_PRE__UPDATE_RACK_STAFF)));
			write((CommUtil.processData(
					CommUtil.fillSentence(staff.getDepartment(),// 部门
							CommUtil.STAFF_INFO_FIELD_LENGTN),
					DATA_PRE__UPDATE_RACK_STAFF)));
			write((CommUtil.processData(
					CommUtil.fillSentence(staff.getProfession(),// 工种
							CommUtil.STAFF_INFO_FIELD_LENGTN),
					DATA_PRE__UPDATE_RACK_STAFF)));
			state = CommState.FINISHED;
			dataType = SerialComm.DATA_TYPE_NOTHING;
		}
	}

	public void sendLedMessages(List<LedSetting> ledSettingList,
			int messageInteval) {
		if (isSerialCommOk()) {
			CommUtil.ledSettingList = ledSettingList;
			CommUtil.messageInteval = messageInteval;
			clearBuffer();
			dataType = SerialComm.SEND_LED_MESSAGES;
			state = CommState.ADDR;
			currentAddr = 1;
			write(new byte[] { makeCmdAndAddr(CMD_REQ_UNIT_INFO, currentAddr) });

		}
	}

	public void sendAllLedMessages(List<LedSetting> ledSettingList,
			int messageInteval) {
		if (isSerialCommOk()) {
			CommUtil.messageNum = ledSettingList.size();
			CommUtil.ledSettingList = ledSettingList;
			CommUtil.messageInteval = messageInteval;
			// clearBuffer();
			dataType = SerialComm.SEND_ALL_LED_MESSAGES;

			write(new byte[] { CMD_SEND_ALL_LED_MESSAGES });// 广播命令号
			write(new byte[] { 1, 3, 5 });// 需重构 1,3,5 (广播标志位)
			write(CommUtil.processData(
					new byte[] { CommUtil.messageNum.byteValue() },
					DATA_PRE__NULL));

			write(CommUtil.processData(
					new byte[] { CommUtil.messageInteval.byteValue() },
					DATA_PRE__NULL));
			if (CommUtil.messageNum > 0) {
				for (int i = 0; i < CommUtil.messageNum; i++) {
					write((CommUtil.processData(CommUtil.fillSentence(
							CommUtil.ledSettingList.get(i).getContent(),
							CommUtil.SENTENCE_LENGTH), DATA_PRE__NULL)));
				}
			}
			dataType = SerialComm.DATA_TYPE_NOTHING;
		}
	}

	public byte makeCmdAndAddr(int cmd, int addr) {
		currentAddr = (byte) addr;
		return (byte) (cmd | addr);
	}

	class ReplyTimeoutThread extends Thread {
		private boolean replyed;
		private boolean finished;

		public ReplyTimeoutThread() {
			this.replyed = false;
			this.finished = false;
			this.start();
		}

		@Override
		public void run() {
			try {
				sleep(SysConfiguration.rackThreadSleepTime);
				if (finished) {

				} else {
					System.out.println("rack" + currentAddr
							+ " reply timeout...");
					if (dataType == DATA_TYPE_UNITS_INFO) {
						reqNextRackUnitInfo();
					} else if (dataType == DATA_TYPE_SINGLE_UNIT_INFO) {
						dataType = DATA_TYPE_NOTHING;
					}
				}
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

		public boolean isReplyed() {
			return replyed;
		}

		public void setReplyed(boolean replyed) {
			this.replyed = replyed;
		}

		public boolean isFinished() {
			return finished;
		}

		public void setFinished(boolean finished) {
			this.finished = finished;
		}

	}
}
