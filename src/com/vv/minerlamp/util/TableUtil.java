package com.vv.minerlamp.util;

import java.awt.Component;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import com.vv.minerlamp.entity.LampChangeLog;

public class TableUtil {
	public static final String STAFFBASICINFO_FILENAME="员工基本信息";
	public static final String STAFFBASICINFO_TITLE="员工基本信息";
	public static final String HISTORY_FILENAME="员工历史考勤信息";
	public static final String HISTORY_TITLE="员工历史考勤信息";
	public static final String SINGLEDAY_FILENAME="单日考勤未按时上井信息";
	public static final String SINGLEDAY_TITLE="单日考勤未按时上井信息";
	public static final String UNDERGROUND_STAFF_FILENAME="下井人员信息";
	public static final String UNDERGROUND_STAFF_TITLE="下井人员信息";
	public static final String ALARM_FILENAME="超时告警信息";
	public static final String ALARM_TITLE="超时告警信息";
	public static final String LAMP_USE_FILENAME="矿灯使用统计";
	public static final String LAMP_USE_TITLE="矿灯使用统计";
	public static final String LAMP_OVER_LIMIT_FILENAME="矿灯超限统计";
	public static final String LAMP_CHANGE_FILENAME="矿灯更换记录";
	public static final String LAMP_CHANGE_TITLE="矿灯更换记录";
	public static final String LAMP_OVER_LIMIT_TITLE="矿灯超限记录";
	public static TableModel getModel(final List list,
			final String columnNames[]) {

		final Object result[] = list != null ? list.toArray() : null;

		AbstractTableModel tm = new AbstractTableModel() {
			public int getColumnCount() {
				return columnNames.length;
			}

			public int getRowCount() {
				return list.size();
			}// 取得表格行数

			public Object getValueAt(int row, int column) {
				if (result == null || result.length == 0) {
					return null;
				}
				Object object = ((Object[]) (result[row]))[column];
				if (Date.class.isInstance(object)) {
					return Util.formatDateTime((Date) object);
				}
				return object;
			}

			public String getColumnName(int column) {
				return columnNames[column];
			}

			public void setValueAt(Object value, int row, int column) {
			}

			public Class getColumnClass(int c) {
				if (getValueAt(0, c) == null) {
					return Object.class;
				}
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return tm;
	}

	public static TableCellEditor getCellEditor() {
		return new DefaultCellEditor(new JTextField()) {

			public Component getTableCellEditorComponent(JTable table,
					Object value, boolean isSelected, int row, int column) {

				JTextField editor = (JTextField) super
						.getTableCellEditorComponent(table, value, isSelected,
								row, column);

				if (value != null) {
					editor.setText(value.toString());
				}
				editor.setHorizontalAlignment(SwingConstants.CENTER);
				// if (column == 0) {
				// editor.setHorizontalAlignment(SwingConstants.CENTER);
				// editor.setFont(new Font("Serif", Font.BOLD, 14));
				// } else {
				// editor.setHorizontalAlignment(SwingConstants.RIGHT);
				// editor.setFont(new Font("Serif", Font.ITALIC, 12));
				// }
				return editor;
			}
		};
	}

}
