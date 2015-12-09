package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.lib.api.config.publicjsonclass.BaseItem;

/**
 * Created by asusa on 2015/12/9.
 */
public class Taskflow extends BaseItem {
    private int mid;
    /**
     * 任务code
     */
    private String task_code;
    /**
     * 当前状态
     */
    private int now_state;

    /**
     * 当前状态名
     */
    private String now_state_name;

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

    public int getNow_state() {
        return now_state;
    }

    public void setNow_state(int now_state) {
        this.now_state = now_state;
    }

    public String getNow_state_name() {
        return now_state_name;
    }

    public void setNow_state_name(String now_state_name) {
        this.now_state_name = now_state_name;
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
