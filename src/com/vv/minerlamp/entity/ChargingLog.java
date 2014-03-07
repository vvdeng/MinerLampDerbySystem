package com.vv.minerlamp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "charging_log")
public class ChargingLog {
	private Long id;
	private Long workId;
	private String name;

	private Long rackId;
	private Long lampNo;
	private String profession;
	private String department;
	private Long clazzId;
	private String clazz;

	private Long UndergroundBeginTime;
	private Long UndergroundEndTime;
	private Integer oldState;
	private Integer newState;
	private Integer action;
	private String description;
	private Long lastUpdateTime;
	private String clazzDay;

	public ChargingLog() {

	}

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

	@Column(name = "work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
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

	@Column(name = "profession")
	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	@Column(name = "department")
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Column(name = "clazz_id")
	public Long getClazzId() {
		return clazzId;
	}

	public void setClazzId(Long clazzId) {
		this.clazzId = clazzId;
	}

	@Column(name = "clazz")
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	@Column(name = "underground_begintime")
	public Long getUndergroundBeginTime() {
		return UndergroundBeginTime;
	}

	public void setUndergroundBeginTime(Long undergroundBeginTime) {
		UndergroundBeginTime = undergroundBeginTime;
	}

	@Column(name = "underground_endtime")
	public Long getUndergroundEndTime() {
		return UndergroundEndTime;
	}

	public void setUndergroundEndTime(Long undergroundEndTime) {
		UndergroundEndTime = undergroundEndTime;
	}

	@Column(name = "old_state")
	public Integer getOldState() {
		return oldState;
	}

	public void setOldState(Integer oldState) {
		this.oldState = oldState;
	}

	@Column(name = "new_state")
	public Integer getNewState() {
		return newState;
	}

	public void setNewState(Integer newState) {
		this.newState = newState;
	}

	@Column(name = "action")
	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "last_update_time")
	public Long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Column(name = "clazz_day")
	public String getClazzDay() {
		return clazzDay;
	}

	public void setClazzDay(String clazzDay) {
		this.clazzDay = clazzDay;
	}

	@Override
	public String toString() {
		return name;
	}

}
