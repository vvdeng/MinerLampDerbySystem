package com.vv.minerlamp.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lamp_change_log")
public class LampChangeLog {
	private Long Id;
	private Long rackId;
	private Long lampNo;
	private Long workId;
	private String staffName;
	private Date changeTime;
	private Integer operation;

	@Id    
	@GeneratedValue
	@Column(name = "ID") 
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	@Column(name = "rack_id") 
	public Long getRackId() {
		return rackId;
	}
	public void setRackId(Long rackId) {
		this.rackId = rackId;
	}
	@Column(name = "lamp_no") 
	public Long getLampNo() {
		return lampNo;
	}
	public void setLampNo(Long lampNo) {
		this.lampNo = lampNo;
	}
	@Column(name = "work_id") 
	public Long getWorkId() {
		return workId;
	}
	public void setWorkId(Long workId) {
		this.workId = workId;
	}
	@Column(name = "staff_name") 
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	@Column(name = "change_time") 
	public Date getChangeTime() {
		return changeTime;
	}
	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}
	@Column(name = "operation") 
	public Integer getOperation() {
		return operation;
	}
	public void setOperation(Integer operation) {
		this.operation = operation;
	}
	public static final int OPERATION_CHANGE=1;
	public static final int OPERATION_ADD=2;
}
