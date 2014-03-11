package com.vv.minerlamp.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sysinf")
public class SysInf {
	public static final int INIT_DATA_COUNT=3;
	public static final int CAT_USER_REGIST=1;
	public static final int CAT_LIMIT_COUNT =2;
	public static final int CAT_LIMIT_DAY=3;
	public static final int CAT_USED_COUNT=4;
	public static final int CAT_START_TIME=5;
	public static final int CAT_MACHINE_CODE=6;
	public static final int CAT_MACHINE__OLD_CODE=7; //�������ݿ��Ǵ���������������ԭ�����ģ��������������Ļ�����
	public static final int CAT_RIGIST_CODE=8; //ע����
	
	//begin AT_USER_REGISTED�Ŀ�ȡֵ
	public static final int VAL_TRY_COUNT=1;
	public static final int VAL_TRY_DAY=2;
	public static final int VAL_REGISTED=3;
	//end
	private Long id;
	private Integer cat;
	private Integer val;
	private String descr;
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getCat() {
		return cat;
	}
	public void setCat(Integer cat) {
		this.cat = cat;
	}
	public Integer getVal() {
		return val;
	}
	public void setVal(Integer val) {
		this.val = val;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
}
