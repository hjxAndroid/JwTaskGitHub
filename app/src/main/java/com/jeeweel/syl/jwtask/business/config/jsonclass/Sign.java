package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

@Table(name="sign")
public class Sign{
	private int mid;
	private String sign_code;
	private String sign_title;
	private int prouser_id;
	private String prouser_code;
	private String prouser_name;
	private String sign_time;
	private int receive_id;
	private String receive_code;
	private String receive_name;
	private String remark;
	private String create_time;
	private String update_time;

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getSign_code() {
		return sign_code;
	}

	public void setSign_code(String sign_code) {
		this.sign_code = sign_code;
	}

	public String getSign_title() {
		return sign_title;
	}

	public void setSign_title(String sign_title) {
		this.sign_title = sign_title;
	}

	public int getProuser_id() {
		return prouser_id;
	}

	public void setProuser_id(int prouser_id) {
		this.prouser_id = prouser_id;
	}

	public String getProuser_code() {
		return prouser_code;
	}

	public void setProuser_code(String prouser_code) {
		this.prouser_code = prouser_code;
	}

	public String getProuser_name() {
		return prouser_name;
	}

	public void setProuser_name(String prouser_name) {
		this.prouser_name = prouser_name;
	}

	public String getSign_time() {
		return sign_time;
	}

	public void setSign_time(String sign_time) {
		this.sign_time = sign_time;
	}

	public int getReceive_id() {
		return receive_id;
	}

	public void setReceive_id(int receive_id) {
		this.receive_id = receive_id;
	}

	public String getReceive_code() {
		return receive_code;
	}

	public void setReceive_code(String receive_code) {
		this.receive_code = receive_code;
	}

	public String getReceive_name() {
		return receive_name;
	}

	public void setReceive_name(String receive_name) {
		this.receive_name = receive_name;
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

