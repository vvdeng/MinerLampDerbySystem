package com.vv.minerlamp.util;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.vv.minerlamp.GBC;
import com.vv.minerlamp.entity.User;

public class Util {
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat dateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static DateFormat dateTimeFormat4CreateFile = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public static String formatDate(Date date) {
		if (date == null) {
			return null;
		}
		return dateFormat.format(date);
	}

	public static String formatDateTime(Date date) {
		return formatDateTime(date, dateTimeFormat);
	}

	public static String formatDateTime(Date date, DateFormat df) {
		if (date == null) {
			return null;
		}
		return df.format(date);
	}

	public static Date parseDate(String dateStr) {
		Date date = new Date();
		if (dateStr != null) {
			try {
				date = dateFormat.parse(dateStr);
			} catch (ParseException e) {

				e.printStackTrace();
			}
		}
		return date;
	}

	public static Point calculatePosition(Window parent, JDialog dialog) {
		int x = parent.getX() + (parent.getWidth() - dialog.getWidth()) / 2;
		int y = parent.getY() + (parent.getHeight() - dialog.getHeight()) / 2;
		Point p = new Point(x, y);
		return p;

	}

	public static JButton makeButton(Action action) {
		JButton button = new JButton(action);
		return button;
	}

	public static JButton makePrivButton(Action action, User user) {
		JButton button = new JButton(action);
		if (user == null || user.getPriv() == User.READ_ONLY) {
			button.setEnabled(false);
		}
		return button;
	}

	public static GBC fillParentPanel() {
		return new GBC(0, 0).setWeight(100, 100).setFill(
				GridBagConstraints.BOTH);
	}

	public static List<Long> makeArray(Long num) {
		List<Long> numList = new ArrayList<Long>();
		for (int i = 1; i <= num; i++) {
			numList.add(new Long(i));
		}
		return numList;
	}

	public static int backup(String url, String name, String pwd,
			String dbName, String file) throws IOException,
			InterruptedException {
		// “mysqldump -u username -pPassword --opt database_name >
		// direction/backup_name.sql”
		int result = -1;
		String str = "mysqldump -u " + name + " -p" + pwd + " --opt   "
				+ dbName + " >  \"" + file + "\"";
		System.out.println(str);
		Runtime rt = Runtime.getRuntime();
		Process process = rt.exec("cmd /c" + str);
		result = process.waitFor();
		System.out.println(result);
		return result;
	}

	public static int load(String url, String name, String pwd, String dbName,
			String file) throws IOException, InterruptedException {
		int result = -1;
		// “mysql -u Username -pPassword database_name < back_up_dir ”
		String str = "mysql -u " + name + " -p" + pwd + " " + dbName
				+ "  <  \"" + file + "\"";
		System.out.println(str);
		Runtime rt = Runtime.getRuntime();
		Process process = rt.exec("cmd /c" + str);
		result = process.waitFor();
		System.out.println(result);

		return result;

	}

	public static NumberFormat getIntegerNumberFormat() {
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(false);
		return nf;
	}

	public static void createExcelFile(String fileName, String title,
			TableModel cellContentModel) {

		try {
			if (cellContentModel == null || cellContentModel.getRowCount() == 0) {
				return;
			}
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(title);
			
			for (int i = 0; i < cellContentModel.getColumnCount(); i++) {
				String columnName = cellContentModel.getColumnName(i);
				if (columnName.contains("日期") || columnName.contains("时间")) {
					sheet.setColumnWidth(i, 5000);
				}
			}
			HSSFRow row = sheet.createRow(0);
			for (int i = 0; i < cellContentModel.getColumnCount(); i++) {
				createCell(wb, row, i, cellContentModel.getColumnName(i));
			}
			for (int i = 0; i < cellContentModel.getRowCount(); i++) {
				row = sheet.createRow(i + 1);
				for (int j = 0; j < cellContentModel.getColumnCount(); j++) {
					Object value = cellContentModel.getValueAt(i, j);
					if (value == null) {
						value = new String();
					}
					if (Date.class.isInstance(value)) {
						value = formatDateTime((Date) value);
					}
					createCell(wb, row, j, value.toString());
				}
			}
			File file = new File(SysConfiguration.tempDir + fileName
					+ formatDateTime(new Date(), dateTimeFormat4CreateFile)
					+ ".xls");
			FileOutputStream fileOut;
			fileOut = new FileOutputStream(file);
			wb.write(fileOut);
			fileOut.close();
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("cmd /c \"" + file.getAbsolutePath() + "\"");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createCell(HSSFWorkbook wb, HSSFRow row, int col,
			String val) {
		HSSFCell cell = row.createCell(col);
		cell.setCellValue(val);
		HSSFCellStyle cellstyle = wb.createCellStyle();
		cellstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
		cellstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellStyle(cellstyle);
	}
	public static ImageIcon getIconFromState(Integer state) {
		ImageIcon icon = new ImageIcon("resources/emptyBattery.png");
		if (state == null) {
			state = StaffState.STAFF_UNDEFINED;
		}
		switch (state) {
		case StaffState.STAFF_UNDEFINED:
			icon = new ImageIcon("resources/battery_notuse.png");
			break;
		case StaffState.STAFF_DEFINED:
			icon = new ImageIcon("resources/battery_empty.png");
			break;
		case StaffState.STAFF_UNDERGROUND:
			icon = new ImageIcon("resources/battery_empty.png");
			break;
		case StaffState.LAMP_CHARGING:
			icon = new ImageIcon("resources/battery_charging.gif");
			break;
		case StaffState.LAMP_FULL:
			icon = new ImageIcon("resources/battery_full.png");
			break;
		case StaffState.LAMP_ERROR:
			icon = new ImageIcon("resources/battery_error.png");
			break;
		case StaffState.LAMP_OFFLINE:
			icon = new ImageIcon("resources/offline.gif");
			break;
		}
		return icon;
	}
}
