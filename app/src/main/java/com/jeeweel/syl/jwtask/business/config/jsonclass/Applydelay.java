package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

import java.util.Date;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(申请延期表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "applydelay")
public class Applydelay {
    /**
     * mid
     */
    @Id(column = "mid")
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
     * 审核人code
     */
    private String auditor_code;
    /**
     * 负责人code
     */
    private String principal_code;
    /**
     * 申请人code
     */
    private String apply_user_code;
    /**
     * 申请人电话号码
     */
    private String apply_username;
    /**
     * 申请人昵称
     */
    private String apply_nickname;
    /**
     * 申请理由
     */
    private String apply_reason;
    /**
     * 申请延期时间
     */
    private String apply_delay_time;
    /**
     * 开始时间
     */
    private String begin_time;
    /**
     * 结束时间
     */
    private String over_time;
    /**
     * 创建时间
     */
    private String create_time;
    /**
     * 更新时间
     */
    private String update_time;

    private String confirm_time;
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

    public String getPrincipal_code() {
        return principal_code;
    }

    public void setPrincipal_code(String principal_code) {
        this.principal_code = principal_code;
    }

    public String getApply_user_code() {
        return apply_user_code;
    }

    public void setApply_user_code(String apply_user_code) {
        this.apply_user_code = apply_user_code;
    }

    public String getApply_username() {
        return apply_username;
    }

    public void setApply_username(String apply_username) {
        this.apply_username = apply_username;
    }

    public String getApply_nickname() {
        return apply_nickname;
    }

    public void setApply_nickname(String apply_nickname) {
        this.apply_nickname = apply_nickname;
    }

    public String getApply_reason() {
        return apply_reason;
    }

    public void setApply_reason(String apply_reason) {
        this.apply_reason = apply_reason;
    }

    public String getApply_delay_time() {
        return apply_delay_time;
    }

    public void setApply_delay_time(String apply_delay_time) {
        this.apply_delay_time = apply_delay_time;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getOver_time() {
        return over_time;
    }

    public void setOver_time(String over_time) {
        this.over_time = over_time;
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

    public String getConfirm_time() {
        return confirm_time;
    }

    public void setConfirm_time(String confirm_time) {
        this.confirm_time = confirm_time;
    }


    public String getAuditor_code() {
        return auditor_code;
    }

    public void setAuditor_code(String auditor_code) {
        this.auditor_code = auditor_code;
    }
}

