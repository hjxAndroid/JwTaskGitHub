package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * friend ʵ����
 * Thu Nov 26 17:04:16 CST 2015 ������ �� GenEntityMysql�Զ�����
 */

@Table(name="friend")
public class Friend {
    private int mid;
    private int user_code;
    private String user_name;
    private String content;
    private int friend_id;
    private String friend_name;
    private String state;
    private String remark;
    private String create_time;
    private String update_time;

    public void setMid(int mid) {
        this.mid = mid;
    }

    public void setUser_code(int user_code) {
        this.user_code = user_code;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}

