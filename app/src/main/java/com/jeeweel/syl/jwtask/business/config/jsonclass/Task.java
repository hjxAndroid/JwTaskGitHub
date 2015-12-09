package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(任务表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "task")
public class Task {
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 任务代码
     */
    private String task_code;
    /**
     * 任务名称
     */
    private String task_name;
    /**
     * 预留组织ID
     */
    private int org_id;
    /**
     * 预留组织名称
     */
    private String org_name;
    /**
     * 起始时间
     */
    private String begin_time;
    /**
     * 结束时间
     */
    private String over_time;
    /**
     * 预留字段
     */
    private String principal;
    /**
     * 负责人代码（过个，号隔开）
     */
    private String principal_code;
    /**
     * 发布人预留字段
     */
    private int promulgator;
    /**
     * 发布人代码
     */
    private String promulgator_code;
    /**
     * 发布人电话号码
     */
    private String promulgator_name;
    /**
     * 发布人姓名
     */
    private String nickname;
    /**
     * 审核人预留ID
     */
    private int auditor;
    /**
     * 审核人代码
     */
    private String auditor_code;
    /**
     * 参与者字段,预留
     */
    private String participant;
    /**
     * 参与者代码
     */
    private String participant_code;
    /**
     * 预留字段
     */
    private String observer;
    /**
     * 观察者（多个）预留字段
     */
    private String observer_code;
    /**
     * 任务要求
     */
    private String task_request;
    /**
     * 优先级
     */
    private String priority;
    /**
     * 考核标准
     */
    private String assess_standard;
    /**
     * 目前状态
     */
    private int now_state;
    /**
     * 图片代码
     */
    private String photo_code;
    /**
     * 创建时间
     */
    private String create_time;
    /**
     * 更新时间
     */
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

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
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

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPrincipal_code() {
        return principal_code;
    }

    public void setPrincipal_code(String principal_code) {
        this.principal_code = principal_code;
    }

    public int getPromulgator() {
        return promulgator;
    }

    public void setPromulgator(int promulgator) {
        this.promulgator = promulgator;
    }

    public String getPromulgator_code() {
        return promulgator_code;
    }

    public void setPromulgator_code(String promulgator_code) {
        this.promulgator_code = promulgator_code;
    }

    public int getAuditor() {
        return auditor;
    }

    public void setAuditor(int auditor) {
        this.auditor = auditor;
    }

    public String getAuditor_code() {
        return auditor_code;
    }

    public void setAuditor_code(String auditor_code) {
        this.auditor_code = auditor_code;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getParticipant_code() {
        return participant_code;
    }

    public void setParticipant_code(String participant_code) {
        this.participant_code = participant_code;
    }

    public String getObserver() {
        return observer;
    }

    public void setObserver(String observer) {
        this.observer = observer;
    }

    public String getObserver_code() {
        return observer_code;
    }

    public void setObserver_code(String observer_code) {
        this.observer_code = observer_code;
    }

    public String getTask_request() {
        return task_request;
    }

    public void setTask_request(String task_request) {
        this.task_request = task_request;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssess_standard() {
        return assess_standard;
    }

    public void setAssess_standard(String assess_standard) {
        this.assess_standard = assess_standard;
    }

    public int getNow_state() {
        return now_state;
    }

    public void setNow_state(int now_state) {
        this.now_state = now_state;
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

    public String getPromulgator_name() {
        return promulgator_name;
    }

    public void setPromulgator_name(String promulgator_name) {
        this.promulgator_name = promulgator_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

