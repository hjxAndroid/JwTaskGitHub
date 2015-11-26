package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

import java.util.Date;

@Table(name="applydelay")
public class Applydelay{
	private int mid;
	/**
	 * 任务UUID
	 */
	private String task_code;
	/**
	 * 任务名称
	 */
	private String task_name;
	/**
	 * 任务完成情况和问题
	 */
	private String performance;
	/**
	 * 意见反馈
	 */
	private String feedback;
	/**
	 * 图片ID,预留字段
	 */
	private int photo_id;
	/**
	 * 图片ID,预留字段
	 */
	private String photo_code;
	/**
	 * 操作状态
	 */
	private String evaluate;
	/**
	 * 操作人状态
	 */
	private String operate_state;
	/**
	 * 操作人
	 */
	private int operator;
	/**
	 * 操作时间
	 */
	private Date operate_time;
	/**
	 * 操作类型，确认接受，递交审核，申请延期，泛起任务，同意任务，驳回，参与人评论
	 */
	private String operate_type;
	/**
	 * 评论内容,评论内容
	 */
	private String commit_content;
	/**
	 * 起始时间
	 */
	private String begin_time;
	/**
	 * 结束时间
	 */
	private String end_time;
	/**
	 * 申请延期结束时间
	 */
	private String apply_delay_endtime;
	/**
	 * 理由
	 */
	private String reason;
	/**
	 * 审批内容
	 */
	private String approval_content;
	/**
	 * 审核人自增ID
	 */
	private int auditor;
	/**
	 * 审核人32位ID
	 */
	private String auditor_id;

	/**
	 * 同意延期时间
	 */
	private String agree_delay_time;
	/**
	 * 同意状态（同意或者不同意）
	 */
	private String agree_state;
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

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getApply_delay_endtime() {
		return apply_delay_endtime;
	}

	public void setApply_delay_endtime(String apply_delay_endtime) {
		this.apply_delay_endtime = apply_delay_endtime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getApproval_content() {
		return approval_content;
	}

	public void setApproval_content(String approval_content) {
		this.approval_content = approval_content;
	}

	public int getAuditor() {
		return auditor;
	}

	public void setAuditor(int auditor) {
		this.auditor = auditor;
	}

	public String getAuditor_id() {
		return auditor_id;
	}

	public void setAuditor_id(String auditor_id) {
		this.auditor_id = auditor_id;
	}

	public String getAgree_delay_time() {
		return agree_delay_time;
	}

	public void setAgree_delay_time(String agree_delay_time) {
		this.agree_delay_time = agree_delay_time;
	}

	public String getAgree_state() {
		return agree_state;
	}

	public void setAgree_state(String agree_state) {
		this.agree_state = agree_state;
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

