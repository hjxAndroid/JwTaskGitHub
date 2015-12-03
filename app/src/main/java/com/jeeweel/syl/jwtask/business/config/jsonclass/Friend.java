package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;
/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(好友表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name="friend")
public class Friend {
    /**
     * mid
     */
    @Id(column="mid")
    private int mid;

    /**
     * 用于两张表相同id，好用于搜索
     */
    private String unid_code;

    /**
     * 用户id
     */
    private String user_id;
    /**
     * 用户代码
     */
    private String user_code;
    /**
     * 用户名称
     */
    private String user_name;
    /**
     * 内容
     */
    private String content;
    /**
     * 预留字段
     */
    private String friend_id;
    /**
     * 朋友用户代码
     */
    private String friend_code;
    /**
     * 朋友用户名称
     */
    private String friend_name;
    /**
     * 状态
     */
    private int state;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private String create_time;
    /**
     * 更新代码
     */
    private String update_time;

    /**
     * 用户昵称
     * @return
     */
    private String user_nickname;

    /**
     * 好友昵称
     * @return
     */
    private String friend_nickname;

    /**
     * 发送状态
     * @return
     */
    private int send_state;


    /**
     * 存储是否已经选中
     * @return
     */
    private String choose = "0";

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFriend_code() {
        return friend_code;
    }

    public void setFriend_code(String friend_code) {
        this.friend_code = friend_code;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getUnid_code() {
        return unid_code;
    }

    public void setUnid_code(String unid_code) {
        this.unid_code = unid_code;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getFriend_nickname() {
        return friend_nickname;
    }

    public void setFriend_nickname(String friend_nickname) {
        this.friend_nickname = friend_nickname;
    }

    public int getSend_state() {
        return send_state;
    }

    public void setSend_state(int send_state) {
        this.send_state = send_state;
    }

    public String getChoose() {
        return choose;
    }

    public void setChoose(String choose) {
        this.choose = choose;
    }
}

