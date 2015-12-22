package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(签到明细表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "signed")
public class Signed {
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 签到代码
     */
    private String task_code;
    /**
     * 签到标题
     */
    private String sign_title;
    /**
     * 预留字段
     */
    private int sign_id;
    /**
     * 签到时间为以下的Create_time,为预留字段
     */
    private String sign_time;
    /**
     * LBS地址 经度
     */
    private double longtude;
    /**
     * LBS地址 纬度
     */
    private double latitude;
    /**
     * 定位,经纬度转地址
     */
    private String location;
    /**
     * 签到消息
     */
    private String sign_msg;
    /**
     * 签到code
     */
    private String sign_code;
    /**
     * 签到人code
     */
    private String sign_user_code;
    /**
     * 签到人昵称
     */
    private String nickname;
    /**
     * 备注
     */
    private String remark;
    private String create_time;
    private String update_time;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getSign_title() {
        return sign_title;
    }

    public void setSign_title(String sign_title) {
        this.sign_title = sign_title;
    }

    public int getSign_id() {
        return sign_id;
    }

    public void setSign_id(int sign_id) {
        this.sign_id = sign_id;
    }

    public String getSign_time() {
        return sign_time;
    }

    public void setSign_time(String sign_time) {
        this.sign_time = sign_time;
    }

    public double getLongtude() {
        return longtude;
    }

    public void setLongtude(double longtude) {
        this.longtude = longtude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSign_code(String sign_code) {
        this.sign_code = sign_code;
    }

    public String getSign_code() {
        return sign_code;
    }

    public String getSign_user_code() {
        return sign_user_code;
    }

    public void setSign_user_code(String sign_user_code) {
        this.sign_user_code = sign_user_code;
    }

    public String getSign_msg() {
        return sign_msg;
    }

    public void setSign_msg(String sign_msg) {
        this.sign_msg = sign_msg;
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
}

