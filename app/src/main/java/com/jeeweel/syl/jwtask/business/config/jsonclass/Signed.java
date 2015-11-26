package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

import java.util.Date;

@Table(name="signed")
public class Signed{
	private int mid;
	private String task_code;
	private String sign_title;
	private int sign_id;
	private Date sign_time;
	private double longtude;
	private double latitude;
	private String location;
	private String sign_msg;
	private String remark;
	private String create_time;
	private String update_time;

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getTask_code() {
		return task_code;
	}

	public void setTask_code(String task_code) {
		this.task_code = task_code;
	}

	public String getSign_title() {
		return sign_title;
	}

	public void setSign_title(String sign_title) {
		this.sign_title = sign_title;
	}

	public int getSign_id() {
		return sign_id;
	}

	public void setSign_id(int sign_id) {
		this.sign_id = sign_id;
	}

	public Date getSign_time() {
		return sign_time;
	}

	public void setSign_time(Date sign_time) {
		this.sign_time = sign_time;
	}

	public double getLongtude() {
		return longtude;
	}

	public void setLongtude(double longtude) {
		this.longtude = longtude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSign_msg() {
		return sign_msg;
	}

	public void setSign_msg(String sign_msg) {
		this.sign_msg = sign_msg;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
}

