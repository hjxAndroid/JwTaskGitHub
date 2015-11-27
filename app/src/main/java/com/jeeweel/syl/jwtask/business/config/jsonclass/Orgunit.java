package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;
/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(组织表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name="orgunit")
public class Orgunit{
	/**
	 * mid
	 */
	@Id(column="mid")
	private int mid;
	private String org_code;
	private String org_name;
	private String org_state;
	private String remark;
	private String founder;
	private String founder_time;
	private String update_time;
	private String create_time;

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getOrg_code() {
		return org_code;
	}

	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public String getOrg_state() {
		return org_state;
	}

	public void setOrg_state(String org_state) {
		this.org_state = org_state;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFounder() {
		return founder;
	}

	public void setFounder(String founder) {
		this.founder = founder;
	}

	public String getFounder_time() {
		return founder_time;
	}

	public void setFounder_time(String founder_time) {
		this.founder_time = founder_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
}

