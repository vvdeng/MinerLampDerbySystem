package com.vv.minerlamp.entity;

import java.io.Serializable;

public class LampUnitPK implements Serializable {
	private Long rackId;
	private Long lampNo;

	public Long getRackId() {
		return rackId;
	}

	public void setRackId(Long rackId) {
		this.rackId = rackId;
	}

	public Long getLampNo() {
		return lampNo;
	}

	public void setLampNo(Long lampNo) {
		this.lampNo = lampNo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LampUnitPK other = (LampUnitPK) obj;
		if ((this.rackId == null) ? (other.rackId != null) : !this.rackId
				.equals(other.rackId)) {
			return false;
		}
		if ((this.lampNo == null) ? (other.lampNo != null) : !this.lampNo
				.equals(other.lampNo)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 73 * hash + (this.rackId != null ? this.rackId.hashCode() : 0);
		hash = 73 * hash + (this.lampNo != null ? this.lampNo.hashCode() : 0);
		return hash;
	}
}