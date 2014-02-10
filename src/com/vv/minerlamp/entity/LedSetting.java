package com.vv.minerlamp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "led_setting")
public class LedSetting {
	private Long id;
	private String content;
	private Integer display;

	@Id        
	@Column(name = "ID") 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "content", nullable = false) 
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Column(name = "display", nullable = false) 
	public Integer getDisplay() {
		return display;
	}
	public void setDisplay(Integer display) {
		this.display = display;
	}

	@Override
	public String toString() {
		return content;
	}
	public static final int NO_DISPLAY=0;
	public static final int DISPLAY=1;
}
