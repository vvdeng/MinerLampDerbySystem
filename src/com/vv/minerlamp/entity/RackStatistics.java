package com.vv.minerlamp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rack_statistics")
public class RackStatistics {
	private Long id;
	private Long idleCount;
	private Long chargingCount;
	private Long undergroundCount;
	private Long errorCount;
	private Long fullCount;

	public RackStatistics() {
		idleCount = 0L;
		chargingCount = 0L;
		undergroundCount = 0L;
		errorCount = 0L;
		fullCount = 0L;
	}

	@Id
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "idle_count")
	public Long getIdleCount() {
		return idleCount;
	}

	public void setIdleCount(Long idleCount) {
		this.idleCount = idleCount;
	}

	@Column(name = "charging_count")
	public Long getChargingCount() {
		return chargingCount;
	}

	public void setChargingCount(Long chargingCount) {
		this.chargingCount = chargingCount;
	}

	@Column(name = "underground_count")
	public Long getUndergroundCount() {
		return undergroundCount;
	}

	public void setUndergroundCount(Long undergroundCount) {
		this.undergroundCount = undergroundCount;
	}

	@Column(name = "error_count")
	public Long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}

	@Column(name = "full_count")
	public Long getFullCount() {
		return fullCount;
	}

	public void setFullCount(Long fullCount) {
		this.fullCount = fullCount;
	}

	public void addChargingCount(Long value) {
		this.chargingCount += value;
	}

	public void minusChargingCount(Long value) {
		this.chargingCount -= value;
	}

	public void addUndergroundCount(Long value) {
		this.undergroundCount += value;
	}

	public void minusUndergroundCount(Long value) {
		this.undergroundCount -= value;
	}

	public void addIdleCount(Long value) {
		this.idleCount += value;
	}

	public void minusIdleCount(Long value) {
		this.idleCount -= value;
	}

	public void addErrorCount(Long value) {
		this.errorCount += value;
	}

	public void minusErrorCount(Long value) {
		this.errorCount -= value;
	}

	public void addFullCount(Long value) {
		this.fullCount += value;
	}

	public void minusFullCount(Long value) {
		this.fullCount -= value;
	}
}
