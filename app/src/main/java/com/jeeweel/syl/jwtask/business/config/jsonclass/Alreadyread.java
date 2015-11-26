package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

import java.util.Date;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(字符串获取和处理的类)
 * @date 2011-11-26 下午7:26:08
 */
@Table(name="alreadyread")
public class Alreadyread{
	/**
	 * mid
	 */
	private String mid;
	/**
	 * 已读任务或者公告ID
	 */
	private String task_id;
	/**
	 * 已读任务或者公告名称
	 */
	private String task_name;
	/**
	 *
	 */
	private String performance;
	/**
	 *
	 */
	private String feedback;
	/**
	 *
	 */
	private int photo_id;

	/**
	 *
	 */
	private String photo_code;
	/**
	 *
	 */
	private String evaluate;
	/**
	 * 操作状态
	 */
	private String operate_state;
	/**
	 *	操作人ID
	 */
	private int operator;
	/**
	 * 操作时间
	 */
	private Date operate_time;
	/**
	 * 操作类型
	 */
	private String operate_type;
	/**
	 * 评论内容
	 */
	private String commit_content;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 创建时间
	 */
	private String create_time;
	/**
	 * 更新时间
	 */
	private String update_time;


	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public String getPerformance() {
		return performance;
	}

	public void setPerformance(String performance) {
		this.performance = performance;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public int getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(int photo_id) {
		this.photo_id = photo_id;
	}

	public String getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(String evaluate) {
		this.evaluate = evaluate;
	}

	public String getOperate_state() {
		return operate_state;
	}

	public void setOperate_state(String operate_state) {
		this.operate_state = operate_state;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	public Date getOperate_time() {
		return operate_time;
	}

	public void setOperate_time(Date operate_time) {
		this.operate_time = operate_time;
	}

	public String getOperate_type() {
		return operate_type;
	}

	public void setOperate_type(String operate_type) {
		this.operate_type = operate_type;
	}

	public String getCommit_content() {
		return commit_content;
	}

	public void setCommit_content(String commit_content) {
		this.commit_content = commit_content;
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

	public String getPhoto_code() {
		return photo_code;
	}

	public void setPhoto_code(String photo_code) {
		this.photo_code = photo_code;
	}
}

