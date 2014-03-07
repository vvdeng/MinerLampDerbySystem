package com.vv.minerlamp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "lamp_unit")
@IdClass(LampUnitPK.class)
public class LampUnit {
	
	private Long lampNo;

	private Long rackId;
	private String staffName;
	private String staffDepart;
	private String staffWorkType;
	private String name;
	private Integer state;
	private Integer chargingCounts;
	private Long chargingBeginTime; //电池首次使用时间 并非每次充电开始时间
	private Long chargingLastTime;  //每次充电开始时间
	private Long chargingTotalTime; //充电总时长
	private String manufacturer;
	@Id
	@Column(name = "lamp_no")
	public Long getLampNo() {
		return lampNo;
	}

	public void setLampNo(Long lampNo) {
		this.lampNo = lampNo;
	}
	@Id
	@Column(name = "rack_id")
	public Long getRackId() {
		return rackId;
	}

	public void setRackId(Long rackId) {
		this.rackId = rackId;
	}
	@Column(name = "staff_name")
	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	@Column(name = "staff_depart")
	public String getStaffDepart() {
		return staffDepart;
	}

	public void setStaffDepart(String staffDepart) {
		this.staffDepart = staffDepart;
	}
	@Column(name = "staff_worktype")
	public String getStaffWorkType() {
		return staffWorkType;
	}

	public void setStaffWorkType(String staffWorkType) {
		this.staffWorkType = staffWorkType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
  @Column(name="charging_counts")
	public Integer getChargingCounts() {
		return chargingCounts;
	}

	public void setChargingCounts(Integer chargingCounts) {
		this.chargingCounts = chargingCounts;
	}
	@Column(name="charging_begintime")
	public Long getChargingBeginTime() {
		return chargingBeginTime;
	}

	public void setChargingBeginTime(Long chargingBeginTime) {
		this.chargingBeginTime = chargingBeginTime;
	}
	@Column(name="charging_lasttime")
	public Long getChargingLastTime() {
		return chargingLastTime;
	}

	public void setChargingLastTime(Long chargingLastTime) {
		this.chargingLastTime = chargingLastTime;
	}
	@Column(name="charging_total_time")
	public Long getChargingTotalTime() {
		return chargingTotalTime;
	}

	public void setChargingTotalTime(Long chargingTotalTime) {
		this.chargingTotalTime = chargingTotalTime;
	}

	@Column(name="manufacturer")
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

}
