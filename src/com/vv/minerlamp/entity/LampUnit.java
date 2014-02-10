package com.vv.minerlamp.entity;

import java.util.Date;

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

	private String name;
	private Integer state;
	private Integer chargingCounts;
	private Date chargingBeginTime; //电池首次使用时间 并非每次充电开始时间
	private Date chargingLastTime;
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
	public Date getChargingBeginTime() {
		return chargingBeginTime;
	}

	public void setChargingBeginTime(Date chargingBeginTime) {
		this.chargingBeginTime = chargingBeginTime;
	}
	@Column(name="charging_lasttime")
	public Date getChargingLastTime() {
		return chargingLastTime;
	}

	public void setChargingLastTime(Date chargingLastTime) {
		this.chargingLastTime = chargingLastTime;
	}
	@Column(name="manufacturer")
	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

}
