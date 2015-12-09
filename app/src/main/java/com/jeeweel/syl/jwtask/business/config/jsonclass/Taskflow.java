package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;
import com.jeeweel.syl.lib.api.config.publicjsonclass.BaseItem;

/**
 * Created by asusa on 2015/12/9.
 */
@Table(name = "taskflow")
public class Taskflow extends BaseItem {
    /**
     * mid
     */
    @Id(column = "mid")
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


    private String user_code;

    private String user_name;

    private String nickname;

    /**
     * 所做操作
     */
    private String user_action;

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


    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_action() {
        return user_action;
    }

    public void setUser_action(String user_action) {
        this.user_action = user_action;
    }
}
