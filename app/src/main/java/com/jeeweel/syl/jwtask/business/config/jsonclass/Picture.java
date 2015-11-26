package com.jeeweel.syl.jwtask.business.config.jsonclass;


import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

@Table(name="picture")
public class Picture{
	private int mid;
	private int out_id;
	private int pic_id;
	private String pic_code;
	private String pic_road;
	private String create_time;
	private String update_time;

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public int getOut_id() {
		return out_id;
	}

	public void setOut_id(int out_id) {
		this.out_id = out_id;
	}

	public int getPic_id() {
		return pic_id;
	}

	public void setPic_id(int pic_id) {
		this.pic_id = pic_id;
	}

	public String getPic_code() {
		return pic_code;
	}

	public void setPic_code(String pic_code) {
		this.pic_code = pic_code;
	}

	public String getPic_road() {
		return pic_road;
	}

	public void setPic_road(String pic_road) {
		this.pic_road = pic_road;
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

