package com.vv.minerlamp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "info_item")
public class InfoItem {
	private Long id;
	private String name;
	private Integer type;
	private String value;
	public static int EDUCATION_ITEM = 0;
	public static int WORK_TYPE_ITEM = 1;
	public static int DEPARTMENT_ITEM = 2;
	public static int CLAZZ_ITEM = 3;
	public static String EDUCATION_ITEM_DESC = "教育程度";
	public static String WORK_TYPE_ITEM_DESC = "工种";
	public static String DEPARTMENT_ITEM_DESC = "部门";
	
	@Id
	@GeneratedValue
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
