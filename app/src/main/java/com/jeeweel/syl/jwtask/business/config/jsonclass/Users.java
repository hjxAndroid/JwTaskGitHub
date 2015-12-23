package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

import api.util.Utils;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(用户表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "users")
public class Users {
    /**
     * mid
     */
    @Id(column = "mid")
    @net.tsz.afinal.annotation.sqlite.Id(column = "mid")
    private int mid;
    private String user_code;
    private String username;
    private String password;
    private String nickname;
    private String sex;
    private String birthday;
    private String area;
    private String strong_point;
    private String sign;
    private int photo_id;
    private String photo_code;
    private String profession;
    private String remark;
    private String create_time;
    private String update_time;
    private String email;
    private String device_token;
    private String pic_road;
    private String pic_exists;

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

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStrong_point() {
        return strong_point;
    }

    public void setStrong_point(String strong_point) {
        this.strong_point = strong_point;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getPic_road() {
        return pic_road;
    }

    public void setPic_road(String pic_road) {
        this.pic_road = Utils.getPicUrl()+pic_road;
    }

    public String isPic_exists() {
        return pic_exists;
    }

    public void setPic_exists(String pic_exists) {
        this.pic_exists = pic_exists;
    }
}

