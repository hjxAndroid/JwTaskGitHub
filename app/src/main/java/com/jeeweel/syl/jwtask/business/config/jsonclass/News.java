package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

@Table(name="news")
public class News{
	private int mid;
	private String msg_code;
	private String msg_type;
	private String latest_msg;
	private String remark;
	private String create_time;
	private String update_time;

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getMsg_code() {
		return msg_code;
	}

	public void setMsg_code(String msg_code) {
		this.msg_code = msg_code;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public String getLatest_msg() {
		return latest_msg;
	}

	public void setLatest_msg(String latest_msg) {
		this.latest_msg = latest_msg;
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

