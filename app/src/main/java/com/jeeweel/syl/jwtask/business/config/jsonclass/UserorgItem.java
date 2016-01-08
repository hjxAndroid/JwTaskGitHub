package com.jeeweel.syl.jwtask.business.config.jsonclass;

import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Id;
import com.jeeweel.syl.jcloudlib.db.annotation.sqlite.Table;

/**
 * @author 苏逸龙 317616660@qq.com
 * @ClassName: Alreadyread
 * @Description: TODO(用户与组织的关系表)
 * @date 2011-11-26 下午7:26:08
 */

@Table(name = "userorg")
public class UserorgItem {
    /**
     * mid
     */
    @Id(column = "mid")
    private int mid;
    /**
     * 组织代码
     */
    private String org_code;
    /**
     * 组织名称
     */
    private String org_name;
    /**
     * 预留字段
     */
    private int user_id;
    /**
     * 创建用户代码
     */
    private String user_code;
    /**
     * 创建用户名称
     */
    private String user_name;


    private String nickname;
    /**
     * 备注
     */
    private String remark;
    private String create_time;
    private String update_time;


    private String photo_code;
    private String pic_road;


    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto_code() {
        return photo_code;
    }

    public void setPhoto_code(String photo_code) {
        this.photo_code = photo_code;
    }

    public String getPic_road() {
        return pic_road;
    }

    public void setPic_road(String pic_road) {
        this.pic_road = pic_road;
    }
}

