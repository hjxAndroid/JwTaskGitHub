package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(递交审核表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "submit")
public class Submit {
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 任务ID
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
     * 意见建议
     */
    private String feedback;
    /**
     * 预留字段
     */
    private int photo_id;
    /**
     * 图片代码
     */
    private String photo_code;
    /**
     * 自我评价(优秀，良好，差，未完成)
     */
    private String evaluate;
    /**
     * 操作人状态
     */
    private String operate_state;
    /**
     * 操作人ID 预留字段
     */
    private int operator;
    /**
     * 操作人代码
     */
    private String operator_code;
    /**
     * create_time
     */
    private String operate_time;
    /**
     * 操作类型（审核状态，同意，驳回等状态）
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
    private String create_time;
    private String update_time;

    /**
     * 审核评价
     */
    private String audit_content;
    /**
     * 审核结论
     */
    private String audit_evaluate;

    /**
     * 审核总分
     */
    private String score;

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

    public String getPhoto_code() {
        return photo_code;
    }

    public void setPhoto_code(String photo_code) {
        this.photo_code = photo_code;
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

    public String getOperator_code() {
        return operator_code;
    }

    public void setOperator_code(String operator_code) {
        this.operator_code = operator_code;
    }

    public String getOperate_time() {
        return operate_time;
    }

    public void setOperate_time(String operate_time) {
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


    public String getAudit_content() {
        return audit_content;
    }

    public void setAudit_content(String audit_content) {
        this.audit_content = audit_content;
    }

    public String getAudit_evaluate() {
        return audit_evaluate;
    }

    public void setAudit_evaluate(String audit_evaluate) {
        this.audit_evaluate = audit_evaluate;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}

