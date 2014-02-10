package com.vv.minerlamp.entity;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "staff")
public class Staff {
	private Long id;
	private Long workId;
	private String name;
	private Integer sex;
	private String birthDate;
	private String certificateNo;
	private String eduLevel;
	private String phone;
	private String address;
	private Long rackId;
	private Long lampNo;
	private Long professionId;
	private String profession;
	private Long departmentId;
	private String department;
	private Long clazzId;
	private String clazz;
	private Blob image;
	private Date chargingLastTime; //命名有误，实际是准备下井时间，而不是开始充电时间
//	public static final int BATTERY_NOTUSE = 0;
//	public static final int BATTERY_UNDERGROUND = 1;
//	public static final int BATTERY_CHARGING = 2;
//	public static final int BATTERY_FULL = 3;
//	public static final int BATTERY_ERROR = 4;
//
//	public static final int BATTERY_ACTION_NEW = 0;
//	public static final int BATTERY_ACTION_EDIT = 1;
//	public static final int BATTERY_ACTION_DELETE = 2;

	public static final int STAFF_MODEL_TYPE_1 = 1;
	public static final int STAFF_MODEL_TYPE_2 = 2;

	
	public Staff() {

	}

	public Staff(Long workId, String name, int sex, String birthDate,
			String certificateNo, String eduLevel, String phone,
			String address, Long rackId, Long lampNo, Long professionId,
			String profession, Long departmentId, String department,
			Long clazzId, String clazz, Blob image) {
		super();
		this.workId = workId;
		this.name = name;
		this.sex = sex;
		this.birthDate = birthDate;
		this.certificateNo = certificateNo;
		this.eduLevel = eduLevel;
		this.phone = phone;
		this.address = address;
		this.rackId = rackId;
		this.lampNo = lampNo;
		this.professionId = professionId;
		this.profession = profession;
		this.departmentId = departmentId;
		this.department = department;
		this.clazzId = clazzId;
		this.clazz = clazz;
		this.image = image;
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

	@Column(name = "sex")
	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	@Column(name = "birth_date")
	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	@Column(name = "certificate_no")
	public String getCertificateNo() {
		return certificateNo;
	}

	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}

	@Column(name = "edu_level")
	public String getEduLevel() {
		return eduLevel;
	}

	public void setEduLevel(String eduLevel) {
		this.eduLevel = eduLevel;
	}

	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	@Column(name = "profession_id")
	public Long getProfessionId() {
		return professionId;
	}

	public void setProfessionId(Long professionId) {
		this.professionId = professionId;
	}

	@Column(name = "profession")
	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	@Column(name = "department_id")
	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
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

	@Column(name = "image")
	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	@Column(name = "charging_lasttime")
	public Date getChargingLastTime() {
		return chargingLastTime;
	}

	public void setChargingLastTime(Date chargingLastTime) {
		this.chargingLastTime = chargingLastTime;
	}

	@Override
	public String toString() {
		return name;
	}

}
