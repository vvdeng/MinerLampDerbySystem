package com.vv.minerlamp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "usr")
public class User {
	private Long id;
	private String name;
	private String pwd;
	private Integer priv;
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
	@Column(name = "pwd", nullable = false) 
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Integer getPriv() {
		return priv;
	}
	public void setPriv(Integer priv) {
		this.priv = priv;
	}
	@Override
	public String toString() {
		return name;
	}
	public static final int READ_ONLY=0;
	public static final int READ_WRITE=1;
}
