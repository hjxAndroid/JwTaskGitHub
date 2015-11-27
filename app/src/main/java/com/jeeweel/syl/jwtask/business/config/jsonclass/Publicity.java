package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;
/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name="publicity")
public class Publicity{
	/**
	 * mid
	 */
	@Id(column="mid")
	private int mid;
	private String publicity_code;
	private String publicity_title;
	private String witness;
	private int photo_id;
	private String photo_code;
	private int prouser_id;
	private String prouser;
	private String pro_time;
	private int accept_orgid;
	private String remark;
	private String create_time;
	private String update_time;

	public String getPhoto_code() {
		return photo_code;
	}

	public void setPhoto_code(String photo_code) {
		this.photo_code = photo_code;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getPublicity_code() {
		return publicity_code;
	}

	public void setPublicity_code(String publicity_code) {
		this.publicity_code = publicity_code;
	}

	public String getPublicity_title() {
		return publicity_title;
	}

	public void setPublicity_title(String publicity_title) {
		this.publicity_title = publicity_title;
	}

	public String getWitness() {
		return witness;
	}

	public void setWitness(String witness) {
		this.witness = witness;
	}

	public int getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(int photo_id) {
		this.photo_id = photo_id;
	}

	public int getProuser_id() {
		return prouser_id;
	}

	public void setProuser_id(int prouser_id) {
		this.prouser_id = prouser_id;
	}

	public String getProuser() {
		return prouser;
	}

	public void setProuser(String prouser) {
		this.prouser = prouser;
	}

	public String getPro_time() {
		return pro_time;
	}

	public void setPro_time(String pro_time) {
		this.pro_time = pro_time;
	}

	public int getAccept_orgid() {
		return accept_orgid;
	}

	public void setAccept_orgid(int accept_orgid) {
		this.accept_orgid = accept_orgid;
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

